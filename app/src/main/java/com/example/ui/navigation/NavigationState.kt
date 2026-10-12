package com.example.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Wizard : Screen("wizard")
    object Projects : Screen("projects")
    object Characters : Screen("characters")
    object CharacterDetail : Screen("character_detail")
    object StoryStudio : Screen("story_studio")
    object SceneStudio : Screen("scene_studio")
    object Timeline : Screen("timeline")
    object AudioStudio : Screen("audio_studio")
    object SubtitleStudio : Screen("subtitle_studio")
    object ExportCenter : Screen("export_center")
    object YouTubeCenter : Screen("youtube_center")
    object Settings : Screen("settings")
    object Help : Screen("help")
}
