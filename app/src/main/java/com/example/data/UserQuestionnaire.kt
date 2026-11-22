package com.example.data

enum class VibeStyle(val titleFa: String, val titleEn: String, val icon: String) {
    MODERN_MINIMAL("مدرن و مینیمال", "Modern Minimal", "✨"),
    GAMER_PRO("گیمینگ و پرو", "Gamer Pro", "🎮"),
    TECH_DEV("برنامه‌نویسی و تک", "Tech & Developer", "💻"),
    CUTE_AESTHETIC("کوالا و فانتزی", "Cute & Aesthetic", "🌸"),
    PERSIAN_STYLE("اصیل و ایرانی", "Persian Vibe", "🦁"),
    SHORT_PUNCHY("کوتاه و جذاب", "Short & Punchy", "⚡"),
    EXECUTIVE("رسمی و بیزینسی", "Executive & Pro", "💼"),
    CREATIVE_ART("هنری و خلاقانه", "Creative Artist", "🎨")
}

data class UserQuestionnaire(
    val seedName: String = "",                  // e.g. "Bardia", "Ali", "Reza", "Cyber"
    val secondSeed: String = "",                // e.g. "Dev", "Code", "Art", "Star"
    val hobbies: List<String> = emptyList(),    // e.g. ["Coding", "Gaming", "Crypto", "Anime", "Music"]
    val selectedVibe: VibeStyle = VibeStyle.MODERN_MINIMAL,
    val includeNumbers: Boolean = true,
    val birthYearOrNumber: String = "",        // e.g. "1380", "2000", "007", "99"
    val allowUnderscores: Boolean = true,
    val allowDots: Boolean = false,
    val preferredPrefixes: List<String> = emptyList(), // e.g. ["the_", "real_", "iam_", "x_"]
    val preferredSuffixes: List<String> = emptyList(), // e.g. ["_dev", "_official", "_ir", "_v1"]
    val lengthPreference: String = "MEDIUM",   // SHORT (<6), MEDIUM (6-10), LONG (>10)
    val usePersianFinglish: Boolean = true
)

val HOBBY_OPTIONS = listOf(
    "برنامه‌نویسی 💻", "گیمینگ 🎮", "کریپتو و بلاکچین 🪙", "موسیقی و دی‌جی 🎵",
    "طراحی و گرافیک 🎨", "ورزش و فیتنس 🏋️", "سفر و عکاسی 📸", "سینما و انیمه 🎬",
    "کسب‌وکار و استارتاپ 🚀", "علم و تکنولوژی 🔬", "مد و استایل 👗", "آشپزی 🍳"
)

val PREFIX_OPTIONS = listOf("the_", "real_", "iam_", "official_", "x_", "mr_", "ms_", "dr_")
val SUFFIX_OPTIONS = listOf("_dev", "_official", "_ir", "_v1", "_pro", "_hq", "_zone", "_hub")
