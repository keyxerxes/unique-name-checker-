package com.example.data

data class PlatformCheckResult(
    val platform: Platform,
    val username: String,
    val status: CheckStatus = CheckStatus.PENDING,
    val httpCode: Int? = null,
    val messageFa: String = "در حال انتظار...",
    val messageEn: String = "Pending...",
    val checkedAt: Long = System.currentTimeMillis()
)
