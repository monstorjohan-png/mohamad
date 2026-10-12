package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

enum class AspectRatio(val labelAr: String, val labelEn: String, val ratioWidth: Int, val ratioHeight: Int) {
    SHORTS_9_16("يوتيوب شورتس (9:16)", "YouTube Shorts (9:16)", 9, 16),
    YOUTUBE_16_9("يوتيوب قياسي (16:9)", "Standard YouTube (16:9)", 16, 9),
    SQUARE_1_1("مربع (1:1)", "Square (1:1)", 1, 1);

    val displayRatio: Float
        get() = ratioWidth.toFloat() / ratioHeight.toFloat()
}

enum class AnimeStyle(val labelAr: String, val labelEn: String, val descriptionAr: String) {
    ANIME_2D("أنمي تقليدي 2D", "Classic 2D Anime", "رسوم متحركة يابانية كلاسيكية بإضاءة درامية"),
    MANGA_PANELS("إطارات مانغا متحركة", "Manga Motion Panels", "لوحات مانغا بالأبيض والأسود مع مؤثرات حركة"),
    CARTOON("كرتون كلاسيكي", "Classic Cartoon", "ألوان زاهية وخطوط عريضة ممتعة"),
    STORYBOOK("رسوم حكايات مصورة", "Storybook Illustration", "أسلوب قصصي دافئ مناسب للحكايات"),
    CINEMATIC_SLIDESHOW("عرض سينمائي ديناميكي", "Cinematic Motion", "حركات كاميرا كين بيرنز وإضاءة فخمة"),
    FANTASY_ORIGINAL("أنمي خيالي ملحمي", "Epic Fantasy Anime", "عوالم خيالية ساحرة وشخصيات ميثولوجية"),
    USER_CUSTOM("أسلوب فني مخصص", "Custom Imported Artwork", "استخدام رسومات وتصاميم الفنان الخاصة")
}

enum class CameraMotionType(val labelAr: String, val labelEn: String) {
    STATIC("ثابت", "Static"),
    KEN_BURNS_ZOOM_IN("تقريب كين بيرنز (Zoom In)", "Ken Burns Zoom In"),
    KEN_BURNS_ZOOM_OUT("ابتعاد كين بيرنز (Zoom Out)", "Ken Burns Zoom Out"),
    PAN_LEFT_TO_RIGHT("تحريك أفقي (يسار ليمين)", "Pan Left to Right"),
    PAN_RIGHT_TO_LEFT("تحريك أفقي (يمين ليسار)", "Pan Right to Left"),
    DRAMATIC_SHAKE("اهتزاز درامي (Action Shake)", "Dramatic Shake")
}

enum class TransitionType(val labelAr: String, val labelEn: String) {
    CUT("قطع مباشر (Cut)", "Cut"),
    FADE("تلاشي تدريجي (Fade)", "Fade"),
    SLIDE_LEFT("انزلاق لليسار", "Slide Left"),
    SLIDE_RIGHT("انزلاق لليمين", "Slide Right"),
    ZOOM_IN("تقريب سريع", "Zoom In")
}

enum class NarrationType(val labelAr: String, val labelEn: String) {
    LOCAL_TTS("قارئ النصوص المحلي (Android TTS)", "Local Android TTS"),
    RECORDED_AUDIO("تسجيل صوتي مستورد", "Imported Audio"),
    EXTERNAL_AI("مزود ذكاء خارجي اختياري", "Configured AI Voice"),
    NONE("بدون تعليق صوتي", "No Narration")
}

@Entity(tableName = "projects")
data class Project(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String = "",
    val aspectRatio: AspectRatio = AspectRatio.SHORTS_9_16,
    val targetDurationSeconds: Int = 60,
    val style: AnimeStyle = AnimeStyle.ANIME_2D,
    val language: String = "ar",
    val narrationType: NarrationType = NarrationType.LOCAL_TTS,
    val fps: Int = 30,
    val resolutionWidth: Int = 1080,
    val resolutionHeight: Int = 1920,
    val isSample: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "scenes")
data class SceneItem(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val projectId: String,
    val orderIndex: Int,
    val title: String,
    val durationSeconds: Float = 5.0f,
    val backgroundType: String = "DRAWABLE_RES", // DRAWABLE_RES, COLOR, FILE_PATH
    val backgroundValue: String = "sample_bg_neo_city",
    val characterId: String? = null,
    val characterLayerType: String = "DRAWABLE_RES", // NONE, DRAWABLE_RES, FILE_PATH
    val characterLayerValue: String? = "sample_character_ren",
    val narrationText: String = "",
    val dialogueText: String = "",
    val subtitleText: String = "",
    val cameraMotion: CameraMotionType = CameraMotionType.KEN_BURNS_ZOOM_IN,
    val transitionIn: TransitionType = TransitionType.FADE,
    val transitionOut: TransitionType = TransitionType.FADE,
    val layerScale: Float = 1.0f,
    val layerOffsetX: Float = 0f,
    val layerOffsetY: Float = 0f
)

@Entity(tableName = "characters")
data class CharacterProfile(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String = "",
    val personality: String = "شجاع وحماسي",
    val ageGroup: String = "شاب (18 سنة)",
    val clothing: String = "سترة نيون تكنولوجية سوداء",
    val hairAndEyes: String = "شعر نيلي وعيون كهرمانية",
    val colorPaletteHex: String = "#8B5CF6,#06B6D4,#F59E0B",
    val voicePitch: Float = 1.0f,
    val voiceSpeed: Float = 1.0f,
    val imageType: String = "DRAWABLE_RES", // DRAWABLE_RES, FILE_PATH
    val imageValue: String = "sample_character_ren",
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

data class SubtitleConfig(
    val fontSizeSp: Float = 22f,
    val textColorHex: String = "#FFFFFF",
    val strokeColorHex: String = "#000000",
    val strokeWidth: Float = 3f,
    val backgroundColorHex: String = "#99000000",
    val positionBottomMarginDp: Float = 36f,
    val burnIntoVideo: Boolean = true
)

data class AudioTrackConfig(
    val bgmEnabled: Boolean = true,
    val bgmVolume: Float = 0.25f,
    val bgmType: String = "SYNTH_NEON",
    val narrationVolume: Float = 1.0f,
    val duckingEnabled: Boolean = true,
    val sfxEnabled: Boolean = true
)

data class ExportJobState(
    val isExporting: Boolean = false,
    val progress: Float = 0f,
    val currentScene: Int = 0,
    val totalScenes: Int = 0,
    val statusMessage: String = "",
    val outputFileUri: String? = null,
    val errorMessage: String? = null
)
