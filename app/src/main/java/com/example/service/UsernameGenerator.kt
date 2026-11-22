package com.example.service

import com.example.data.UserQuestionnaire
import com.example.data.VibeStyle
import java.util.Locale

class UsernameGenerator(private val geminiService: GeminiService = GeminiService()) {

    suspend fun generateSuggestions(q: UserQuestionnaire): List<String> {
        val candidates = LinkedHashSet<String>()

        val baseSeed = q.seedName.trim().lowercase(Locale.ROOT).replace(" ", "")
            .ifEmpty { "user" }
        val second = q.secondSeed.trim().lowercase(Locale.ROOT).replace(" ", "")

        // 1. Fetch AI Suggestions if Gemini API key available
        val aiResults = geminiService.generateAiUsernames(q)
        candidates.addAll(aiResults)

        // 2. Pure & Minimal Variations
        candidates.add(baseSeed)
        if (second.isNotEmpty()) {
            candidates.add("${baseSeed}_${second}")
            candidates.add("${baseSeed}${second}")
            candidates.add("${second}_${baseSeed}")
        }

        // 3. Prefix & Suffix Variations
        val prefixes = if (q.preferredPrefixes.isNotEmpty()) q.preferredPrefixes else listOf("the_", "real_", "iam_", "official_", "x_", "mr_")
        val suffixes = if (q.preferredSuffixes.isNotEmpty()) q.preferredSuffixes else listOf("_dev", "_official", "_ir", "_v1", "_pro", "_hq", "_hub")

        prefixes.forEach { prefix ->
            candidates.add("${prefix}${baseSeed}")
            if (second.isNotEmpty()) candidates.add("${prefix}${baseSeed}_${second}")
        }

        suffixes.forEach { suffix ->
            candidates.add("${baseSeed}${suffix}")
            if (second.isNotEmpty()) candidates.add("${baseSeed}_${second}${suffix}")
        }

        // 4. Vibe Specific Variations
        when (q.selectedVibe) {
            VibeStyle.GAMER_PRO -> {
                candidates.add("x_${baseSeed}_x")
                candidates.add("${baseSeed}_gaming")
                candidates.add("${baseSeed}_pro")
                candidates.add("cyber_${baseSeed}")
                candidates.add("${baseSeed}_zero")
                candidates.add("shadow_${baseSeed}")
            }
            VibeStyle.TECH_DEV -> {
                candidates.add("${baseSeed}_dev")
                candidates.add("${baseSeed}_tech")
                candidates.add("${baseSeed}code")
                candidates.add("${baseSeed}_io")
                candidates.add("dev_${baseSeed}")
                candidates.add("${baseSeed}_stack")
            }
            VibeStyle.PERSIAN_STYLE -> {
                candidates.add("${baseSeed}_ir")
                candidates.add("persian_${baseSeed}")
                candidates.add("${baseSeed}_iran")
                candidates.add("iran_${baseSeed}")
                candidates.add("${baseSeed}_tehran")
            }
            VibeStyle.CUTE_AESTHETIC -> {
                candidates.add("${baseSeed}_cute")
                candidates.add("${baseSeed}_art")
                candidates.add("${baseSeed}_cloud")
                candidates.add("soft_${baseSeed}")
                candidates.add("pink_${baseSeed}")
            }
            VibeStyle.SHORT_PUNCHY -> {
                val shortName = if (baseSeed.length > 3) baseSeed.substring(0, 3) else baseSeed
                candidates.add("${shortName}_x")
                candidates.add("i_${shortName}")
                candidates.add("${shortName}_v1")
                candidates.add("${shortName}_hq")
            }
            VibeStyle.EXECUTIVE -> {
                candidates.add("${baseSeed}_official")
                candidates.add("${baseSeed}_group")
                candidates.add("${baseSeed}_studio")
                candidates.add("the_${baseSeed}_co")
            }
            VibeStyle.CREATIVE_ART -> {
                candidates.add("${baseSeed}_design")
                candidates.add("${baseSeed}_studio")
                candidates.add("art_by_${baseSeed}")
                candidates.add("${baseSeed}_gallery")
            }
            VibeStyle.MODERN_MINIMAL -> {
                candidates.add("the_${baseSeed}")
                candidates.add("${baseSeed}_lab")
                candidates.add("${baseSeed}_box")
            }
        }

        // 5. Hobby Variations
        q.hobbies.forEach { hobby ->
            val cleanHobby = hobby.replace(Regex("[^a-zA-Z]"), "").lowercase()
            if (cleanHobby.isNotEmpty()) {
                candidates.add("${baseSeed}_${cleanHobby}")
                candidates.add("${cleanHobby}_${baseSeed}")
            }
        }

        // 6. Number & Year variations
        val numberTag = q.birthYearOrNumber.ifEmpty { "1380" }
        if (q.includeNumbers) {
            candidates.add("${baseSeed}${numberTag}")
            candidates.add("${baseSeed}_${numberTag}")
            candidates.add("${baseSeed}007")
            candidates.add("${baseSeed}2026")
        }

        // 7. Clean up candidates to respect allowed chars & max lengths
        return candidates.map { name ->
            var cleaned = name.lowercase(Locale.ROOT)
            if (!q.allowUnderscores) cleaned = cleaned.replace("_", "")
            if (!q.allowDots) cleaned = cleaned.replace(".", "")
            cleaned
        }.filter { it.length >= 3 && it.length <= 25 }
            .distinct()
            .take(35)
    }
}
