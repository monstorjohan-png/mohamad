package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.SceneItem
import com.example.ui.navigation.Screen
import com.example.ui.theme.*
import com.example.ui.viewmodel.StudioViewModel

@Composable
fun TimelineScreen(
    viewModel: StudioViewModel,
    modifier: Modifier = Modifier
) {
    val isArabic by viewModel.isArabic.collectAsStateWithLifecycle()
    val selectedProject by viewModel.selectedProject.collectAsStateWithLifecycle()
    val scenes by viewModel.currentScenes.collectAsStateWithLifecycle()
    val selectedScene by viewModel.selectedScene.collectAsStateWithLifecycle()

    val totalDuration = remember(scenes) {
        scenes.sumOf { it.durationSeconds.toDouble() }.toFloat()
    }
    val targetDuration = selectedProject?.targetDurationSeconds ?: 60

    var isPlaying by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 10.dp, bottom = 32.dp)
    ) {
        // Header & Play controls
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (isArabic) "المخطط الزمني متعدد المسارات" else "Multitrack Timeline Editor",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = if (isArabic)
                            "المدة الإجمالية: ${String.format("%.1f", totalDuration)} ثانية من أصل $targetDuration ثانية"
                        else
                            "Total Duration: ${String.format("%.1f", totalDuration)}s of ${targetDuration}s",
                        fontSize = 12.sp,
                        color = if (totalDuration <= targetDuration) AnimeCyanLight else AnimeAmber
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { isPlaying = !isPlaying },
                        modifier = Modifier.testTag("timeline_play_toggle")
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Filled.PauseCircle else Icons.Filled.PlayCircle,
                            contentDescription = "Play",
                            tint = AnimePurpleLight,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                    Button(
                        onClick = { viewModel.addSceneToCurrentProject() },
                        colors = ButtonDefaults.buttonColors(containerColor = AnimePurple),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("add_scene_timeline_btn")
                    ) {
                        Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (isArabic) "إضافة مشهد" else "Add Scene", fontSize = 12.sp)
                    }
                }
            }
        }

        // Timeline Progress Bar Indicator
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(DarkSurfaceVariant)
            ) {
                val ratio = (totalDuration / targetDuration.toFloat()).coerceIn(0f, 1f)
                Box(
                    modifier = Modifier
                        .fillMaxWidth(ratio)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(4.dp))
                        .background(AnimeCyan)
                )
            }
        }

        // Horizontal Timeline Tracks Visualizer
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(DarkSurface)
                    .border(1.dp, BorderSubtle, RoundedCornerShape(16.dp))
                    .padding(14.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = if (isArabic) "مسار الفيديو والصور (Video Track)" else "Video & Background Track",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary
                    )

                    // Video clips row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        scenes.forEach { scene ->
                            val isSelected = scene.id == selectedScene?.id
                            val clipWidth = (scene.durationSeconds * 24).dp.coerceAtLeast(90.dp)

                            Box(
                                modifier = Modifier
                                    .width(clipWidth)
                                    .height(56.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) DarkSurfaceElevated else Color(0xFF1E2438))
                                    .border(1.5.dp, if (isSelected) AnimeCyan else AnimePurple.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                                    .clickable {
                                        viewModel.selectScene(scene)
                                    }
                                    .padding(6.dp)
                            ) {
                                Column(verticalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxSize()) {
                                    Text(
                                        text = "${scene.orderIndex + 1}. ${scene.title}",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary,
                                        maxLines = 1
                                    )
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(text = "${scene.durationSeconds}s", fontSize = 9.sp, color = AnimeCyanLight)
                                        Text(text = scene.cameraMotion.labelAr, fontSize = 9.sp, color = TextTertiary, maxLines = 1)
                                    }
                                }
                            }
                        }
                    }

                    // Narration / Dialogue Track
                    Text(
                        text = if (isArabic) "مسار الصوت والتعليق (Audio Track)" else "Audio & Dialogue Track",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        scenes.forEach { scene ->
                            val clipWidth = (scene.durationSeconds * 24).dp.coerceAtLeast(90.dp)
                            val hasNarration = scene.narrationText.isNotBlank()

                            Box(
                                modifier = Modifier
                                    .width(clipWidth)
                                    .height(38.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (hasNarration) Color(0x338B5CF6) else DarkSurfaceVariant)
                                    .border(1.dp, if (hasNarration) AnimePurpleLight else BorderSubtle, RoundedCornerShape(8.dp))
                                    .padding(horizontal = 6.dp, vertical = 4.dp),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Filled.Mic,
                                        contentDescription = null,
                                        tint = if (hasNarration) AnimePurpleLight else TextTertiary,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (hasNarration) scene.narrationText else (if (isArabic) "صامت" else "Muted"),
                                        fontSize = 9.sp,
                                        color = if (hasNarration) TextPrimary else TextTertiary,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }

                    // Subtitle Track
                    Text(
                        text = if (isArabic) "مسار الترجمات (Subtitles Track)" else "Subtitles Track",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        scenes.forEach { scene ->
                            val clipWidth = (scene.durationSeconds * 24).dp.coerceAtLeast(90.dp)
                            val hasSubtitle = scene.subtitleText.isNotBlank()

                            Box(
                                modifier = Modifier
                                    .width(clipWidth)
                                    .height(30.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (hasSubtitle) Color(0x3306B6D4) else DarkSurfaceVariant)
                                    .border(0.5.dp, if (hasSubtitle) AnimeCyan else BorderSubtle, RoundedCornerShape(6.dp))
                                    .padding(horizontal = 6.dp),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                Text(
                                    text = if (hasSubtitle) scene.subtitleText else "---",
                                    fontSize = 9.sp,
                                    color = if (hasSubtitle) AnimeCyanLight else TextTertiary,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }

        // Scene List for editing & trimming
        item {
            Text(
                text = if (isArabic) "قائمة مشاهد المشروع" else "Project Scenes List",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }

        items(scenes.size) { index ->
            val scene = scenes[index]
            val isSelected = scene.id == selectedScene?.id
            TimelineSceneRow(
                scene = scene,
                isSelected = isSelected,
                isArabic = isArabic,
                onSelect = { viewModel.selectScene(scene) },
                onEditInStudio = {
                    viewModel.selectScene(scene)
                    viewModel.navigate(Screen.SceneStudio)
                },
                onTrimDuration = { newDur ->
                    viewModel.updateScene(scene.copy(durationSeconds = newDur.coerceIn(1f, 60f)))
                },
                onDelete = { viewModel.deleteScene(scene) }
            )
        }
    }
}

@Composable
fun TimelineSceneRow(
    scene: SceneItem,
    isSelected: Boolean,
    isArabic: Boolean,
    onSelect: () -> Unit,
    onEditInStudio: () -> Unit,
    onTrimDuration: (Float) -> Unit,
    onDelete: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) DarkSurfaceElevated else DarkSurface)
            .border(1.dp, if (isSelected) AnimePurple else BorderSubtle, RoundedCornerShape(12.dp))
            .clickable { onSelect() }
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(AnimePurple.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${scene.orderIndex + 1}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = AnimePurpleLight
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = scene.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = TextPrimary
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${scene.cameraMotion.labelAr} • ${scene.durationSeconds}s",
                    fontSize = 11.sp,
                    color = TextSecondary
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                // Quick Trim +/-
                IconButton(onClick = { onTrimDuration(scene.durationSeconds - 1f) }, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Filled.RemoveCircleOutline, contentDescription = "Trim Less", tint = TextSecondary, modifier = Modifier.size(18.dp))
                }
                Text(text = "${scene.durationSeconds.toInt()}s", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AnimeCyanLight)
                IconButton(onClick = { onTrimDuration(scene.durationSeconds + 1f) }, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Filled.AddCircleOutline, contentDescription = "Trim More", tint = TextSecondary, modifier = Modifier.size(18.dp))
                }

                Spacer(modifier = Modifier.width(4.dp))

                IconButton(onClick = onEditInStudio, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Filled.Tune, contentDescription = "Edit Studio", tint = AnimePurpleLight, modifier = Modifier.size(18.dp))
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Filled.Close, contentDescription = "Delete", tint = Color(0xFFEF4444), modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}
