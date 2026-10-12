package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [Project::class, SceneItem::class, CharacterProfile::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(AnimeConverters::class)
abstract class AnimeDatabase : RoomDatabase() {
    abstract fun projectDao(): ProjectDao
    abstract fun sceneDao(): SceneDao
    abstract fun characterDao(): CharacterDao

    companion object {
        @Volatile
        private var INSTANCE: AnimeDatabase? = null

        fun getInstance(context: Context): AnimeDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AnimeDatabase::class.java,
                    "anime_studio_database.db"
                )
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // Pre-populate with default sample project and sample character
                        CoroutineScope(Dispatchers.IO).launch {
                            val database = getInstance(context)
                            seedDefaultData(database)
                        }
                    }
                })
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }

        suspend fun seedDefaultData(database: AnimeDatabase) {
            val sampleChar = CharacterProfile(
                id = "char_ren_hero",
                name = "رين (Ren)",
                description = "بطل أنمي شاب ذو قدرات حركية فائقة وقلم ضوئي بلوري",
                personality = "شجاع، ذكي، مفعم بالأمل",
                ageGroup = "شاب (18 سنة)",
                clothing = "سترة نيون تكنولوجية زرقاء وبرتقالية",
                hairAndEyes = "شعر نيلي وعيون كهرمانية متقدة",
                colorPaletteHex = "#8B5CF6,#06B6D4,#F59E0B",
                voicePitch = 1.0f,
                voiceSpeed = 1.0f,
                imageType = "DRAWABLE_RES",
                imageValue = "sample_character_ren",
                notes = "الشخصية الرئيسية في مشروع 'شرارة النيون: حكاية طوكيو المستقبلية'"
            )
            database.characterDao().insertCharacter(sampleChar)

            val sampleProject = Project(
                id = "sample_project_neon",
                title = "شرارة النيون: حكاية طوكيو المستقبلية",
                description = "مشروع تجريبي كامل لشورتس أنمي ملحمي في مدينة النيون مع حركات كين بيرنز وتعليق صوتي وترجمات.",
                aspectRatio = AspectRatio.SHORTS_9_16,
                targetDurationSeconds = 30,
                style = AnimeStyle.ANIME_2D,
                language = "ar",
                narrationType = NarrationType.LOCAL_TTS,
                fps = 30,
                resolutionWidth = 1080,
                resolutionHeight = 1920,
                isSample = true
            )
            database.projectDao().insertProject(sampleProject)

            val sampleScenes = listOf(
                SceneItem(
                    id = "scene_1_neon",
                    projectId = "sample_project_neon",
                    orderIndex = 0,
                    title = "أضواء نيو-طوكيو عند الغسق",
                    durationSeconds = 6.0f,
                    backgroundType = "DRAWABLE_RES",
                    backgroundValue = "sample_bg_neo_city",
                    characterId = null,
                    characterLayerType = "NONE",
                    characterLayerValue = null,
                    narrationText = "في أعماق مدينة نيو-طوكيو، حين تلتقي أزهار الكرز بشاشات النيون المتوهجة...",
                    dialogueText = "",
                    subtitleText = "في أعماق نيو-طوكيو، حيث تلتقي أزهار الكرز بالنيون المتوهج...",
                    cameraMotion = CameraMotionType.KEN_BURNS_ZOOM_IN,
                    transitionIn = TransitionType.FADE,
                    transitionOut = TransitionType.FADE
                ),
                SceneItem(
                    id = "scene_2_neon",
                    projectId = "sample_project_neon",
                    orderIndex = 1,
                    title = "ظهور البطل رين",
                    durationSeconds = 7.0f,
                    backgroundType = "DRAWABLE_RES",
                    backgroundValue = "sample_bg_neo_city",
                    characterId = "char_ren_hero",
                    characterLayerType = "DRAWABLE_RES",
                    characterLayerValue = "sample_character_ren",
                    narrationText = "يقف رين حاملاً قلماً سحرياً يبث الحياة في الرسوم المتحركة.",
                    dialogueText = "رين: حان وقت إيقاظ الحكاية!",
                    subtitleText = "رين: حان وقت إيقاظ الحكاية وإطلاق شرارة الإبداع!",
                    cameraMotion = CameraMotionType.KEN_BURNS_ZOOM_OUT,
                    transitionIn = TransitionType.SLIDE_RIGHT,
                    transitionOut = TransitionType.FADE
                ),
                SceneItem(
                    id = "scene_3_neon",
                    projectId = "sample_project_neon",
                    orderIndex = 2,
                    title = "إطلاق شرارة الخيال",
                    durationSeconds = 7.0f,
                    backgroundType = "DRAWABLE_RES",
                    backgroundValue = "sample_bg_neo_city",
                    characterId = "char_ren_hero",
                    characterLayerType = "DRAWABLE_RES",
                    characterLayerValue = "sample_character_ren",
                    narrationText = "كل فكرة ترسمها اليوم تصبح عالماً ينبض بالحياة غداً.",
                    dialogueText = "رين: لا حدود لما يمكننا ابتكاره معاً!",
                    subtitleText = "كل فكرة ترسمها اليوم تصبح عالماً ينبض بالحياة غداً.",
                    cameraMotion = CameraMotionType.PAN_LEFT_TO_RIGHT,
                    transitionIn = TransitionType.FADE,
                    transitionOut = TransitionType.FADE
                )
            )
            database.sceneDao().insertScenes(sampleScenes)
        }
    }
}
