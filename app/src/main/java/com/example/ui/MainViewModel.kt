package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import com.example.data.db.AppDatabase
import com.example.data.db.SavedUsernameEntity
import com.example.service.PlatformChecker
import com.example.service.UsernameGenerator
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class AvailabilityStats(
    val total: Int = 0,
    val available: Int = 0,
    val taken: Int = 0,
    val restricted: Int = 0,
    val percentage: Int = 0
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val savedDao = db.savedUsernameDao()

    private val usernameGenerator = UsernameGenerator()
    private val platformChecker = PlatformChecker()

    // 1. Language Toggle
    private val _isPersianLanguage = MutableStateFlow(true)
    val isPersianLanguage: StateFlow<Boolean> = _isPersianLanguage.asStateFlow()

    // 2. Active Screen Tab (0: Questionnaire & Ideas, 1: Multi-Platform Dashboard, 2: Custom Search, 3: Saved/Favorites)
    private val _activeTab = MutableStateFlow(0)
    val activeTab: StateFlow<Int> = _activeTab.asStateFlow()

    // 3. User Questionnaire Form State
    private val _questionnaire = MutableStateFlow(UserQuestionnaire())
    val questionnaire: StateFlow<UserQuestionnaire> = _questionnaire.asStateFlow()

    // 4. Generated Suggestions
    private val _suggestions = MutableStateFlow<List<String>>(emptyList())
    val suggestions: StateFlow<List<String>> = _suggestions.asStateFlow()

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    // 5. Custom Username Entry
    private val _searchUsernameInput = MutableStateFlow("")
    val searchUsernameInput: StateFlow<String> = _searchUsernameInput.asStateFlow()

    // 6. Live Checking State
    private val _checkingUsername = MutableStateFlow("")
    val checkingUsername: StateFlow<String> = _checkingUsername.asStateFlow()

    private val _platformResultsMap = MutableStateFlow<Map<String, PlatformCheckResult>>(emptyMap())

    private val _isChecking = MutableStateFlow(false)
    val isChecking: StateFlow<Boolean> = _isChecking.asStateFlow()

    // 7. Dashboard Filters
    private val _selectedCategoryFilter = MutableStateFlow(PlatformCategory.ALL)
    val selectedCategoryFilter: StateFlow<PlatformCategory> = _selectedCategoryFilter.asStateFlow()

    private val _selectedStatusFilter = MutableStateFlow<CheckStatus?>(null)
    val selectedStatusFilter: StateFlow<CheckStatus?> = _selectedStatusFilter.asStateFlow()

    // Combined Filtered Platform Results
    val filteredPlatformResults: StateFlow<List<PlatformCheckResult>> = combine(
        _platformResultsMap,
        _selectedCategoryFilter,
        _selectedStatusFilter
    ) { resultsMap: Map<String, PlatformCheckResult>, catFilter: PlatformCategory, statusFilter: CheckStatus? ->
        resultsMap.values.filter { res ->
            val matchesCategory = (catFilter == PlatformCategory.ALL || res.platform.category == catFilter)
            val matchesStatus = (statusFilter == null || res.status == statusFilter)
            matchesCategory && matchesStatus
        }.sortedBy { res -> res.platform.name }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Availability Statistics
    val availabilityStats: StateFlow<AvailabilityStats> = _platformResultsMap.map { map: Map<String, PlatformCheckResult> ->
        val list = map.values
        val total = list.size
        val available = list.count { it.status == CheckStatus.AVAILABLE }
        val taken = list.count { it.status == CheckStatus.TAKEN }
        val restricted = list.count { it.status == CheckStatus.RESTRICTED }
        val percent = if (total > 0) (available * 100) / total else 0
        AvailabilityStats(total, available, taken, restricted, percent)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AvailabilityStats())

    // 8. Room Database Saved Usernames
    val savedUsernames: StateFlow<List<SavedUsernameEntity>> = savedDao.getAllSaved()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private var checkJob: Job? = null

    init {
        // Initial suggestion generation
        generateSuggestions()
    }

    fun toggleLanguage() {
        _isPersianLanguage.value = !_isPersianLanguage.value
    }

    fun setTab(index: Int) {
        _activeTab.value = index
    }

    fun updateQuestionnaire(newQ: UserQuestionnaire) {
        _questionnaire.value = newQ
    }

    fun generateSuggestions() {
        viewModelScope.launch {
            _isGenerating.value = true
            val list = usernameGenerator.generateSuggestions(_questionnaire.value)
            _suggestions.value = list
            _isGenerating.value = false
        }
    }

    fun updateSearchInput(text: String) {
        _searchUsernameInput.value = text
    }

    fun checkUsername(username: String) {
        val cleanUser = username.trim().lowercase().replace(" ", "")
        if (cleanUser.isEmpty()) return

        _checkingUsername.value = cleanUser
        _activeTab.value = 1 // Switch to dashboard

        checkJob?.cancel()
        checkJob = viewModelScope.launch {
            _isChecking.value = true
            _platformResultsMap.value = emptyMap()

            platformChecker.checkUsernameRealtimeFlow(cleanUser).collect { result ->
                val currentMap = _platformResultsMap.value.toMutableMap()
                currentMap[result.platform.id] = result
                _platformResultsMap.value = currentMap
            }

            _isChecking.value = false
        }
    }

    fun setCategoryFilter(category: PlatformCategory) {
        _selectedCategoryFilter.value = category
    }

    fun setStatusFilter(status: CheckStatus?) {
        _selectedStatusFilter.value = status
    }

    fun toggleFavorite(username: String) {
        viewModelScope.launch {
            val stats = availabilityStats.value
            val isAlreadySaved = savedDao.isSaved(username)
            if (isAlreadySaved) {
                savedDao.deleteByUsername(username)
            } else {
                savedDao.saveUsername(
                    SavedUsernameEntity(
                        username = username,
                        availabilityScore = stats.percentage,
                        totalChecked = stats.total,
                        availableCount = stats.available,
                        categoryVibe = _questionnaire.value.selectedVibe.titleFa
                    )
                )
            }
        }
    }

    fun isSaved(username: String): Flow<Boolean> = flow {
        emit(savedDao.isSaved(username))
    }
}
