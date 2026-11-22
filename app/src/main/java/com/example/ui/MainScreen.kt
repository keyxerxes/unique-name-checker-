package com.example.ui

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.screens.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: MainViewModel) {
    val activeTab by viewModel.activeTab.collectAsState(initial = 0)
    val isFa by viewModel.isPersianLanguage.collectAsState(initial = true)
    val isChecking by viewModel.isChecking.collectAsState(initial = false)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = if (isFa) "نام‌یاب آیدی" else "UniqueUser",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                        Surface(
                            shape = MaterialTheme.shapes.extraSmall,
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Text(
                                text = "PRO",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                },
                actions = {
                    // Language Switcher Toggle Button
                    FilterChip(
                        selected = true,
                        onClick = { viewModel.toggleLanguage() },
                        label = {
                            Text(
                                text = if (isFa) "🇮🇷 فارسی" else "🇬🇧 EN",
                                fontWeight = FontWeight.Bold
                            )
                        },
                        modifier = Modifier.padding(end = 8.dp)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                windowInsets = WindowInsets.navigationBars,
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                NavigationBarItem(
                    selected = activeTab == 0,
                    onClick = { viewModel.setTab(0) },
                    icon = { Icon(Icons.Default.AutoAwesome, contentDescription = null) },
                    label = { Text(if (isFa) "پیشنهادات" else "Ideas") },
                    modifier = Modifier.testTag("tab_ideas")
                )
                NavigationBarItem(
                    selected = activeTab == 1,
                    onClick = { viewModel.setTab(1) },
                    icon = {
                        BadgedBox(badge = {
                            if (isChecking) {
                                Badge { Text("...") }
                            }
                        }) {
                            Icon(Icons.Default.Dashboard, contentDescription = null)
                        }
                    },
                    label = { Text(if (isFa) "پنل بررسی" else "Checker") },
                    modifier = Modifier.testTag("tab_checker")
                )
                NavigationBarItem(
                    selected = activeTab == 2,
                    onClick = { viewModel.setTab(2) },
                    icon = { Icon(Icons.Default.Search, contentDescription = null) },
                    label = { Text(if (isFa) "جستجوی دستی" else "Custom") },
                    modifier = Modifier.testTag("tab_custom")
                )
                NavigationBarItem(
                    selected = activeTab == 3,
                    onClick = { viewModel.setTab(3) },
                    icon = { Icon(Icons.Default.Star, contentDescription = null) },
                    label = { Text(if (isFa) "ذخیره‌شده‌ها" else "Saved") },
                    modifier = Modifier.testTag("tab_saved")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Crossfade(targetState = activeTab, label = "TabSwitch") { tab ->
                when (tab) {
                    0 -> QuestionnaireScreen(
                        viewModel = viewModel,
                        onCheckUsername = { username ->
                            viewModel.checkUsername(username)
                        }
                    )
                    1 -> LiveDashboardScreen(
                        viewModel = viewModel,
                        onNavigateToSearch = {
                            viewModel.setTab(2)
                        }
                    )
                    2 -> CustomSearchScreen(
                        viewModel = viewModel,
                        onNavigateToDashboard = {
                            viewModel.setTab(1)
                        }
                    )
                    3 -> FavoritesScreen(
                        viewModel = viewModel,
                        onCheckUsername = { username ->
                            viewModel.checkUsername(username)
                        }
                    )
                }
            }
        }
    }
}
