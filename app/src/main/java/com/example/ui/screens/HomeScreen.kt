package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.AnimeStyle
import com.example.data.model.AspectRatio
import com.example.data.model.NarrationType
import com.example.data.model.Project
import com.example.ui.navigation.Screen
import com.example.ui.theme.*
import com.example.ui.viewmodel.StudioViewModel

@Composable
fun HomeScreen(
    viewModel: StudioViewModel,
    modifier: Modifier = Modifier
) {
    val isArabic by viewModel.isArabic.collectAsStateWithLifecycle()
    val projects by viewModel.allProjects.collectAsStateWithLifecycle()
    val selectedProject by viewModel.selectedProject.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 24.dp)
    ) {
        // Hero Banner: Create New Video & Quick Actions
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(
                                Color(0xFF3B1B68),
                                Color(0xFF162544),
                                DarkSurface
                            )
                        )
                    )
                    .border(1.dp, BorderSubtle, RoundedCornerShape(20.dp))
                    .padding(20.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isArabic) "ستوديو صناعة الأنمي" else "Anime Video Studio",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextPrimary
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0x3310B981))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = if (isArabic) "مجاني ومحلي 100%" else "Free & Local 100%",
                                color = AnimeGreen,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (isArabic)
                            "أنتج قصص أنمي كاملة وشورتس يوتيوب وفيديوهات سينمائية 5 دقائق بدون اشتراكات أو قيود اصطناعية."
                        else
                            "Produce anime stories, Shorts, and 5-minute videos with on-device rendering, no forced subscriptions.",
                        fontSize = 13.sp,
                        color = TextSecondary,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { viewModel.navigate(Screen.Wizard) },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = AnimePurple),
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("create_new_project_btn")
                        ) {
                            Icon(Icons.Filled.AddCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isArabic) "مشروع جديد" else "New Project",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }

                        OutlinedButton(
                            onClick = {
                                if (selectedProject != null) {
                                    viewModel.navigate(Screen.Timeline)
                                } else {
                                    viewModel.navigate(Screen.Wizard)
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.linearGradient(listOf(AnimeCyan, AnimePurple))),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("continue_editing_btn")
                        ) {
                            Icon(Icons.Filled.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp), tint = AnimeCyan)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isArabic) "متابعة التحرير" else "Continue",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }

        // Quick Starts (Shorts vs 5-Minute Story)
        item {
            Text(
                text = if (isArabic) "بدء سريع بنقرة واحدة" else "Quick Start Templates",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickStartCard(
                    title = if (isArabic) "يوتيوب شورتس" else "YouTube Shorts",
                    subtitle = if (isArabic) "9:16 عمودي • 30-60 ثانية" else "9:16 Vertical • 30-60s",
                    badge = "Shorts",
                    badgeColor = AnimePink,
                    icon = Icons.Filled.Smartphone,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        viewModel.createProject(
                            title = if (isArabic) "شورتس أنمي خاطف" else "Anime Quick Short",
                            aspectRatio = AspectRatio.SHORTS_9_16,
                            targetDurationSeconds = 30,
                            style = AnimeStyle.ANIME_2D,
                            language = if (isArabic) "ar" else "en",
                            narrationType = NarrationType.LOCAL_TTS
                        )
                    }
                )

                QuickStartCard(
                    title = if (isArabic) "قصة 5 دقائق كاملة" else "5-Minute Story",
                    subtitle = if (isArabic) "16:9 سينمائي • 300 ثانية" else "16:9 Cinema • 300s",
                    badge = "5 Min",
                    badgeColor = AnimeAmber,
                    icon = Icons.Filled.LiveTv,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        viewModel.createProject(
                            title = if (isArabic) "أسطورة نيو-طوكيو (5 دقائق)" else "Neo-Tokyo Legend (5 Min)",
                            aspectRatio = AspectRatio.YOUTUBE_16_9,
                            targetDurationSeconds = 300,
                            style = AnimeStyle.CINEMATIC_SLIDESHOW,
                            language = if (isArabic) "ar" else "en",
                            narrationType = NarrationType.LOCAL_TTS
                        )
                    }
                )
            }
        }

        // Feature Studios Navigation Grid
        item {
            Text(
                text = if (isArabic) "استوديوهات الإنتاج" else "Production Studios",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StudioToolCard(
                        title = if (isArabic) "مؤلف القصص" else "Story Writer",
                        subtitle = if (isArabic) "AI وقوالب جاهزة" else "AI & Templates",
                        icon = Icons.Filled.AutoStories,
                        accentColor = AnimePurple,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.navigate(Screen.StoryStudio) }
                    )
                    StudioToolCard(
                        title = if (isArabic) "مكتبة الشخصيات" else "Characters",
                        subtitle = if (isArabic) "أبطال أصليون وميزات" else "Original Heroes",
                        icon = Icons.Filled.Face,
                        accentColor = AnimeCyan,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.navigate(Screen.Characters) }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StudioToolCard(
                        title = if (isArabic) "استوديو الصوت" else "Audio Studio",
                        subtitle = if (isArabic) "TTS عربي وإنجليزي" else "Android TTS & Mix",
                        icon = Icons.Filled.Mic,
                        accentColor = AnimeAmber,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.navigate(Screen.AudioStudio) }
                    )
                    StudioToolCard(
                        title = if (isArabic) "الترجمات والشارات" else "Subtitles",
                        subtitle = if (isArabic) "محرر SRT وحرق فيديو" else "Burn & Export SRT",
                        icon = Icons.Filled.Subtitles,
                        accentColor = AnimePink,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.navigate(Screen.SubtitleStudio) }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StudioToolCard(
                        title = if (isArabic) "المخطط الزمني" else "Timeline",
                        subtitle = if (isArabic) "تنسيق المقاطع والمسارات" else "Multitrack Editor",
                        icon = Icons.Filled.ViewTimeline,
                        accentColor = AnimeGreen,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.navigate(Screen.Timeline) }
                    )
                    StudioToolCard(
                        title = if (isArabic) "نشر يوتيوب والربح" else "Monetization",
                        subtitle = if (isArabic) "قائمة الفحص والكلمات" else "Checklist & SEO",
                        icon = Icons.Filled.MonetizationOn,
                        accentColor = AnimeAmber,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.navigate(Screen.YouTubeCenter) }
                    )
                }
            }
        }

        // Recent Projects Horizontal Scroll / List
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isArabic) "المشاريع الأخيرة" else "Recent Projects",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                TextButton(onClick = { viewModel.navigate(Screen.Projects) }) {
                    Text(
                        text = if (isArabic) "عرض الكل (${projects.size})" else "View All (${projects.size})",
                        color = AnimeCyanLight,
                        fontSize = 12.sp
                    )
                }
            }

            if (projects.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(DarkSurface)
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isArabic) "لا توجد مشاريع حتى الآن. انقر على 'مشروع جديد' للبدء!" else "No projects yet. Click 'New Project' to begin!",
                        color = TextSecondary,
                        fontSize = 13.sp
                    )
                }
            } else {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(vertical = 4.dp)
                ) {
                    items(projects) { project ->
                        ProjectMiniCard(
                            project = project,
                            isSelected = project.id == selectedProject?.id,
                            isArabic = isArabic,
                            onClick = {
                                viewModel.selectProject(project)
                                viewModel.navigate(Screen.Timeline)
                            }
                        )
                    }
                }
            }
        }

        // Free-First & AI Quota Policy Card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(DarkSurfaceVariant)
                    .border(1.dp, BorderSubtle, RoundedCornerShape(14.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.VerifiedUser, contentDescription = null, tint = AnimeCyan, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isArabic) "سياسة الاستخدام المجاني والمحلي" else "Free-First & Local Architecture",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = TextPrimary
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (isArabic)
                            "• تحرير المشاهد والمخطط الزمني وتوليد القوالب مجاني ويعمل بدون إنترنت.\n" +
                            "• ريندر الفيديو MP4 يتم محلياً بواسطة معالج الهاتف (MediaCodec).\n" +
                            "• تحويل النص لصوت يعمل عبر محرك Android TTS دون تكلفة سحابية.\n" +
                            "• مزود الذكاء الاصطناعي (Gemini) اختياري تماماً ويخضع لحصص المفتاح المخصص."
                        else
                            "• Editing, timeline, and template scripts work offline with no cost.\n" +
                            "• MP4 video rendering executes locally via Android MediaCodec.\n" +
                            "• Voice-over runs via local Android TextToSpeech engine.\n" +
                            "• Gemini AI story generation is fully optional using your configured key.",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}

@Composable
fun QuickStartCard(
    title: String,
    subtitle: String,
    badge: String,
    badgeColor: Color,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(DarkSurface)
            .border(1.dp, BorderSubtle, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(14.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(icon, contentDescription = null, tint = badgeColor, modifier = Modifier.size(24.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(badgeColor.copy(alpha = 0.2f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(text = badge, color = badgeColor, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimary)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = subtitle, fontSize = 11.sp, color = TextSecondary)
        }
    }
}

@Composable
fun StudioToolCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(DarkSurface)
            .border(1.dp, BorderSubtle, RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(accentColor.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(text = title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimary)
            Text(text = subtitle, fontSize = 11.sp, color = TextSecondary)
        }
    }
}

@Composable
fun ProjectMiniCard(
    project: Project,
    isSelected: Boolean,
    isArabic: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .width(180.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(if (isSelected) DarkSurfaceElevated else DarkSurface)
            .border(
                1.5.dp,
                if (isSelected) AnimePurple else BorderSubtle,
                RoundedCornerShape(14.dp)
            )
            .clickable { onClick() }
            .padding(12.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(AnimeCyan.copy(alpha = 0.15f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "${project.targetDurationSeconds}s",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = AnimeCyanLight
                    )
                }
                if (project.isSample) {
                    Text(
                        text = if (isArabic) "نموذج" else "Sample",
                        fontSize = 10.sp,
                        color = AnimeAmber
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = project.title,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = TextPrimary,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${project.aspectRatio.labelAr} • ${project.style.labelAr}",
                fontSize = 11.sp,
                color = TextSecondary,
                maxLines = 1
            )
        }
    }
}
