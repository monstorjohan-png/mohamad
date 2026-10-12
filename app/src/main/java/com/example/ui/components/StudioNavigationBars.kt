package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Project
import com.example.ui.navigation.Screen
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudioTopBar(
    currentScreen: Screen,
    selectedProject: Project?,
    isArabic: Boolean,
    onBackClick: () -> Unit,
    onToggleLanguage: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToHelp: () -> Unit
) {
    TopAppBar(
        title = {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (isArabic) "ستوديو أنمي AI" else "Anime Studio AI",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(AnimePurple, AnimeCyan)
                                )
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "PRO",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }
                }
                if (selectedProject != null) {
                    Text(
                        text = selectedProject.title,
                        fontSize = 12.sp,
                        color = AnimeCyanLight,
                        maxLines = 1
                    )
                }
            }
        },
        navigationIcon = {
            if (currentScreen != Screen.Home) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.testTag("nav_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimary
                    )
                }
            }
        },
        actions = {
            // Language Toggle Button (عربي / EN)
            OutlinedButton(
                onClick = onToggleLanguage,
                shape = RoundedCornerShape(8.dp),
                border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.linearGradient(listOf(AnimePurple, AnimeCyan))),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                modifier = Modifier
                    .height(34.dp)
                    .testTag("lang_toggle_button")
            ) {
                Text(
                    text = if (isArabic) "English" else "عربي",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
            IconButton(
                onClick = onNavigateToHelp,
                modifier = Modifier.testTag("nav_help_button")
            ) {
                Icon(
                    imageVector = Icons.Outlined.HelpOutline,
                    contentDescription = "Help",
                    tint = TextSecondary
                )
            }
            IconButton(
                onClick = onNavigateToSettings,
                modifier = Modifier.testTag("nav_settings_button")
            ) {
                Icon(
                    imageVector = Icons.Outlined.Settings,
                    contentDescription = "Settings",
                    tint = TextSecondary
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = DarkBg,
            scrolledContainerColor = DarkSurface
        )
    )
}

@Composable
fun StudioBottomBar(
    currentScreen: Screen,
    isArabic: Boolean,
    onNavigate: (Screen) -> Unit
) {
    NavigationBar(
        containerColor = DarkSurface,
        tonalElevation = 8.dp,
        modifier = Modifier
            .windowInsetsPadding(WindowInsets.navigationBars)
            .border(width = 0.5.dp, color = BorderSubtle)
            .testTag("main_bottom_nav")
    ) {
        val items = listOf(
            NavDestination(
                screen = Screen.Home,
                labelAr = "الرئيسية",
                labelEn = "Home",
                selectedIcon = Icons.Filled.Home,
                unselectedIcon = Icons.Outlined.Home
            ),
            NavDestination(
                screen = Screen.Projects,
                labelAr = "مشاريعي",
                labelEn = "Projects",
                selectedIcon = Icons.Filled.Folder,
                unselectedIcon = Icons.Outlined.Folder
            ),
            NavDestination(
                screen = Screen.Timeline,
                labelAr = "المخطط",
                labelEn = "Timeline",
                selectedIcon = Icons.Filled.ViewTimeline,
                unselectedIcon = Icons.Outlined.ViewTimeline
            ),
            NavDestination(
                screen = Screen.SceneStudio,
                labelAr = "المشاهد",
                labelEn = "Scenes",
                selectedIcon = Icons.Filled.MovieCreation,
                unselectedIcon = Icons.Outlined.MovieCreation
            ),
            NavDestination(
                screen = Screen.ExportCenter,
                labelAr = "تصدير",
                labelEn = "Export",
                selectedIcon = Icons.Filled.CloudDownload,
                unselectedIcon = Icons.Outlined.CloudDownload
            )
        )

        items.forEach { dest ->
            val isSelected = currentScreen == dest.screen
            NavigationBarItem(
                selected = isSelected,
                onClick = { onNavigate(dest.screen) },
                icon = {
                    Icon(
                        imageVector = if (isSelected) dest.selectedIcon else dest.unselectedIcon,
                        contentDescription = if (isArabic) dest.labelAr else dest.labelEn
                    )
                },
                label = {
                    Text(
                        text = if (isArabic) dest.labelAr else dest.labelEn,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.White,
                    selectedTextColor = AnimePurpleLight,
                    indicatorColor = AnimePurple,
                    unselectedIconColor = TextSecondary,
                    unselectedTextColor = TextSecondary
                ),
                modifier = Modifier.testTag("nav_item_${dest.screen.route}")
            )
        }
    }
}

data class NavDestination(
    val screen: Screen,
    val labelAr: String,
    val labelEn: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)
