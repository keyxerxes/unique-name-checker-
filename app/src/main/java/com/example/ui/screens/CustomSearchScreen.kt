package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.ui.MainViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CustomSearchScreen(
    viewModel: MainViewModel,
    onNavigateToDashboard: () -> Unit
) {
    val searchInput by viewModel.searchUsernameInput.collectAsState(initial = "")
    val isFa by viewModel.isPersianLanguage.collectAsState(initial = true)

    val quickIdeaChips = listOf(
        "cyber_dev", "bardia_v1", "persian_star", "x_gamer_x",
        "tech_lab", "real_ali", "code_ninja", "the_official"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Header
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.ManageSearch, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Text(
                            text = if (isFa) "جستجوی دستی نام کاربری دلخواه" else "Custom Username Checker",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    Text(
                        text = if (isFa) "هر نام کاربری که در نظر دارید را وارد کنید تا وضعیت دقیق و لحظه‌ای آن در بیش از ۲۵ پلتفرم و دامنه بررسی شود."
                        else "Enter any custom username to inspect its exact real-time availability across 25+ platforms and domains.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                }
            }
        }

        // Input Box Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = searchInput,
                        onValueChange = { viewModel.updateSearchInput(it) },
                        label = { Text(if (isFa) "نام کاربری مورد نظر (مثلاً bardia_dev)" else "Enter Custom Username") },
                        prefix = { Text("@ ") },
                        leadingIcon = { Icon(Icons.Default.AlternateEmail, contentDescription = null) },
                        trailingIcon = {
                            if (searchInput.isNotEmpty()) {
                                IconButton(onClick = { viewModel.updateSearchInput("") }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear")
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("custom_username_input")
                    )

                    Button(
                        onClick = {
                            if (searchInput.trim().isNotEmpty()) {
                                viewModel.checkUsername(searchInput)
                                onNavigateToDashboard()
                            }
                        },
                        enabled = searchInput.trim().isNotEmpty(),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("check_custom_button")
                    ) {
                        Icon(Icons.Default.Search, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isFa) "بررسی لحظه‌ای در تمام پلتفرم‌ها" else "Check All Platforms Live",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }

        // Quick Search Chips
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = if (isFa) "ایده‌های سریع جهت تست:" else "Quick Test Ideas:",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    quickIdeaChips.forEach { idea ->
                        SuggestionChip(
                            onClick = {
                                viewModel.updateSearchInput(idea)
                                viewModel.checkUsername(idea)
                                onNavigateToDashboard()
                            },
                            label = { Text("@$idea") },
                            icon = { Icon(Icons.Default.LocalOffer, contentDescription = null, modifier = Modifier.size(14.dp)) }
                        )
                    }
                }
            }
        }
    }
}
