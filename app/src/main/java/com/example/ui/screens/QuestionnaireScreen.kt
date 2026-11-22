package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.ui.MainViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun QuestionnaireScreen(
    viewModel: MainViewModel,
    onCheckUsername: (String) -> Unit
) {
    val questionnaire by viewModel.questionnaire.collectAsState(initial = UserQuestionnaire())
    val suggestions by viewModel.suggestions.collectAsState(initial = emptyList())
    val isGenerating by viewModel.isGenerating.collectAsState(initial = false)
    val isFa by viewModel.isPersianLanguage.collectAsState(initial = true)
    val clipboardManager = LocalClipboardManager.current

    var showAdvancedOptions by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Title Banner
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                shape = RoundedCornerShape(20.dp),
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
                        Text(text = "✨", fontSize = 24.sp)
                        Text(
                            text = if (isFa) "پرسشنامه ساخت آیدی منحصربه‌فرد" else "Unique Username Questionnaire",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    Text(
                        text = if (isFa) "مشخصات و علاقه‌مندی‌های خود را انتخاب کنید تا نام‌های کاربری جذاب، کمیاب و یونیک برای شما تولید شود."
                        else "Fill out your traits & interests to generate catchy, rare & unique username proposals.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                }
            }
        }

        // Section 1: Main Seed Words
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = if (isFa) "۱. کلمات کلیدی و اسم پایه" else "1. Seed Name & Keywords",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )

                    OutlinedTextField(
                        value = questionnaire.seedName,
                        onValueChange = { viewModel.updateQuestionnaire(questionnaire.copy(seedName = it)) },
                        label = { Text(if (isFa) "اسم یا کلمه اصلی (مثلاً Bardia, Ali, Cyber)" else "Primary Name/Word") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("seed_name_input")
                    )

                    OutlinedTextField(
                        value = questionnaire.secondSeed,
                        onValueChange = { viewModel.updateQuestionnaire(questionnaire.copy(secondSeed = it)) },
                        label = { Text(if (isFa) "کلمه مکمل اختیاری (مثلاً Dev, Art, Pro)" else "Optional Second Word") },
                        leadingIcon = { Icon(Icons.Default.Tag, contentDescription = null) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // Section 2: Vibe & Style Selection
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = if (isFa) "۲. سبک و فضای آیدی (Vibe)" else "2. Style & Vibe",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        VibeStyle.values().forEach { vibe ->
                            val isSelected = questionnaire.selectedVibe == vibe
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.updateQuestionnaire(questionnaire.copy(selectedVibe = vibe)) },
                                label = {
                                    Text("${vibe.icon} ${if (isFa) vibe.titleFa else vibe.titleEn}")
                                },
                                leadingIcon = if (isSelected) {
                                    { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                                } else null
                            )
                        }
                    }
                }
            }
        }

        // Section 3: Hobbies & Topics
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = if (isFa) "۳. حوزه‌های کاری و علاقه‌مندی‌ها" else "3. Hobbies & Fields",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        HOBBY_OPTIONS.forEach { hobby ->
                            val isSelected = questionnaire.hobbies.contains(hobby)
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    val newHobbies = if (isSelected) {
                                        questionnaire.hobbies - hobby
                                    } else {
                                        questionnaire.hobbies + hobby
                                    }
                                    viewModel.updateQuestionnaire(questionnaire.copy(hobbies = newHobbies))
                                },
                                label = { Text(hobby) }
                            )
                        }
                    }
                }
            }
        }

        // Advanced Options Toggle
        item {
            OutlinedButton(
                onClick = { showAdvancedOptions = !showAdvancedOptions },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    if (showAdvancedOptions) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    if (isFa) {
                        if (showAdvancedOptions) "پنهان کردن تنظیمات پیشرفته" else "نمایش تنظیمات پیشرفته (پیشوند، پسوند، عدد)"
                    } else {
                        if (showAdvancedOptions) "Hide Advanced Options" else "Show Advanced Options (Prefixes, Suffixes, Numbers)"
                    }
                )
            }
        }

        // Advanced Settings Content
        if (showAdvancedOptions) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = if (isFa) "۴. پیشوند و پسوندهای دلخواه" else "4. Prefixes & Suffixes",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )

                        Text(if (isFa) "پیشوندهای محبوب:" else "Preferred Prefixes:", style = MaterialTheme.typography.bodySmall)
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            PREFIX_OPTIONS.forEach { prefix ->
                                val isSelected = questionnaire.preferredPrefixes.contains(prefix)
                                FilterChip(
                                    selected = isSelected,
                                    onClick = {
                                        val list = if (isSelected) questionnaire.preferredPrefixes - prefix else questionnaire.preferredPrefixes + prefix
                                        viewModel.updateQuestionnaire(questionnaire.copy(preferredPrefixes = list))
                                    },
                                    label = { Text(prefix) }
                                )
                            }
                        }

                        Text(if (isFa) "پسوندهای محبوب:" else "Preferred Suffixes:", style = MaterialTheme.typography.bodySmall)
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            SUFFIX_OPTIONS.forEach { suffix ->
                                val isSelected = questionnaire.preferredSuffixes.contains(suffix)
                                FilterChip(
                                    selected = isSelected,
                                    onClick = {
                                        val list = if (isSelected) questionnaire.preferredSuffixes - suffix else questionnaire.preferredSuffixes + suffix
                                        viewModel.updateQuestionnaire(questionnaire.copy(preferredSuffixes = list))
                                    },
                                    label = { Text(suffix) }
                                )
                            }
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = if (isFa) "شامل عدد یا سال تولد" else "Include Year or Number",
                                modifier = Modifier.weight(1f)
                            )
                            Switch(
                                checked = questionnaire.includeNumbers,
                                onCheckedChange = { viewModel.updateQuestionnaire(questionnaire.copy(includeNumbers = it)) }
                            )
                        }

                        if (questionnaire.includeNumbers) {
                            OutlinedTextField(
                                value = questionnaire.birthYearOrNumber,
                                onValueChange = { viewModel.updateQuestionnaire(questionnaire.copy(birthYearOrNumber = it)) },
                                label = { Text(if (isFa) "عدد یا سال (مثلاً 1380, 2000, 007)" else "Number or Year") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = if (isFa) "ترکیب استایل فینگلیش ایرانی" else "Blend Persian Finglish Aesthetics",
                                modifier = Modifier.weight(1f)
                            )
                            Switch(
                                checked = questionnaire.usePersianFinglish,
                                onCheckedChange = { viewModel.updateQuestionnaire(questionnaire.copy(usePersianFinglish = it)) }
                            )
                        }
                    }
                }
            }
        }

        // Generate Action Button
        item {
            Button(
                onClick = { viewModel.generateSuggestions() },
                enabled = !isGenerating,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("generate_button")
            ) {
                if (isGenerating) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isFa) "تولید و دریافت پیشنهادات یونیک" else "Generate Unique Proposals",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }

        // Suggestions Result Section
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (isFa) "نام‌های پیشنهادی تولید شده (${suggestions.size})" else "Suggested Usernames (${suggestions.size})",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                if (suggestions.isNotEmpty()) {
                    TextButton(onClick = { viewModel.generateSuggestions() }) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (isFa) "باز تولید" else "Regenerate")
                    }
                }
            }
        }

        if (suggestions.isEmpty() && !isGenerating) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isFa) "کلمه کلیدی خود را وارد کرده و دکمه تولید را بزنید" else "Type your seed name and tap Generate",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        items(suggestions) { name ->
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "@$name",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        IconButton(onClick = {
                            clipboardManager.setText(AnnotatedString(name))
                        }) {
                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy", modifier = Modifier.size(20.dp))
                        }

                        Button(
                            onClick = { onCheckUsername(name) },
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.ManageSearch, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (isFa) "بررسی" else "Check", fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}
