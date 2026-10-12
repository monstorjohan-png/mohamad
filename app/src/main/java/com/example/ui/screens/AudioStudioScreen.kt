package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.ui.theme.*
import com.example.ui.viewmodel.StudioViewModel

@Composable
fun AudioStudioScreen(
    viewModel: StudioViewModel,
    modifier: Modifier = Modifier
) {
    val isArabic by viewModel.isArabic.collectAsStateWithLifecycle()
    val isSpeaking by viewModel.isTtsSpeaking.collectAsStateWithLifecycle()
    val isArabicTtsSupported by viewModel.isArabicTtsSupported.collectAsStateWithLifecycle()
    val isEnglishTtsSupported by viewModel.isEnglishTtsSupported.collectAsStateWithLifecycle()
    val selectedProject by viewModel.selectedProject.collectAsStateWithLifecycle()
    val scenes by viewModel.currentScenes.collectAsStateWithLifecycle()

    var testScript by remember {
        mutableStateOf(
            if (isArabic)
                "مرحباً بكم في ستوديو أنمي AI! هذه معاينة حية لمحرك التعليق الصوتي المحلي."
            else
                "Welcome to Anime Studio AI! This is a live preview of the local voice-over engine."
        )
    }

    var pitch by remember { mutableFloatStateOf(1.0f) }
    var speed by remember { mutableFloatStateOf(1.0f) }

    var bgmEnabled by remember { mutableStateOf(true) }
    var bgmVolume by remember { mutableFloatStateOf(0.25f) }
    var duckingEnabled by remember { mutableStateOf(true) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 10.dp, bottom = 32.dp)
    ) {
        item {
            Text(
                text = if (isArabic) "استوديو الصوت والتعليق" else "Audio & Voice-Over Studio",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (isArabic) "محرك تحويل النص إلى صوت (Android TTS) ومؤثرات الموسيقى وخفض الصوت التلقائي" else "Android TextToSpeech engine, voice pitch/speed, and background music ducking",
                fontSize = 12.sp,
                color = TextSecondary
            )
        }

        // Engine Status Card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(DarkSurface)
                    .border(1.dp, BorderSubtle, RoundedCornerShape(14.dp))
                    .padding(14.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isArabic) "حالة محرك الصوت المحلي (Android TTS)" else "Local TTS Engine Status",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = TextPrimary
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0x3310B981))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(text = "مجاني 100%", color = AnimeGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        StatusIndicator(
                            label = if (isArabic) "اللغة العربية" else "Arabic Voice",
                            isSupported = isArabicTtsSupported
                        )
                        StatusIndicator(
                            label = if (isArabic) "اللغة الإنجليزية" else "English Voice",
                            isSupported = isEnglishTtsSupported
                        )
                    }
                }
            }
        }

        // Voice Controls (Pitch & Speed)
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(DarkSurface)
                    .border(1.dp, BorderSubtle, RoundedCornerShape(14.dp))
                    .padding(14.dp)
            ) {
                Column {
                    Text(
                        text = if (isArabic) "نبرة الصوت والسرعة" else "Voice Pitch & Speed",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "${if (isArabic) "نبرة الصوت (Pitch):" else "Pitch:"} ${String.format("%.2f", pitch)}x",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    Slider(
                        value = pitch,
                        onValueChange = {
                            pitch = it
                            viewModel.ttsManager.currentPitch = it
                        },
                        valueRange = 0.5f..2.0f,
                        colors = SliderDefaults.colors(thumbColor = AnimePurple, activeTrackColor = AnimePurpleLight)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "${if (isArabic) "سرعة الإلقاء (Speed):" else "Speech Rate:"} ${String.format("%.2f", speed)}x",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    Slider(
                        value = speed,
                        onValueChange = {
                            speed = it
                            viewModel.ttsManager.currentSpeed = it
                        },
                        valueRange = 0.5f..2.0f,
                        colors = SliderDefaults.colors(thumbColor = AnimeCyan, activeTrackColor = AnimeCyanLight)
                    )
                }
            }
        }

        // Script Test & Live Preview
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(DarkSurface)
                    .border(1.dp, BorderSubtle, RoundedCornerShape(14.dp))
                    .padding(14.dp)
            ) {
                Column {
                    Text(
                        text = if (isArabic) "تجربة ونطق النص" else "Test Narration Script",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = testScript,
                        onValueChange = { testScript = it },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AnimePurple,
                            unfocusedBorderColor = BorderSubtle,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                if (isSpeaking) {
                                    viewModel.stopSpeaking()
                                } else {
                                    viewModel.speakNarration(testScript, selectedProject?.language ?: "ar")
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = if (isSpeaking) Color(0xFFEF4444) else AnimePurple),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("tts_preview_btn")
                        ) {
                            Icon(if (isSpeaking) Icons.Filled.Stop else Icons.Filled.VolumeUp, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isSpeaking) (if (isArabic) "إيقاف الصوت" else "Stop") else (if (isArabic) "تشغيل الصوت" else "Speak"),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Background Music & Ducking settings
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(DarkSurface)
                    .border(1.dp, BorderSubtle, RoundedCornerShape(14.dp))
                    .padding(14.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isArabic) "الموسيقى التصويرية والخفوت التلقائي" else "BGM & Auto Ducking",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = TextPrimary
                        )
                        Switch(
                            checked = bgmEnabled,
                            onCheckedChange = { bgmEnabled = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = AnimeCyan)
                        )
                    }

                    if (bgmEnabled) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "${if (isArabic) "مستوى صوت الموسيقى:" else "BGM Volume:"} ${(bgmVolume * 100).toInt()}%",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                        Slider(
                            value = bgmVolume,
                            onValueChange = { bgmVolume = it },
                            valueRange = 0f..1f,
                            colors = SliderDefaults.colors(thumbColor = AnimeCyan, activeTrackColor = AnimeCyanLight)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = if (isArabic) "خفض الموسيقى أثناء حديث الراوي (Ducking)" else "Audio Ducking during narration",
                                    fontSize = 12.sp,
                                    color = TextPrimary
                                )
                                Text(
                                    text = if (isArabic) "تخفيض تلقائي لصوت الخلفية لمنع التشويش" else "Lowers BGM volume while speech is active",
                                    fontSize = 10.sp,
                                    color = TextSecondary
                                )
                            }
                            Checkbox(
                                checked = duckingEnabled,
                                onCheckedChange = { duckingEnabled = it },
                                colors = CheckboxDefaults.colors(checkedColor = AnimePurple)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatusIndicator(label: String, isSupported: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(RoundedCornerShape(5.dp))
                .background(if (isSupported) AnimeGreen else AnimeAmber)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = "$label: ${if (isSupported) "مدعوم" else "غير مثبت"}",
            fontSize = 11.sp,
            color = if (isSupported) TextPrimary else TextSecondary
        )
    }
}
