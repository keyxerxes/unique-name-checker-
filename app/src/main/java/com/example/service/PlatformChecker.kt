package com.example.service

import com.example.data.ALL_PLATFORMS
import com.example.data.CheckStatus
import com.example.data.Platform
import com.example.data.PlatformCheckResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

class PlatformChecker {

    private val client = OkHttpClient.Builder()
        .connectTimeout(5, TimeUnit.SECONDS)
        .readTimeout(5, TimeUnit.SECONDS)
        .followRedirects(true)
        .build()

    private val userAgents = listOf(
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36",
        "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36",
        "Mozilla/5.0 (iPhone; CPU iPhone OS 17_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/17.1 Mobile/15E148 Safari/604.1"
    )

    fun checkUsernameRealtimeFlow(username: String): Flow<PlatformCheckResult> = channelFlow {
        val cleanUser = username.trim().lowercase()

        // First send pending status for all platforms
        ALL_PLATFORMS.forEach { platform ->
            send(
                PlatformCheckResult(
                    platform = platform,
                    username = cleanUser,
                    status = CheckStatus.CHECKING,
                    messageFa = "در حال بررسی...",
                    messageEn = "Checking..."
                )
            )
        }

        // Launch async checks for all platforms in parallel
        withContext(Dispatchers.IO) {
            ALL_PLATFORMS.map { platform ->
                async {
                    val result = performSinglePlatformCheck(platform, cleanUser)
                    send(result)
                }
            }.awaitAll()
        }
    }

    suspend fun performSinglePlatformCheck(platform: Platform, username: String): PlatformCheckResult = withContext(Dispatchers.IO) {
        val cleanUser = username.trim()

        // 1. Format validation
        if (cleanUser.length < platform.minLength) {
            return@withContext PlatformCheckResult(
                platform = platform,
                username = cleanUser,
                status = CheckStatus.RESTRICTED,
                messageFa = "کوتاه‌تر از حداقل (${platform.minLength} کاراکتر)",
                messageEn = "Shorter than minimum (${platform.minLength} chars)"
            )
        }

        if (cleanUser.length > platform.maxLength) {
            return@withContext PlatformCheckResult(
                platform = platform,
                username = cleanUser,
                status = CheckStatus.RESTRICTED,
                messageFa = "بلندتر از حد مجاز (${platform.maxLength} کاراکتر)",
                messageEn = "Exceeds max length (${platform.maxLength} chars)"
            )
        }

        try {
            val pattern = Pattern.compile(platform.allowedRegex)
            if (!pattern.matcher(cleanUser).matches()) {
                return@withContext PlatformCheckResult(
                    platform = platform,
                    username = cleanUser,
                    status = CheckStatus.RESTRICTED,
                    messageFa = "کاراکتر غیرمجاز برای این پلتفرم",
                    messageEn = "Invalid character for this platform"
                )
            }
        } catch (e: Exception) {
            // Regex fallback ignore
        }

        // 2. Perform Network Check
        val targetUrl = String.format(platform.checkUrlTemplate, cleanUser)
        val userAgent = userAgents.random()

        val request = Request.Builder()
            .url(targetUrl)
            .header("User-Agent", userAgent)
            .header("Accept-Language", "en-US,en;q=0.9")
            .build()

        try {
            client.newCall(request).execute().use { response ->
                val code = response.code

                when {
                    code == 404 -> {
                        PlatformCheckResult(
                            platform = platform,
                            username = cleanUser,
                            status = CheckStatus.AVAILABLE,
                            httpCode = code,
                            messageFa = "🟢 آزاد و قابل ثبت!",
                            messageEn = "🟢 Available to claim!"
                        )
                    }

                    code == 200 -> {
                        // Special check for Telegram page content
                        if (platform.id == "telegram") {
                            val bodyText = response.body?.string() ?: ""
                            if (bodyText.contains("tgme_page_extra") || bodyText.contains("extra_title") || bodyText.contains("@$cleanUser")) {
                                PlatformCheckResult(
                                    platform = platform,
                                    username = cleanUser,
                                    status = CheckStatus.TAKEN,
                                    httpCode = code,
                                    messageFa = "🔴 قبلاً گرفته شده است",
                                    messageEn = "🔴 Username is taken"
                                )
                            } else {
                                PlatformCheckResult(
                                    platform = platform,
                                    username = cleanUser,
                                    status = CheckStatus.AVAILABLE,
                                    httpCode = code,
                                    messageFa = "🟢 آزاد و قابل ثبت!",
                                    messageEn = "🟢 Available to claim!"
                                )
                            }
                        } else {
                            PlatformCheckResult(
                                platform = platform,
                                username = cleanUser,
                                status = CheckStatus.TAKEN,
                                httpCode = code,
                                messageFa = "🔴 قبلاً گرفته شده است",
                                messageEn = "🔴 Username is taken"
                            )
                        }
                    }

                    code in 300..399 -> {
                        PlatformCheckResult(
                            platform = platform,
                            username = cleanUser,
                            status = CheckStatus.TAKEN,
                            httpCode = code,
                            messageFa = "🔴 در حال استفاده (تغییر مسیر)",
                            messageEn = "🔴 Taken (Redirected)"
                        )
                    }

                    code == 429 -> {
                        PlatformCheckResult(
                            platform = platform,
                            username = cleanUser,
                            status = CheckStatus.RESTRICTED,
                            httpCode = code,
                            messageFa = "🟡 نیاز به بررسی دستی (محدودیت تعداد درخواست)",
                            messageEn = "🟡 Rate limited (Check manually)"
                        )
                    }

                    else -> {
                        PlatformCheckResult(
                            platform = platform,
                            username = cleanUser,
                            status = CheckStatus.AVAILABLE,
                            httpCode = code,
                            messageFa = "🟢 احتمالاً آزاد ($code)",
                            messageEn = "🟢 Likely available ($code)"
                        )
                    }
                }
            }
        } catch (e: Exception) {
            // For domain names or blocked routes: Connection failure / Unresolved host often indicates domain is available!
            if (platform.category == com.example.data.PlatformCategory.DOMAINS) {
                PlatformCheckResult(
                    platform = platform,
                    username = cleanUser,
                    status = CheckStatus.AVAILABLE,
                    httpCode = null,
                    messageFa = "🟢 دامنه آزاد و ثبت نشده است!",
                    messageEn = "🟢 Domain is available!"
                )
            } else {
                PlatformCheckResult(
                    platform = platform,
                    username = cleanUser,
                    status = CheckStatus.ERROR,
                    messageFa = "⚪ عدم پاسخ‌دهی سرور (نیاز به بررسی)",
                    messageEn = "⚪ Timeout / Network Error"
                )
            }
        }
    }
}
