package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.StudioBottomBar
import com.example.ui.components.StudioTopBar
import com.example.ui.navigation.Screen
import com.example.ui.screens.*
import com.example.ui.theme.DarkBg
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.StudioViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                AnimeStudioApp()
            }
        }
    }
}

@Composable
fun AnimeStudioApp(
    viewModel: StudioViewModel = viewModel()
) {
    val isArabic by viewModel.isArabic.collectAsStateWithLifecycle()
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val selectedProject by viewModel.selectedProject.collectAsStateWithLifecycle()

    val layoutDirection = if (isArabic) LayoutDirection.Rtl else LayoutDirection.Ltr

    // System back button handler
    BackHandler(enabled = currentScreen != Screen.Home) {
        viewModel.navigateBack()
    }

    CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
        Scaffold(
            containerColor = DarkBg,
            topBar = {
                StudioTopBar(
                    currentScreen = currentScreen,
                    selectedProject = selectedProject,
                    isArabic = isArabic,
                    onBackClick = { viewModel.navigateBack() },
                    onToggleLanguage = { viewModel.toggleLanguage() },
                    onNavigateToSettings = { viewModel.navigate(Screen.Settings) },
                    onNavigateToHelp = { viewModel.navigate(Screen.Help) }
                )
            },
            bottomBar = {
                StudioBottomBar(
                    currentScreen = currentScreen,
                    isArabic = isArabic,
                    onNavigate = { screen -> viewModel.navigate(screen) }
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (currentScreen) {
                    Screen.Home -> HomeScreen(viewModel = viewModel)
                    Screen.Wizard -> WizardScreen(viewModel = viewModel)
                    Screen.Projects -> ProjectListScreen(viewModel = viewModel)
                    Screen.Characters -> CharacterLibraryScreen(viewModel = viewModel)
                    Screen.CharacterDetail -> CharacterLibraryScreen(viewModel = viewModel)
                    Screen.StoryStudio -> StoryStudioScreen(viewModel = viewModel)
                    Screen.SceneStudio -> SceneStudioScreen(viewModel = viewModel)
                    Screen.Timeline -> TimelineScreen(viewModel = viewModel)
                    Screen.AudioStudio -> AudioStudioScreen(viewModel = viewModel)
                    Screen.SubtitleStudio -> SubtitleStudioScreen(viewModel = viewModel)
                    Screen.ExportCenter -> ExportCenterScreen(viewModel = viewModel)
                    Screen.YouTubeCenter -> YouTubeCenterScreen(viewModel = viewModel)
                    Screen.Settings -> SettingsScreen(viewModel = viewModel)
                    Screen.Help -> HelpScreen(viewModel = viewModel)
                }
            }
        }
    }
}
