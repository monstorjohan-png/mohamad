package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.render.SrtExporter
import com.example.ui.theme.*
import com.example.ui.viewmodel.StudioViewModel
import java.io.File

@Composable
fun SubtitleStudioScreen(
    viewModel: StudioViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isArabic by viewModel.isArabic.collectAsStateWithLifecycle()
    val scenes by viewModel.currentScenes.collectAsStateWithLifecycle()

    var fontSize by remember { mutableFloatStateOf(16f) }
    var burnInVideo by remember { mutableStateOf(true) }
    var previewSampleText by remember {
        mutableStateOf(
            if (isArabic)
                "رين: في أعماق نيو-طوكيو، حيث تلتقي أزهار الكرز بالنيون المتوهج..."
            else
                "Ren: In rainy Neo-Tokyo, where cherry blossoms meet glowing neon..."
        )
    }

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
                text = if (isArabic) "استوديو الترجمات والشارات" else "Subtitle & Captions Studio",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (isArabic) "تنسيق الترجمات، حرق النصوص على الفيديو وتصدير ملفات SRT القياسية" else "Style subtitles, burn into MP4 video, and export SRT subtitle files",
                fontSize = 12.sp,
                color = TextSecondary
            )
        }

        // Live Subtitle Styling Box
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(DarkSurfaceVariant)
                    .border(1.dp, BorderSubtle, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xCC0B0E17))
                        .border(1.dp, Color(0x668B5CF6), RoundedCornerShape(10.dp))
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = previewSampleText,
                        color = Color.White,
                        fontSize = fontSize.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // Font Size Slider
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
                        text = "${if (isArabic) "حجم الخط:" else "Font Size:"} ${fontSize.toInt()}sp",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Slider(
                        value = fontSize,
                        onValueChange = { fontSize = it },
                        valueRange = 12f..28f,
                        colors = SliderDefaults.colors(thumbColor = AnimePurple, activeTrackColor = AnimePurpleLight)
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (isArabic) "حرق الترجمة داخل الفيديو مباشرة (Hardcoded)" else "Burn subtitles directly into MP4",
                                fontSize = 12.sp,
                                color = TextPrimary
                            )
                            Text(
                                text = if (isArabic) "تظهر الترجمة على كل المشغلات وتطبيقات التواصل" else "Displays burned-in captions on all video players",
                                fontSize = 10.sp,
                                color = TextSecondary
                            )
                        }
                        Switch(
                            checked = burnInVideo,
                            onCheckedChange = { burnInVideo = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = AnimeCyan)
                        )
                    }
                }
            }
        }

        // Export SRT Button
        item {
            Button(
                onClick = {
                    val dir = context.getExternalFilesDir(null) ?: context.filesDir
                    val srtFile = File(dir, "subtitles_${System.currentTimeMillis()}.srt")
                    val ok = SrtExporter.exportToFile(scenes, srtFile)
                    if (ok) {
                        Toast.makeText(
                            context,
                            if (isArabic) "تم تصدير ملف SRT بنجاح في: ${srtFile.name}" else "SRT exported successfully!",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("export_srt_btn"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AnimeCyan)
            ) {
                Icon(Icons.Filled.FileDownload, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isArabic) "تصدير ملف الترجمة القياسي (SRT)" else "Export Standard SRT File",
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Scene Subtitle list
        item {
            Text(
                text = if (isArabic) "نصوص الترجمة لكل مشهد" else "Per-Scene Subtitle Texts",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }

        items(scenes) { scene ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(DarkSurface)
                    .border(1.dp, BorderSubtle, RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                Column {
                    Text(
                        text = "${scene.orderIndex + 1}. ${scene.title} (${scene.durationSeconds}s)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = AnimeCyanLight
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = scene.subtitleText,
                        onValueChange = { viewModel.updateScene(scene.copy(subtitleText = it)) },
                        placeholder = { Text(if (isArabic) "نص الشارة أو الترجمة لهذا المشهد..." else "Subtitle text for this scene...") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = false,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AnimePurple,
                            unfocusedBorderColor = BorderSubtle,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            }
        }
    }
}
