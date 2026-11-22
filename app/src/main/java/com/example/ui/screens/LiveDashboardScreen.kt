package com.example.ui.screens

import android.content.Intent
import android.net.Uri
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CheckStatus
import com.example.data.PlatformCategory
import com.example.data.PlatformCheckResult
import com.example.ui.AvailabilityStats
import com.example.ui.MainViewModel
import com.example.ui.theme.*

@Composable
fun LiveDashboardScreen(
    viewModel: MainViewModel,
    onNavigateToSearch: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    val checkingUsername by viewModel.checkingUsername.collectAsState()
    val isChecking by viewModel.isChecking.collectAsState()
    val isFa by viewModel.isPersianLanguage.collectAsState()

    val platformResults by viewModel.filteredPlatformResults.collectAsState(initial = emptyList())
    val stats by viewModel.availabilityStats.collectAsState(initial = AvailabilityStats())

    val selectedCategory by viewModel.selectedCategoryFilter.collectAsState()
    val selectedStatus by viewModel.selectedStatusFilter.collectAsState()

    var snackbarMessage by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Active Username Header
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isFa) "آیدی در حال بررسی:" else "Active Username:",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = if (checkingUsername.isNotEmpty()) "@$checkingUsername" else if (isFa) "هنوز آیدی انتخاب نشده" else "No username selected",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    if (checkingUsername.isNotEmpty()) {
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            IconButton(onClick = { viewModel.checkUsername(checkingUsername) }) {
                                Icon(Icons.Default.Refresh, contentDescription = "Re-check")
                            }

                            OutlinedButton(
                                onClick = onNavigateToSearch,
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(if (isFa) "تغییر آیدی" else "Change")
                            }
                        }
                    }
                }
            }
        }

        if (checkingUsername.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            Icons.Default.ManageSearch,
                            contentDescription = null,
                            modifier = Modifier.size(56.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = if (isFa) "هیچ نام کاربری برای بررسی انتخاب نشده است" else "No username submitted yet",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Button(
                            onClick = onNavigateToSearch,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(if (isFa) "ورود نام کاربری دلخواه یا انتخاب از پیشنهادات" else "Enter custom username or choose proposal")
                        }
                    }
                }
            }
        } else {
            // Live Progress & Summary Stats Card
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (stats.percentage >= 60) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.errorContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column {
                                Text(
                                    text = if (isFa) "پنل وضعیت لحظه‌ای دسترسی" else "Real-time Availability Panel",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                                Text(
                                    text = if (isFa) "${stats.available} از ${stats.total} پلتفرم آزاد است (${stats.percentage}٪)"
                                    else "${stats.available} of ${stats.total} platforms available (${stats.percentage}%)",
                                    fontSize = 14.sp
                                )
                            }

                            if (isChecking) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(28.dp),
                                    strokeWidth = 3.dp
                                )
                            } else {
                                Surface(
                                    shape = CircleShape,
                                    color = if (stats.percentage >= 50) StatusAvailable else StatusTaken,
                                    modifier = Modifier.size(44.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = "${stats.percentage}٪",
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                            }
                        }

                        if (isChecking) {
                            LinearProgressIndicator(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(CircleShape)
                            )
                        }

                        // Summary Badges
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            StatusBadgeCount("🟢 ${if (isFa) "آزاد" else "Available"}", stats.available, StatusAvailable)
                            StatusBadgeCount("🔴 ${if (isFa) "گرفته شده" else "Taken"}", stats.taken, StatusTaken)
                            StatusBadgeCount("🟡 ${if (isFa) "محدود" else "Restricted"}", stats.restricted, StatusRestricted)
                        }
                    }
                }
            }

            // Category Filter Bar
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = if (isFa) "فیلتر دسته‌بندی پلتفرم‌ها:" else "Filter by Platform Category:",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(PlatformCategory.values()) { category ->
                            val isSelected = selectedCategory == category
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.setCategoryFilter(category) },
                                label = {
                                    Text(if (isFa) category.titleFa else category.titleEn)
                                }
                            )
                        }
                    }
                }
            }

            // Status Filter Bar
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilterChip(
                        selected = selectedStatus == null,
                        onClick = { viewModel.setStatusFilter(null) },
                        label = { Text(if (isFa) "همه وضعیت‌ها" else "All Statuses") }
                    )
                    FilterChip(
                        selected = selectedStatus == CheckStatus.AVAILABLE,
                        onClick = { viewModel.setStatusFilter(if (selectedStatus == CheckStatus.AVAILABLE) null else CheckStatus.AVAILABLE) },
                        label = { Text("🟢 ${if (isFa) "فقط آزادها" else "Available Only"}") }
                    )
                    FilterChip(
                        selected = selectedStatus == CheckStatus.TAKEN,
                        onClick = { viewModel.setStatusFilter(if (selectedStatus == CheckStatus.TAKEN) null else CheckStatus.TAKEN) },
                        label = { Text("🔴 ${if (isFa) "گرفته شده" else "Taken"}") }
                    )
                }
            }

            // Platform Results List Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isFa) "لیست نتایج بررسی (${platformResults.size})" else "Platform Check Results (${platformResults.size})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )

                    TextButton(onClick = {
                        val exportText = StringBuilder().apply {
                            append("گزارش دسترسی آیدی @$checkingUsername:\n")
                            platformResults.filter { it.status == CheckStatus.AVAILABLE }.forEach {
                                append("🟢 ${it.platform.name}: ${String.format(it.platform.profileUrlTemplate, checkingUsername)}\n")
                            }
                        }.toString()
                        clipboardManager.setText(AnnotatedString(exportText))
                        snackbarMessage = if (isFa) "گزارش آیدی‌های آزاد کپی شد!" else "Copied available links to clipboard!"
                    }) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (isFa) "کپی گزارش" else "Copy Report", fontSize = 12.sp)
                    }
                }
            }

            // Items
            items(platformResults, key = { it.platform.id }) { result ->
                PlatformResultCard(
                    result = result,
                    isFa = isFa,
                    onOpenUrl = { url ->
                        try {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            // Fallback ignore
                        }
                    },
                    onCopyUrl = { url ->
                        clipboardManager.setText(AnnotatedString(url))
                        snackbarMessage = if (isFa) "لینک در حافظه کپی شد" else "Link copied to clipboard"
                    },
                    onToggleSave = {
                        viewModel.toggleFavorite(checkingUsername)
                    }
                )
            }
        }
    }
}

@Composable
fun StatusBadgeCount(label: String, count: Int, color: Color) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = color.copy(alpha = 0.2f),
        contentColor = color
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(text = label, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            Text(text = "($count)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun PlatformResultCard(
    result: PlatformCheckResult,
    isFa: Boolean,
    onOpenUrl: (String) -> Unit,
    onCopyUrl: (String) -> Unit,
    onToggleSave: () -> Unit
) {
    val platform = result.platform
    val profileUrl = String.format(platform.profileUrlTemplate, result.username)

    val (statusColor, statusText) = when (result.status) {
        CheckStatus.AVAILABLE -> StatusAvailable to (if (isFa) result.messageFa else result.messageEn)
        CheckStatus.TAKEN -> StatusTaken to (if (isFa) result.messageFa else result.messageEn)
        CheckStatus.RESTRICTED -> StatusRestricted to (if (isFa) result.messageFa else result.messageEn)
        CheckStatus.CHECKING -> StatusPending to (if (isFa) "در حال بررسی..." else "Checking...")
        CheckStatus.PENDING -> StatusPending to (if (isFa) "در صف بررسی" else "Pending")
        CheckStatus.ERROR -> StatusPending to (if (isFa) result.messageFa else result.messageEn)
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("platform_card_${platform.id}")
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Platform Brand Color Dot / Icon
                    Surface(
                        shape = CircleShape,
                        color = parseHexColor(platform.brandColorHex),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = platform.name.take(2).uppercase(),
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }

                    Column {
                        Text(
                            text = platform.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = profileUrl,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Status Badge
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = statusColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = statusText,
                        color = statusColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }

            if (result.status == CheckStatus.AVAILABLE || result.status == CheckStatus.TAKEN) {
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { onCopyUrl(profileUrl) }) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy Link", modifier = Modifier.size(18.dp))
                    }

                    IconButton(onClick = { onOpenUrl(profileUrl) }) {
                        Icon(Icons.Default.OpenInNew, contentDescription = "Open Link", modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}

fun parseHexColor(hex: String): Color {
    return try {
        Color(android.graphics.Color.parseColor(hex))
    } catch (e: Exception) {
        PrimaryIndigo
    }
}
