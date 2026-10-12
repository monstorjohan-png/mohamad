package com.example.data.ai

import com.example.BuildConfig
import com.example.data.model.CameraMotionType
import com.example.data.model.SceneItem
import com.example.data.model.TransitionType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID
import java.util.concurrent.TimeUnit

class GeminiStoryService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    // Model per user request & metadata: models/gemini-3.8-flash (or gemini-3.5-flash)
    private val modelName = "gemini-3.5-flash"

    fun hasApiKey(): Boolean {
        val key = getEffectiveApiKey()
        return key.isNotBlank() && key != "MY_GEMINI_API_KEY"
    }

    fun getEffectiveApiKey(): String {
        return try {
            BuildConfig.GEMINI_API_KEY
        } catch (_: Exception) {
            ""
        }
    }

    suspend fun generateStory(
        promptIdea: String,
        genre: String,
        projectId: String,
        targetDurationSeconds: Int,
        language: String,
        sceneCount: Int = 5
    ): Result<GeneratedStoryResult> = withContext(Dispatchers.IO) {
        val apiKey = getEffectiveApiKey()
        if (!hasApiKey()) {
            return@withContext Result.failure(
                IllegalStateException("لم يتم ضبط مفتاح Gemini API في لوحة الأسرار (Secrets Panel). يمكنك استخدام قوالب القصص الجاهزة أو المحرر اليدوي مجاناً.")
            )
        }

        try {
            val systemInstruction = """
                You are a senior anime scriptwriter and animation storyboard director.
                Create an anime story structured specifically for a video with target duration of $targetDurationSeconds seconds.
                Language: $language (if 'ar', write title, logline, synopsis, narration, and dialogue in natural expressive Arabic).
                Output MUST be valid JSON with this exact structure:
                {
                  "title": "String",
                  "logline": "String",
                  "synopsis": "String",
                  "characterName": "String",
                  "characterDescription": "String",
                  "scenes": [
                    {
                      "title": "String",
                      "durationSeconds": 6.0,
                      "narration": "String",
                      "dialogue": "String",
                      "subtitle": "String",
                      "cameraMotion": "KEN_BURNS_ZOOM_IN" // options: STATIC, KEN_BURNS_ZOOM_IN, KEN_BURNS_ZOOM_OUT, PAN_LEFT_TO_RIGHT, PAN_RIGHT_TO_LEFT, DRAMATIC_SHAKE
                    }
                  ]
                }
                Provide exactly $sceneCount scenes.
            """.trimIndent()

            val userContent = "Story Idea: $promptIdea\nGenre: $genre\nTarget Duration: $targetDurationSeconds seconds\nScene count: $sceneCount."

            val jsonBody = JSONObject().apply {
                put("contents", JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().apply { put("text", "$systemInstruction\n\n$userContent") })
                        })
                    })
                })
                put("generationConfig", JSONObject().apply {
                    put("responseMimeType", "application/json")
                    put("temperature", 0.7)
                })
            }

            val request = Request.Builder()
                .url("https://generativelanguage.googleapis.com/v1beta/models/$modelName:generateContent?key=$apiKey")
                .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
                .build()

            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                val errBody = response.body?.string() ?: ""
                return@withContext Result.failure(Exception("خطأ في مزود الذكاء (${response.code}): $errBody"))
            }

            val responseBody = response.body?.string() ?: ""
            val jsonRoot = JSONObject(responseBody)
            val candidates = jsonRoot.optJSONArray("candidates")
            if (candidates == null || candidates.length() == 0) {
                return@withContext Result.failure(Exception("لم يرجع مزود الذكاء أي استجابة صالحة"))
            }

            val contentObj = candidates.getJSONObject(0).getJSONObject("content")
            val parts = contentObj.getJSONArray("parts")
            val text = parts.getJSONObject(0).getString("text")

            val storyJson = JSONObject(text)
            val title = storyJson.optString("title", "قصة أنمي جديدة")
            val logline = storyJson.optString("logline", "")
            val synopsis = storyJson.optString("synopsis", "")
            val charName = storyJson.optString("characterName", "رين")
            val charDesc = storyJson.optString("characterDescription", "")

            val scenesArray = storyJson.optJSONArray("scenes") ?: JSONArray()
            val scenes = mutableListOf<SceneItem>()
            val avgDuration = (targetDurationSeconds.toFloat() / scenesArray.length().coerceAtLeast(1)).coerceIn(4f, 30f)

            for (i in 0 until scenesArray.length()) {
                val sObj = scenesArray.getJSONObject(i)
                val sTitle = sObj.optString("title", "مشهد ${i + 1}")
                val sDuration = sObj.optDouble("durationSeconds", avgDuration.toDouble()).toFloat()
                val sNarration = sObj.optString("narration", "")
                val sDialogue = sObj.optString("dialogue", "")
                val sSubtitle = sObj.optString("subtitle", sNarration.ifEmpty { sDialogue })
                val sMotionStr = sObj.optString("cameraMotion", "KEN_BURNS_ZOOM_IN")
                val sMotion = try {
                    CameraMotionType.valueOf(sMotionStr)
                } catch (_: Exception) {
                    CameraMotionType.KEN_BURNS_ZOOM_IN
                }

                scenes.add(
                    SceneItem(
                        id = UUID.randomUUID().toString(),
                        projectId = projectId,
                        orderIndex = i,
                        title = sTitle,
                        durationSeconds = sDuration,
                        backgroundType = "DRAWABLE_RES",
                        backgroundValue = "sample_bg_neo_city",
                        characterId = "char_ren_hero",
                        characterLayerType = if (i % 2 == 1) "DRAWABLE_RES" else "NONE",
                        characterLayerValue = "sample_character_ren",
                        narrationText = sNarration,
                        dialogueText = sDialogue,
                        subtitleText = sSubtitle,
                        cameraMotion = sMotion,
                        transitionIn = TransitionType.FADE,
                        transitionOut = TransitionType.FADE
                    )
                )
            }

            Result.success(
                GeneratedStoryResult(
                    title = title,
                    logline = logline,
                    synopsis = synopsis,
                    characterName = charName,
                    characterDescription = charDesc,
                    scenes = scenes
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
