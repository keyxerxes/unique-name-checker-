package com.example.service

import com.example.BuildConfig
import com.example.data.UserQuestionnaire
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class GeminiService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    suspend fun generateAiUsernames(questionnaire: UserQuestionnaire): List<String> = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isNullOrEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext emptyList()
        }

        val prompt = """
            You are a world-class social media branding and username creation specialist.
            Generate 12 UNIQUE, CATCHY, RARE, AND HIGHLY DESIRABLE usernames based on these user preferences:
            - Primary Seed Word / Name: "${questionnaire.seedName}"
            - Secondary Seed Word: "${questionnaire.secondSeed}"
            - Hobbies & Topics: ${questionnaire.hobbies.joinToString(", ")}
            - Vibe & Style: ${questionnaire.selectedVibe.titleEn}
            - Preferred Prefixes: ${questionnaire.preferredPrefixes.joinToString(", ")}
            - Preferred Suffixes: ${questionnaire.preferredSuffixes.joinToString(", ")}
            - Number preference: ${if (questionnaire.includeNumbers) questionnaire.birthYearOrNumber else "No numbers"}
            - Persian/Finglish style: ${if (questionnaire.usePersianFinglish) "Yes, blend Persian rhythm/Finglish aesthetics" else "No"}
            
            REQUIREMENTS:
            1. All usernames MUST be lowercase, alphanumeric with allowed underscores/dots.
            2. Must be easy to spell, sound premium, and be unique.
            3. Return ONLY a valid JSON array of strings, for example:
            ["username1", "username2", "username3", "username4"]
            Do NOT include markdown formatting or extra text outside the JSON array.
        """.trimIndent()

        try {
            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"

            val jsonBody = JSONObject().apply {
                put("contents", JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().apply {
                                put("text", prompt)
                            })
                        })
                    })
                })
            }

            val request = Request.Builder()
                .url(url)
                .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return@withContext emptyList()

                val responseBody = response.body?.string() ?: return@withContext emptyList()
                val jsonResponse = JSONObject(responseBody)
                val candidates = jsonResponse.optJSONArray("candidates") ?: return@withContext emptyList()
                if (candidates.length() == 0) return@withContext emptyList()

                val firstCandidate = candidates.getJSONObject(0)
                val content = firstCandidate.optJSONObject("content") ?: return@withContext emptyList()
                val parts = content.optJSONArray("parts") ?: return@withContext emptyList()
                if (parts.length() == 0) return@withContext emptyList()

                val text = parts.getJSONObject(0).optString("text", "")
                val cleanJson = text.trim()
                    .removePrefix("```json")
                    .removePrefix("```")
                    .removeSuffix("```")
                    .trim()

                val jsonArray = JSONArray(cleanJson)
                val resultList = mutableListOf<String>()
                for (i in 0 until jsonArray.length()) {
                    val candidate = jsonArray.getString(i).trim().lowercase()
                        .replace(" ", "")
                    if (candidate.isNotEmpty() && !resultList.contains(candidate)) {
                        resultList.add(candidate)
                    }
                }
                resultList
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}
