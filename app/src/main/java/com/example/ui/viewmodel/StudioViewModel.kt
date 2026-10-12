package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ai.GeminiStoryService
import com.example.data.ai.StoryTemplateProvider
import com.example.data.audio.LocalTtsManager
import com.example.data.local.AnimeDatabase
import com.example.data.model.*
import com.example.data.render.SrtExporter
import com.example.data.render.VideoExportEngine
import com.example.data.repository.AnimeRepository
import com.example.ui.navigation.Screen
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import java.util.UUID

class StudioViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AnimeDatabase.getInstance(application)
    private val repository = AnimeRepository(database)
    val ttsManager = LocalTtsManager(application)
    val geminiService = GeminiStoryService()
    val videoExportEngine = VideoExportEngine(application)

    // Language state: true = Arabic, false = English
    private val _isArabic = MutableStateFlow(true)
    val isArabic: StateFlow<Boolean> = _isArabic

    // Navigation state
    private val _currentScreen = MutableStateFlow<Screen>(Screen.Home)
    val currentScreen: StateFlow<Screen> = _currentScreen
    private val backStack = mutableListOf<Screen>()

    // Projects & Selection
    val allProjects: StateFlow<List<Project>> = repository.allProjects
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedProject = MutableStateFlow<Project?>(null)
    val selectedProject: StateFlow<Project?> = _selectedProject

    // Scenes of the selected project
    private val _currentScenes = MutableStateFlow<List<SceneItem>>(emptyList())
    val currentScenes: StateFlow<List<SceneItem>> = _currentScenes

    private val _selectedScene = MutableStateFlow<SceneItem?>(null)
    val selectedScene: StateFlow<SceneItem?> = _selectedScene

    // Characters
    val allCharacters: StateFlow<List<CharacterProfile>> = repository.allCharacters
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedCharacter = MutableStateFlow<CharacterProfile?>(null)
    val selectedCharacter: StateFlow<CharacterProfile?> = _selectedCharacter

    // AI & Story Generation
    private val _isGeneratingStory = MutableStateFlow(false)
    val isGeneratingStory: StateFlow<Boolean> = _isGeneratingStory

    private val _storyGenerationError = MutableStateFlow<String?>(null)
    val storyGenerationError: StateFlow<String?> = _storyGenerationError

    // Export Job
    private val _exportState = MutableStateFlow(ExportJobState())
    val exportState: StateFlow<ExportJobState> = _exportState
    private var exportJob: Job? = null

    // TTS Speaking State
    val isTtsSpeaking: StateFlow<Boolean> = ttsManager.isSpeaking
    val isArabicTtsSupported: StateFlow<Boolean> = ttsManager.isArabicSupported
    val isEnglishTtsSupported: StateFlow<Boolean> = ttsManager.isEnglishSupported

    init {
        viewModelScope.launch {
            repository.ensureDefaultDataLoaded()
            // Observe projects and auto-select the latest/first if none selected
            allProjects.collectLatest { projects ->
                if (_selectedProject.value == null && projects.isNotEmpty()) {
                    selectProject(projects.first())
                }
            }
        }
    }

    fun toggleLanguage() {
        _isArabic.value = !_isArabic.value
    }

    fun navigate(screen: Screen) {
        if (_currentScreen.value != screen) {
            backStack.add(_currentScreen.value)
            _currentScreen.value = screen
        }
    }

    fun navigateBack(): Boolean {
        if (backStack.isNotEmpty()) {
            _currentScreen.value = backStack.removeAt(backStack.size - 1)
            return true
        }
        if (_currentScreen.value != Screen.Home) {
            _currentScreen.value = Screen.Home
            return true
        }
        return false
    }

    fun selectProject(project: Project) {
        _selectedProject.value = project
        viewModelScope.launch {
            repository.getScenesForProject(project.id).collectLatest { scenes ->
                _currentScenes.value = scenes
                if (_selectedScene.value == null || scenes.none { it.id == _selectedScene.value?.id }) {
                    _selectedScene.value = scenes.firstOrNull()
                }
            }
        }
    }

    fun selectScene(scene: SceneItem) {
        _selectedScene.value = scene
    }

    fun selectCharacter(character: CharacterProfile?) {
        _selectedCharacter.value = character
    }

    fun createProject(
        title: String,
        aspectRatio: AspectRatio,
        targetDurationSeconds: Int,
        style: AnimeStyle,
        language: String,
        narrationType: NarrationType
    ) {
        viewModelScope.launch {
            val (width, height) = when (aspectRatio) {
                AspectRatio.SHORTS_9_16 -> 1080 to 1920
                AspectRatio.YOUTUBE_16_9 -> 1920 to 1080
                AspectRatio.SQUARE_1_1 -> 1080 to 1080
            }

            val newProject = Project(
                id = UUID.randomUUID().toString(),
                title = title.ifBlank { "مشروع أنمي جديد" },
                aspectRatio = aspectRatio,
                targetDurationSeconds = targetDurationSeconds,
                style = style,
                language = language,
                narrationType = narrationType,
                resolutionWidth = width,
                resolutionHeight = height,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            repository.insertProject(newProject)

            // Create initial default scenes for this project
            val sceneCount = if (targetDurationSeconds >= 300) 10 else if (targetDurationSeconds >= 60) 4 else 2
            val durationPerScene = (targetDurationSeconds.toFloat() / sceneCount).coerceIn(4f, 25f)
            val scenes = (0 until sceneCount).map { i ->
                SceneItem(
                    id = UUID.randomUUID().toString(),
                    projectId = newProject.id,
                    orderIndex = i,
                    title = "المشهد ${i + 1}",
                    durationSeconds = durationPerScene,
                    backgroundType = "DRAWABLE_RES",
                    backgroundValue = "sample_bg_neo_city",
                    characterId = "char_ren_hero",
                    characterLayerType = if (i % 2 == 1) "DRAWABLE_RES" else "NONE",
                    characterLayerValue = "sample_character_ren",
                    narrationText = if (language == "ar") "في هذه اللحظة، تبدأ تفاصيل القصة بالتشكل..." else "At this moment, our journey begins...",
                    dialogueText = "",
                    subtitleText = if (language == "ar") "في هذه اللحظة، تبدأ تفاصيل القصة بالتشكل..." else "At this moment, our journey begins...",
                    cameraMotion = CameraMotionType.KEN_BURNS_ZOOM_IN,
                    transitionIn = TransitionType.FADE,
                    transitionOut = TransitionType.FADE
                )
            }
            repository.insertScenes(scenes)
            selectProject(newProject)
            navigate(Screen.Timeline)
        }
    }

    fun duplicateProject(project: Project) {
        viewModelScope.launch {
            val copyTitle = "${project.title} (نسخة)"
            val newId = repository.duplicateProject(project.id, copyTitle)
            val newProj = repository.getProjectById(newId)
            if (newProj != null) {
                selectProject(newProj)
            }
        }
    }

    fun deleteProject(projectId: String) {
        viewModelScope.launch {
            repository.deleteProject(projectId)
            if (_selectedProject.value?.id == projectId) {
                _selectedProject.value = null
                _currentScenes.value = emptyList()
            }
        }
    }

    fun addSceneToCurrentProject(
        title: String = "مشهد جديد",
        duration: Float = 5.0f,
        motion: CameraMotionType = CameraMotionType.KEN_BURNS_ZOOM_IN,
        background: String = "sample_bg_neo_city"
    ) {
        val project = _selectedProject.value ?: return
        viewModelScope.launch {
            val currentList = _currentScenes.value
            val newOrder = currentList.size
            val newScene = SceneItem(
                id = UUID.randomUUID().toString(),
                projectId = project.id,
                orderIndex = newOrder,
                title = title,
                durationSeconds = duration,
                backgroundType = "DRAWABLE_RES",
                backgroundValue = background,
                characterId = "char_ren_hero",
                characterLayerType = "DRAWABLE_RES",
                characterLayerValue = "sample_character_ren",
                narrationText = "",
                subtitleText = "",
                cameraMotion = motion
            )
            repository.insertScene(newScene)
            _selectedScene.value = newScene
        }
    }

    fun updateScene(scene: SceneItem) {
        viewModelScope.launch {
            repository.updateScene(scene)
            if (_selectedScene.value?.id == scene.id) {
                _selectedScene.value = scene
            }
        }
    }

    fun deleteScene(scene: SceneItem) {
        viewModelScope.launch {
            repository.deleteScene(scene)
            if (_selectedScene.value?.id == scene.id) {
                _selectedScene.value = _currentScenes.value.firstOrNull { it.id != scene.id }
            }
        }
    }

    fun duplicateScene(scene: SceneItem) {
        viewModelScope.launch {
            repository.duplicateScene(scene)
        }
    }

    fun saveCharacter(character: CharacterProfile) {
        viewModelScope.launch {
            repository.insertCharacter(character)
            _selectedCharacter.value = character
        }
    }

    fun deleteCharacter(characterId: String) {
        viewModelScope.launch {
            repository.deleteCharacter(characterId)
            if (_selectedCharacter.value?.id == characterId) {
                _selectedCharacter.value = null
            }
        }
    }

    fun generateStory(
        prompt: String,
        genre: String,
        targetDurationSeconds: Int,
        useAi: Boolean
    ) {
        val currentProj = _selectedProject.value ?: return
        _isGeneratingStory.value = true
        _storyGenerationError.value = null

        viewModelScope.launch {
            if (useAi && geminiService.hasApiKey()) {
                val result = geminiService.generateStory(
                    promptIdea = prompt,
                    genre = genre,
                    projectId = currentProj.id,
                    targetDurationSeconds = targetDurationSeconds,
                    language = currentProj.language
                )
                if (result.isSuccess) {
                    val story = result.getOrNull()!!
                    repository.updateProject(
                        currentProj.copy(
                            title = story.title,
                            description = story.synopsis,
                            targetDurationSeconds = targetDurationSeconds
                        )
                    )
                    repository.insertScenes(story.scenes)
                    _selectedProject.value = repository.getProjectById(currentProj.id)
                    _isGeneratingStory.value = false
                    navigate(Screen.Timeline)
                } else {
                    _storyGenerationError.value = result.exceptionOrNull()?.message ?: "خطأ أثناء توليد القصة"
                    _isGeneratingStory.value = false
                }
            } else {
                // Free-first instant offline template generation
                val story = StoryTemplateProvider.getTemplatesForGenre(
                    genre = genre,
                    projectId = currentProj.id,
                    targetDurationSeconds = targetDurationSeconds,
                    language = currentProj.language
                )
                repository.updateProject(
                    currentProj.copy(
                        title = story.title,
                        description = story.synopsis,
                        targetDurationSeconds = targetDurationSeconds
                    )
                )
                repository.insertScenes(story.scenes)
                _selectedProject.value = repository.getProjectById(currentProj.id)
                _isGeneratingStory.value = false
                navigate(Screen.Timeline)
            }
        }
    }

    fun speakNarration(text: String, lang: String = "ar") {
        if (text.isNotBlank()) {
            ttsManager.speak(text, lang)
        }
    }

    fun stopSpeaking() {
        ttsManager.stop()
    }

    fun startVideoExport(
        resolutionWidth: Int = 720,
        resolutionHeight: Int = 1280,
        fps: Int = 30,
        burnSubtitles: Boolean = true
    ) {
        val proj = _selectedProject.value ?: return
        val scenes = _currentScenes.value
        if (scenes.isEmpty()) {
            _exportState.value = ExportJobState(
                errorMessage = if (_isArabic.value) "لا يمكن التصدير بدون أي مشاهد" else "Cannot export without scenes"
            )
            return
        }

        exportJob?.cancel()
        _exportState.value = ExportJobState(isExporting = true, statusMessage = "جاري تحضير ملف الفيديو...")

        exportJob = viewModelScope.launch {
            val outputDir = getApplication<Application>().getExternalFilesDir(null) ?: getApplication<Application>().filesDir
            val fileName = "anime_video_${System.currentTimeMillis()}.mp4"
            val outputFile = File(outputDir, fileName)

            val result = videoExportEngine.renderVideo(
                project = proj,
                scenes = scenes,
                outputFile = outputFile,
                resolutionWidth = resolutionWidth,
                resolutionHeight = resolutionHeight,
                fps = fps,
                burnSubtitles = burnSubtitles
            ) { progress, currentScene, totalScenes, status ->
                _exportState.value = ExportJobState(
                    isExporting = true,
                    progress = progress,
                    currentScene = currentScene,
                    totalScenes = totalScenes,
                    statusMessage = status
                )
            }

            if (result.isSuccess) {
                // Also export SRT file alongside it
                val srtFile = File(outputDir, "subtitles_${System.currentTimeMillis()}.srt")
                SrtExporter.exportToFile(scenes, srtFile)

                _exportState.value = ExportJobState(
                    isExporting = false,
                    progress = 1.0f,
                    statusMessage = if (_isArabic.value) "تم تصدير الفيديو بنجاح بصيغة MP4!" else "MP4 Video exported successfully!",
                    outputFileUri = outputFile.absolutePath
                )
            } else {
                _exportState.value = ExportJobState(
                    isExporting = false,
                    errorMessage = result.exceptionOrNull()?.message ?: "فشل تصدير الفيديو"
                )
            }
        }
    }

    fun cancelExport() {
        exportJob?.cancel()
        _exportState.value = ExportJobState(
            isExporting = false,
            errorMessage = if (_isArabic.value) "تم إلغاء التصدير" else "Export cancelled"
        )
    }

    override fun onCleared() {
        super.onCleared()
        ttsManager.shutdown()
    }
}
