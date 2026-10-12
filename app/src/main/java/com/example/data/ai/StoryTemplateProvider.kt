package com.example.data.ai

import com.example.data.model.CameraMotionType
import com.example.data.model.SceneItem
import com.example.data.model.TransitionType
import java.util.UUID

data class GeneratedStoryResult(
    val title: String,
    val logline: String,
    val synopsis: String,
    val characterName: String,
    val characterDescription: String,
    val scenes: List<SceneItem>
)

object StoryTemplateProvider {

    fun getTemplatesForGenre(
        genre: String,
        projectId: String,
        targetDurationSeconds: Int,
        language: String
    ): GeneratedStoryResult {
        return when (genre) {
            "SCIENCE" -> createScienceStory(projectId, targetDurationSeconds, language)
            "SHONEN" -> createShonenStory(projectId, targetDurationSeconds, language)
            "COMEDY" -> createComedyStory(projectId, targetDurationSeconds, language)
            "MYSTERY" -> createMysteryStory(projectId, targetDurationSeconds, language)
            else -> createCyberpunkStory(projectId, targetDurationSeconds, language)
        }
    }

    private fun createCyberpunkStory(projectId: String, targetDuration: Int, lang: String): GeneratedStoryResult {
        val sceneCount = if (targetDuration >= 300) 12 else if (targetDuration >= 60) 6 else 3
        val avgDuration = (targetDuration.toFloat() / sceneCount).coerceIn(4f, 25f)

        val isAr = lang == "ar"
        val title = if (isAr) "شرارة النيون: تمرد الرقاقات" else "Neon Spark: The Chip Revolt"
        val logline = if (isAr) "مهندس أنمي شاب يكتشف ذكاءً اصطناعياً قديماً يغير مصير مدينة السايبربانك." else "A young anime technician uncovers an ancient AI changing the fate of Neo-Tokyo."
        val synopsis = if (isAr) "في أزقة نيو-طوكيو الممطرة، يواجه رين أبراج الشركات العملاقة لحماية حلمه في حرية الإبداع." else "In rainy Neo-Tokyo, Ren confronts megacorporations to protect his dream of creative freedom."

        val sceneTitles = listOf(
            "الغسق فوق المدينة الرقمية" to "Dusk Over Digital City",
            "إشارة غامضة في المخبأ" to "Mysterious Signal in the Hideout",
            "مطاردة على دراجة النيون" to "Neon Bike Pursuit",
            "المواجهة في محطة البيانات" to "Confrontation at Data Hub",
            "إيقاظ النواة البلورية" to "Awakening the Crystal Core",
            "فجر الحرية والإبداع" to "Dawn of Creative Freedom"
        )

        val scenes = (0 until sceneCount).map { index ->
            val titlePair = sceneTitles.getOrElse(index % sceneTitles.size) { "مشهد $index" to "Scene $index" }
            val sceneTitle = if (isAr) titlePair.first else titlePair.second
            val narration = if (isAr) {
                "حين اشتعلت أنوار النيون في الأفق، أدرك رين أن اللحظة الحاسمة قد حانت للبدء في خطوته الأولى."
            } else {
                "As neon lights ignited the skyline, Ren realized the decisive moment had arrived."
            }
            SceneItem(
                id = UUID.randomUUID().toString(),
                projectId = projectId,
                orderIndex = index,
                title = sceneTitle,
                durationSeconds = avgDuration,
                backgroundType = "DRAWABLE_RES",
                backgroundValue = "sample_bg_neo_city",
                characterId = "char_ren_hero",
                characterLayerType = if (index % 2 == 1) "DRAWABLE_RES" else "NONE",
                characterLayerValue = "sample_character_ren",
                narrationText = narration,
                dialogueText = if (isAr) "رين: لن نستسلم للظلام أبداً!" else "Ren: We will never surrender to darkness!",
                subtitleText = narration,
                cameraMotion = when (index % 4) {
                    0 -> CameraMotionType.KEN_BURNS_ZOOM_IN
                    1 -> CameraMotionType.PAN_LEFT_TO_RIGHT
                    2 -> CameraMotionType.KEN_BURNS_ZOOM_OUT
                    else -> CameraMotionType.DRAMATIC_SHAKE
                },
                transitionIn = TransitionType.FADE,
                transitionOut = TransitionType.FADE
            )
        }

        return GeneratedStoryResult(
            title = title,
            logline = logline,
            synopsis = synopsis,
            characterName = if (isAr) "رين" else "Ren",
            characterDescription = if (isAr) "مبرمج ومغامر أنمي شاب" else "Young anime programmer & adventurer",
            scenes = scenes
        )
    }

    private fun createScienceStory(projectId: String, targetDuration: Int, lang: String): GeneratedStoryResult {
        val sceneCount = if (targetDuration >= 300) 12 else if (targetDuration >= 60) 5 else 3
        val avgDuration = (targetDuration.toFloat() / sceneCount).coerceIn(5f, 25f)
        val isAr = lang == "ar"

        val title = if (isAr) "رحلة إلى قلب الثقب الأسود" else "Journey to the Heart of the Black Hole"
        val logline = if (isAr) "مستكشفة فلكية شابة تخوض رحلة مذهلة لشرح النسبية وفيزياء الزمكان بأسلوب أنمي شيق." else "A young astrophysicist embarks on a thrilling journey explaining spacetime and relativity."
        val synopsis = if (isAr) "قصة تعليمية ممتعة تشرح ظواهر الفيزياء الكونية والضوء والزمن بأسلوب بصري ساحر." else "An educational and visual anime story explaining cosmological wonders and physics."

        val scenes = (0 until sceneCount).map { index ->
            val sceneTitle = if (isAr) "المرحلة ${index + 1}: استكشاف آفاق الكون" else "Stage ${index + 1}: Cosmic Horizon"
            val narr = if (isAr) {
                "الجاذبية ليست مجرد قوة خفية، بل هي انحناء في نسيج الكون نفسه، حيث ينحني الضوء ويبطؤ الزمن."
            } else {
                "Gravity isn't merely a force, but the curved geometry of spacetime itself bending light."
            }
            SceneItem(
                id = UUID.randomUUID().toString(),
                projectId = projectId,
                orderIndex = index,
                title = sceneTitle,
                durationSeconds = avgDuration,
                backgroundType = "DRAWABLE_RES",
                backgroundValue = "sample_bg_neo_city",
                characterId = "char_ren_hero",
                characterLayerType = "DRAWABLE_RES",
                characterLayerValue = "sample_character_ren",
                narrationText = narr,
                dialogueText = if (isAr) "العلم هو المفتاح لفهم هذا الكون الشاسع!" else "Science is our key to comprehending the cosmos!",
                subtitleText = narr,
                cameraMotion = CameraMotionType.KEN_BURNS_ZOOM_IN,
                transitionIn = TransitionType.FADE,
                transitionOut = TransitionType.FADE
            )
        }

        return GeneratedStoryResult(
            title = title,
            logline = logline,
            synopsis = synopsis,
            characterName = if (isAr) "د. ليلى" else "Dr. Layla",
            characterDescription = if (isAr) "عالمة فيزياء فلكية عبقرية" else "Genius astrophysicist",
            scenes = scenes
        )
    }

    private fun createShonenStory(projectId: String, targetDuration: Int, lang: String): GeneratedStoryResult {
        val sceneCount = if (targetDuration >= 300) 10 else if (targetDuration >= 60) 5 else 3
        val avgDuration = (targetDuration.toFloat() / sceneCount).coerceIn(5f, 25f)
        val isAr = lang == "ar"

        return GeneratedStoryResult(
            title = if (isAr) "عهد التنانين السبعة" else "Oath of the Seven Dragons",
            logline = if (isAr) "محارب متدرب يسعى لتوحيد قوى العناصر وحماية قريته الجبلية." else "A trainee warrior seeks to unite elemental forces to protect his mountain village.",
            synopsis = if (isAr) "ملحمة حماسية حول الصداقة، التدريب الشاق، وتجاوز الحدود القصوى." else "An epic shonen tale of friendship, training, and breaking limits.",
            characterName = if (isAr) "كايتو" else "Kaito",
            characterDescription = if (isAr) "مقاتل النار الشغوف" else "Passionate fire warrior",
            scenes = (0 until sceneCount).map { i ->
                SceneItem(
                    id = UUID.randomUUID().toString(),
                    projectId = projectId,
                    orderIndex = i,
                    title = if (isAr) "التدريب الأسطوري - المرحلة ${i + 1}" else "Legendary Training - Stage ${i + 1}",
                    durationSeconds = avgDuration,
                    backgroundType = "DRAWABLE_RES",
                    backgroundValue = "sample_bg_neo_city",
                    characterId = "char_ren_hero",
                    characterLayerType = "DRAWABLE_RES",
                    characterLayerValue = "sample_character_ren",
                    narrationText = if (isAr) "العزيمة الصادقة لا تنكسر أمام أصعب التحديات." else "True determination never breaks before any challenge.",
                    dialogueText = if (isAr) "سأتجاوز حدودي اليوم!" else "I will surpass my limits today!",
                    subtitleText = if (isAr) "العزيمة الصادقة لا تنكسر أبداً!" else "True determination never breaks!",
                    cameraMotion = CameraMotionType.DRAMATIC_SHAKE
                )
            }
        )
    }

    private fun createComedyStory(projectId: String, targetDuration: Int, lang: String): GeneratedStoryResult {
        val sceneCount = if (targetDuration >= 60) 4 else 3
        val isAr = lang == "ar"
        return GeneratedStoryResult(
            title = if (isAr) "يوميات الروبوت الطاهي" else "Adventures of the Robot Chef",
            logline = if (isAr) "روبوت ذكاء اصطناعي يحاول طبخ أفضل وجبة رامن وتحدث معه كوارث مضحكة." else "An AI robot tries cooking the ultimate ramen with hilarious disasters.",
            synopsis = if (isAr) "مواقف كوميدية سريعة ومضحكة تناسب مقاطع يوتيوب شورتس القصيرة." else "Fast-paced anime comedy suitable for YouTube Shorts.",
            characterName = if (isAr) "روبو-رامن" else "Robo-Ramen",
            characterDescription = if (isAr) "روبوت أبيض مضحك يحب الفلفل الحار" else "Funny culinary droid who loves spicy food",
            scenes = (0 until sceneCount).map { i ->
                SceneItem(
                    id = UUID.randomUUID().toString(),
                    projectId = projectId,
                    orderIndex = i,
                    title = if (isAr) "كارثة في المطبخ #${i + 1}" else "Kitchen Mayhem #${i + 1}",
                    durationSeconds = 6f,
                    backgroundType = "DRAWABLE_RES",
                    backgroundValue = "sample_bg_neo_city",
                    characterLayerType = "DRAWABLE_RES",
                    characterLayerValue = "sample_character_ren",
                    narrationText = if (isAr) "لقد خلط الملح بالسكر مجدداً واندلع البركان!" else "He mixed salt with sugar again, causing an eruption!",
                    subtitleText = if (isAr) "روبو: أوبس! لم تكن هذه الوصفة الصحيحة!" else "Robo: Oops! That wasn't the recipe!",
                    cameraMotion = CameraMotionType.DRAMATIC_SHAKE
                )
            }
        )
    }

    private fun createMysteryStory(projectId: String, targetDuration: Int, lang: String): GeneratedStoryResult {
        val sceneCount = if (targetDuration >= 60) 5 else 3
        val isAr = lang == "ar"
        return GeneratedStoryResult(
            title = if (isAr) "لغز الساعة الرملية الفضية" else "Mystery of the Silver Hourglass",
            logline = if (isAr) "متحرية سرية تطارد أثراً غامضاً في متحف المخطوطات القديمة." else "A detective uncovers hidden clues in the ancient manuscripts vault.",
            synopsis = if (isAr) "أجواء غموض وتشويق سينمائي مع موسيقى تصويرية مشوقة." else "Cinematic mystery atmosphere with suspenseful pacing.",
            characterName = if (isAr) "مايا" else "Maya",
            characterDescription = if (isAr) "محققة أذكياء ذات معطف كلاسيكي" else "Keen detective with classic coat",
            scenes = (0 until sceneCount).map { i ->
                SceneItem(
                    id = UUID.randomUUID().toString(),
                    projectId = projectId,
                    orderIndex = i,
                    title = if (isAr) "الخيط السري #${i + 1}" else "Secret Clue #${i + 1}",
                    durationSeconds = 7f,
                    backgroundType = "DRAWABLE_RES",
                    backgroundValue = "sample_bg_neo_city",
                    characterLayerType = "NONE",
                    narrationText = if (isAr) "كل تفصيل في هذا المكان يخفي سراً ينتظر من يكشفه." else "Every detail here conceals a secret waiting to be uncovered.",
                    subtitleText = if (isAr) "كل تفصيل يخفي سراً عميقاً..." else "Every detail hides a deep secret...",
                    cameraMotion = CameraMotionType.KEN_BURNS_ZOOM_IN
                )
            }
        )
    }
}
