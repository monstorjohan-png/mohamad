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
import com.example.data.model.AspectRatio
import com.example.data.model.CameraMotionType
import com.example.data.model.SceneItem
import com.example.data.model.TransitionType
import com.example.ui.components.SceneCanvasPreview
import com.example.ui.theme.*
import com.example.ui.viewmodel.StudioViewModel

@Composable
fun SceneStudioScreen(
    viewModel: StudioViewModel,
    modifier: Modifier = Modifier
) {
    val isArabic by viewModel.isArabic.collectAsStateWithLifecycle()
    val selectedProject by viewModel.selectedProject.collectAsStateWithLifecycle()
    val scenes by viewModel.currentScenes.collectAsStateWithLifecycle()
    val selectedScene by viewModel.selectedScene.collectAsStateWithLifecycle()
    val isSpeaking by viewModel.isTtsSpeaking.collectAsStateWithLifecycle()

    var isPreviewPlaying by remember { mutableStateOf(true) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 10.dp, bottom = 32.dp)
    ) {
        // Top Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (isArabic) "استوديو تركيب المشاهد" else "Scene Studio",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = if (isArabic) "معاينة حية ومؤثرات كين بيرنز وطبقات الشخصيات" else "Live Ken Burns motion & character layer compositor",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }
                IconButton(onClick = { isPreviewPlaying = !isPreviewPlaying }) {
                    Icon(
                        imageVector = if (isPreviewPlaying) Icons.Filled.PauseCircle else Icons.Filled.PlayCircle,
                        contentDescription = "Toggle Preview Play",
                        tint = AnimeCyan,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }

        // Live Preview Canvas
        item {
            val aspect = selectedProject?.aspectRatio ?: AspectRatio.SHORTS_9_16
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(if (aspect == AspectRatio.SHORTS_9_16) 360.dp else 220.dp),
                contentAlignment = Alignment.Center
            ) {
                SceneCanvasPreview(
                    scene = selectedScene,
                    aspectRatio = aspect,
                    isPlaying = isPreviewPlaying,
                    showSubtitles = true,
                    modifier = Modifier.fillMaxHeight()
                )
            }
        }

        // Scene Picker Chips Row
        item {
            Text(
                text = if (isArabic) "اختر المشهد للتحرير:" else "Select Scene:",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(6.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                items(scenes) { scene ->
                    val isCurrent = scene.id == selectedScene?.id
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isCurrent) DarkSurfaceElevated else DarkSurface)
                            .border(1.5.dp, if (isCurrent) AnimePurple else BorderSubtle, RoundedCornerShape(10.dp))
                            .clickable { viewModel.selectScene(scene) }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "${scene.orderIndex + 1}. ${scene.title}",
                            fontSize = 12.sp,
                            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                            color = if (isCurrent) AnimePurpleLight else TextSecondary
                        )
                    }
                }
            }
        }

        if (selectedScene != null) {
            val scene = selectedScene!!

            // Title & Duration Row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = scene.title,
                        onValueChange = { viewModel.updateScene(scene.copy(title = it)) },
                        label = { Text(if (isArabic) "اسم المشهد" else "Scene Title") },
                        singleLine = true,
                        modifier = Modifier.weight(2f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AnimePurple,
                            unfocusedBorderColor = BorderSubtle,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )

                    OutlinedTextField(
                        value = scene.durationSeconds.toString(),
                        onValueChange = {
                            val dur = it.toFloatOrNull() ?: scene.durationSeconds
                            viewModel.updateScene(scene.copy(durationSeconds = dur.coerceIn(1f, 60f)))
                        },
                        label = { Text(if (isArabic) "المدة (ث)" else "Duration (s)") },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AnimeCyan,
                            unfocusedBorderColor = BorderSubtle,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            }

            // Camera Motion Preset Selector
            item {
                Text(
                    text = if (isArabic) "حركة الكاميرا (كين بيرنز والاهتزاز)" else "Camera Motion Preset",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    CameraMotionType.entries.forEach { motion ->
                        val isSelected = scene.cameraMotion == motion
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) DarkSurfaceElevated else DarkSurface)
                                .border(1.dp, if (isSelected) AnimeCyan else BorderSubtle, RoundedCornerShape(10.dp))
                                .clickable { viewModel.updateScene(scene.copy(cameraMotion = motion)) }
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = { viewModel.updateScene(scene.copy(cameraMotion = motion)) },
                                colors = RadioButtonDefaults.colors(selectedColor = AnimeCyan)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isArabic) motion.labelAr else motion.labelEn,
                                fontSize = 12.sp,
                                color = TextPrimary
                            )
                        }
                    }
                }
            }

            // Character Layer Toggle
            item {
                Text(
                    text = if (isArabic) "طبقة الشخصية (Foreground Layer)" else "Character Layer",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    val hasChar = scene.characterLayerType != "NONE"
                    Button(
                        onClick = {
                            viewModel.updateScene(
                                scene.copy(
                                    characterLayerType = "DRAWABLE_RES",
                                    characterLayerValue = "sample_character_ren"
                                )
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = if (hasChar) AnimePurple else DarkSurface),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(if (isArabic) "تفعيل شخصية رين" else "Enable Ren")
                    }

                    Button(
                        onClick = {
                            viewModel.updateScene(
                                scene.copy(
                                    characterLayerType = "NONE",
                                    characterLayerValue = null
                                )
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = if (!hasChar) AnimePurple else DarkSurface),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(if (isArabic) "بدون شخصية" else "No Character")
                    }
                }
            }

            // Narration & Subtitle Inputs
            item {
                Text(
                    text = if (isArabic) "التعليق الصوتي والنص" else "Narration & Dialogue",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = scene.narrationText,
                    onValueChange = { viewModel.updateScene(scene.copy(narrationText = it, subtitleText = it)) },
                    label = { Text(if (isArabic) "نص الراوي (Narration)" else "Narration Text") },
                    minLines = 2,
                    maxLines = 4,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AnimePurple,
                        unfocusedBorderColor = BorderSubtle,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = {
                            if (isSpeaking) {
                                viewModel.stopSpeaking()
                            } else {
                                viewModel.speakNarration(scene.narrationText, selectedProject?.language ?: "ar")
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AnimeCyan),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(if (isSpeaking) Icons.Filled.Stop else Icons.Filled.VolumeUp, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isSpeaking) (if (isArabic) "إيقاف الصوت" else "Stop") else (if (isArabic) "استماع للصوت (TTS)" else "Listen (TTS)"),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Transition selector
            item {
                Text(
                    text = if (isArabic) "الانتقال الداخلي والخارجي" else "Scene Transitions",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TransitionType.entries.forEach { trans ->
                        val isSelected = scene.transitionIn == trans
                        DurationChip(
                            label = trans.labelAr,
                            isSelected = isSelected,
                            onClick = { viewModel.updateScene(scene.copy(transitionIn = trans, transitionOut = trans)) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Duplicate or Delete Scene Actions
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = { viewModel.duplicateScene(scene) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Filled.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (isArabic) "تكرار المشهد" else "Duplicate")
                    }

                    OutlinedButton(
                        onClick = { viewModel.deleteScene(scene) },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFEF4444)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF4444)),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Filled.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (isArabic) "حذف المشهد" else "Delete")
                    }
                }
            }
        }
    }
}
