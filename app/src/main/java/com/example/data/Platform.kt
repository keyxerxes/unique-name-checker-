package com.example.data

enum class PlatformCategory(val titleFa: String, val titleEn: String) {
    ALL("همه", "All"),
    SOCIAL("شبکه‌های اجتماعی", "Social Networks"),
    DEV_CREATIVE("برنامه‌نویسی و طراحی", "Developer & Creative"),
    GAMING_STREAMING("گیمینگ و استریم", "Gaming & Streaming"),
    MUSIC("موسیقی و پادکست", "Music & Audio"),
    DOMAINS("دامنه و وبسایت", "Domains & Web")
}

enum class CheckStatus {
    PENDING,
    CHECKING,
    AVAILABLE,      // 🟢
    TAKEN,          // 🔴
    RESTRICTED,     // 🟡 (format issue or restricted username)
    ERROR           // ⚪ (network timeout / unknown)
}

data class Platform(
    val id: String,
    val name: String,
    val category: PlatformCategory,
    val brandColorHex: String,
    val profileUrlTemplate: String, // e.g., "https://t.me/%s"
    val checkUrlTemplate: String,   // e.g., "https://t.me/%s"
    val minLength: Int = 3,
    val maxLength: Int = 30,
    val allowedRegex: String = "^[a-zA-Z0-9_.-]+$",
    val noteFa: String? = null
)

val ALL_PLATFORMS = listOf(
    // Social & Messaging
    Platform(
        id = "telegram",
        name = "Telegram",
        category = PlatformCategory.SOCIAL,
        brandColorHex = "#229ED9",
        profileUrlTemplate = "https://t.me/%s",
        checkUrlTemplate = "https://t.me/%s",
        minLength = 5,
        maxLength = 32,
        allowedRegex = "^[a-zA-Z0-9_]+$",
        noteFa = "حداقل ۵ کاراکتر، حروف و اعداد و خط زیر"
    ),
    Platform(
        id = "instagram",
        name = "Instagram",
        category = PlatformCategory.SOCIAL,
        brandColorHex = "#E1306C",
        profileUrlTemplate = "https://instagram.com/%s",
        checkUrlTemplate = "https://www.instagram.com/%s/",
        minLength = 1,
        maxLength = 30,
        allowedRegex = "^[a-zA-Z0-9._]+$"
    ),
    Platform(
        id = "x_twitter",
        name = "X / Twitter",
        category = PlatformCategory.SOCIAL,
        brandColorHex = "#000000",
        profileUrlTemplate = "https://x.com/%s",
        checkUrlTemplate = "https://x.com/%s",
        minLength = 4,
        maxLength = 15,
        allowedRegex = "^[a-zA-Z0-9_]+$"
    ),
    Platform(
        id = "tiktok",
        name = "TikTok",
        category = PlatformCategory.SOCIAL,
        brandColorHex = "#EE1D52",
        profileUrlTemplate = "https://tiktok.com/@%s",
        checkUrlTemplate = "https://www.tiktok.com/@%s",
        minLength = 2,
        maxLength = 24,
        allowedRegex = "^[a-zA-Z0-9_.-]+$"
    ),
    Platform(
        id = "reddit",
        name = "Reddit",
        category = PlatformCategory.SOCIAL,
        brandColorHex = "#FF4500",
        profileUrlTemplate = "https://reddit.com/user/%s",
        checkUrlTemplate = "https://www.reddit.com/user/%s/about.json",
        minLength = 3,
        maxLength = 20,
        allowedRegex = "^[a-zA-Z0-9_-]+$"
    ),
    Platform(
        id = "pinterest",
        name = "Pinterest",
        category = PlatformCategory.SOCIAL,
        brandColorHex = "#BD081C",
        profileUrlTemplate = "https://pinterest.com/%s",
        checkUrlTemplate = "https://www.pinterest.com/%s/",
        minLength = 3,
        maxLength = 30,
        allowedRegex = "^[a-zA-Z0-9_]+$"
    ),
    Platform(
        id = "linkedin",
        name = "LinkedIn",
        category = PlatformCategory.SOCIAL,
        brandColorHex = "#0A66C2",
        profileUrlTemplate = "https://linkedin.com/in/%s",
        checkUrlTemplate = "https://www.linkedin.com/in/%s/",
        minLength = 3,
        maxLength = 100,
        allowedRegex = "^[a-zA-Z0-9_-]+$"
    ),
    Platform(
        id = "threads",
        name = "Threads",
        category = PlatformCategory.SOCIAL,
        brandColorHex = "#101010",
        profileUrlTemplate = "https://threads.net/@%s",
        checkUrlTemplate = "https://www.threads.net/@%s",
        minLength = 1,
        maxLength = 30,
        allowedRegex = "^[a-zA-Z0-9._]+$"
    ),
    Platform(
        id = "snapchat",
        name = "Snapchat",
        category = PlatformCategory.SOCIAL,
        brandColorHex = "#FFFC00",
        profileUrlTemplate = "https://snapchat.com/add/%s",
        checkUrlTemplate = "https://www.snapchat.com/add/%s",
        minLength = 3,
        maxLength = 15,
        allowedRegex = "^[a-zA-Z0-9._-]+$"
    ),

    // Developer & Creative
    Platform(
        id = "github",
        name = "GitHub",
        category = PlatformCategory.DEV_CREATIVE,
        brandColorHex = "#2DBA4E",
        profileUrlTemplate = "https://github.com/%s",
        checkUrlTemplate = "https://github.com/%s",
        minLength = 1,
        maxLength = 39,
        allowedRegex = "^[a-zA-Z0-9-]+$"
    ),
    Platform(
        id = "gitlab",
        name = "GitLab",
        category = PlatformCategory.DEV_CREATIVE,
        brandColorHex = "#FC6D26",
        profileUrlTemplate = "https://gitlab.com/%s",
        checkUrlTemplate = "https://gitlab.com/%s",
        minLength = 2,
        maxLength = 255,
        allowedRegex = "^[a-zA-Z0-9._-]+$"
    ),
    Platform(
        id = "behance",
        name = "Behance",
        category = PlatformCategory.DEV_CREATIVE,
        brandColorHex = "#1769FF",
        profileUrlTemplate = "https://behance.net/%s",
        checkUrlTemplate = "https://www.behance.net/%s",
        minLength = 3,
        maxLength = 30,
        allowedRegex = "^[a-zA-Z0-9_]+$"
    ),
    Platform(
        id = "dribbble",
        name = "Dribbble",
        category = PlatformCategory.DEV_CREATIVE,
        brandColorHex = "#EA4C89",
        profileUrlTemplate = "https://dribbble.com/%s",
        checkUrlTemplate = "https://dribbble.com/%s",
        minLength = 3,
        maxLength = 30,
        allowedRegex = "^[a-zA-Z0-9_-]+$"
    ),
    Platform(
        id = "medium",
        name = "Medium",
        category = PlatformCategory.DEV_CREATIVE,
        brandColorHex = "#000000",
        profileUrlTemplate = "https://medium.com/@%s",
        checkUrlTemplate = "https://medium.com/@%s",
        minLength = 3,
        maxLength = 30,
        allowedRegex = "^[a-zA-Z0-9_]+$"
    ),
    Platform(
        id = "producthunt",
        name = "ProductHunt",
        category = PlatformCategory.DEV_CREATIVE,
        brandColorHex = "#DA552F",
        profileUrlTemplate = "https://producthunt.com/@%s",
        checkUrlTemplate = "https://www.producthunt.com/@%s",
        minLength = 2,
        maxLength = 30,
        allowedRegex = "^[a-zA-Z0-9_]+$"
    ),

    // Gaming & Streaming
    Platform(
        id = "youtube",
        name = "YouTube Handle",
        category = PlatformCategory.GAMING_STREAMING,
        brandColorHex = "#FF0000",
        profileUrlTemplate = "https://youtube.com/@%s",
        checkUrlTemplate = "https://www.youtube.com/@%s",
        minLength = 3,
        maxLength = 30,
        allowedRegex = "^[a-zA-Z0-9._-]+$"
    ),
    Platform(
        id = "twitch",
        name = "Twitch",
        category = PlatformCategory.GAMING_STREAMING,
        brandColorHex = "#9146FF",
        profileUrlTemplate = "https://twitch.tv/%s",
        checkUrlTemplate = "https://www.twitch.tv/%s",
        minLength = 4,
        maxLength = 25,
        allowedRegex = "^[a-zA-Z0-9_]+$"
    ),
    Platform(
        id = "kick",
        name = "Kick",
        category = PlatformCategory.GAMING_STREAMING,
        brandColorHex = "#53FC18",
        profileUrlTemplate = "https://kick.com/%s",
        checkUrlTemplate = "https://kick.com/%s",
        minLength = 3,
        maxLength = 25,
        allowedRegex = "^[a-zA-Z0-9_]+$"
    ),
    Platform(
        id = "steam",
        name = "Steam Custom URL",
        category = PlatformCategory.GAMING_STREAMING,
        brandColorHex = "#171A21",
        profileUrlTemplate = "https://steamcommunity.com/id/%s",
        checkUrlTemplate = "https://steamcommunity.com/id/%s",
        minLength = 2,
        maxLength = 32,
        allowedRegex = "^[a-zA-Z0-9_-]+$"
    ),
    Platform(
        id = "roblox",
        name = "Roblox",
        category = PlatformCategory.GAMING_STREAMING,
        brandColorHex = "#000000",
        profileUrlTemplate = "https://roblox.com/user.aspx?username=%s",
        checkUrlTemplate = "https://www.roblox.com/user.aspx?username=%s",
        minLength = 3,
        maxLength = 20,
        allowedRegex = "^[a-zA-Z0-9_]+$"
    ),

    // Music & Audio
    Platform(
        id = "spotify",
        name = "Spotify Artist/User",
        category = PlatformCategory.MUSIC,
        brandColorHex = "#1DB954",
        profileUrlTemplate = "https://open.spotify.com/user/%s",
        checkUrlTemplate = "https://open.spotify.com/user/%s",
        minLength = 3,
        maxLength = 30,
        allowedRegex = "^[a-zA-Z0-9_]+$"
    ),
    Platform(
        id = "soundcloud",
        name = "SoundCloud",
        category = PlatformCategory.MUSIC,
        brandColorHex = "#FF5500",
        profileUrlTemplate = "https://soundcloud.com/%s",
        checkUrlTemplate = "https://soundcloud.com/%s",
        minLength = 3,
        maxLength = 25,
        allowedRegex = "^[a-zA-Z0-9_-]+$"
    ),

    // Domains & Web Extensions
    Platform(
        id = "domain_com",
        name = ".COM Domain",
        category = PlatformCategory.DOMAINS,
        brandColorHex = "#0284C7",
        profileUrlTemplate = "https://%s.com",
        checkUrlTemplate = "https://%s.com",
        minLength = 1,
        maxLength = 63,
        allowedRegex = "^[a-zA-Z0-9-]+$"
    ),
    Platform(
        id = "domain_org",
        name = ".ORG Domain",
        category = PlatformCategory.DOMAINS,
        brandColorHex = "#059669",
        profileUrlTemplate = "https://%s.org",
        checkUrlTemplate = "https://%s.org",
        minLength = 1,
        maxLength = 63,
        allowedRegex = "^[a-zA-Z0-9-]+$"
    ),
    Platform(
        id = "domain_io",
        name = ".IO Domain",
        category = PlatformCategory.DOMAINS,
        brandColorHex = "#7C3AED",
        profileUrlTemplate = "https://%s.io",
        checkUrlTemplate = "https://%s.io",
        minLength = 1,
        maxLength = 63,
        allowedRegex = "^[a-zA-Z0-9-]+$"
    ),
    Platform(
        id = "domain_ir",
        name = ".IR Domain",
        category = PlatformCategory.DOMAINS,
        brandColorHex = "#22C55E",
        profileUrlTemplate = "https://%s.ir",
        checkUrlTemplate = "https://%s.ir",
        minLength = 3,
        maxLength = 63,
        allowedRegex = "^[a-zA-Z0-9-]+$"
    ),
    Platform(
        id = "domain_dev",
        name = ".DEV Domain",
        category = PlatformCategory.DOMAINS,
        brandColorHex = "#EA580C",
        profileUrlTemplate = "https://%s.dev",
        checkUrlTemplate = "https://%s.dev",
        minLength = 1,
        maxLength = 63,
        allowedRegex = "^[a-zA-Z0-9-]+$"
    )
)
