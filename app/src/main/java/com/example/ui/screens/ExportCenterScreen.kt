package com.example.ui.screens

import android.content.Intent
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.AspectRatio
import com.example.ui.theme.*
import com.example.ui.viewmodel.StudioViewModel
import java.io.File

@Composable
fun ExportCenterScreen(
    viewModel: StudioViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isArabic by viewModel.isArabic.collectAsStateWithLifecycle()
    val selectedProject by viewModel.selectedProject.collectAsStateWithLifecycle()
    val scenes by viewModel.currentScenes.collectAsStateWithLifecycle()
    val exportState by viewModel.exportState.collectAsStateWithLifecycle()

    var resolution by remember { mutableStateOf("720p") }
    var fps by remember { mutableIntStateOf(30) }
    var burnSubtitles by remember { mutableStateOf(true) }

    val totalDuration = remember(scenes) {
        scenes.sumOf { it.durationSeconds.toDouble() }.toFloat()
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
                text = if (isArabic) "مركز التصدير وريندر الفيديو MP4" else "Video Export Center",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (isArabic) "تصدير حقيقي لملف MP4 بمعيار H.264 عالي التوافق بدون اشتراكات أو علامات مائية" else "Real H.264 MP4 hardware rendering with no subscriptions or watermarks",
                fontSize = 12.sp,
                color = TextSecondary
            )
        }

        // Project Summary Card
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
                            text = selectedProject?.title ?: (if (isArabic) "المشروع النشط" else "Active Project"),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        BadgeChip(
                            text = selectedProject?.aspectRatio?.labelAr ?: "9:16",
                            color = AnimeCyan
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "${if (isArabic) "عدد المشاهد:" else "Scenes:"} ${scenes.size} • ${if (isArabic) "المدة الفعلية:" else "Duration:"} ${String.format("%.1f", totalDuration)}s",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            }
        }

        // Export Configuration Card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(DarkSurface)
                    .border(1.dp, BorderSubtle, RoundedCornerShape(14.dp))
                    .padding(14.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = if (isArabic) "إعدادات الريندر والدقة" else "Render Resolution & Quality",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    // Resolution selector
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        listOf("720p (سريع ومثالي)" to "720p", "1080p (Full HD)" to "1080p").forEach { (label, resKey) ->
                            val isSel = resolution == resKey
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSel) DarkSurfaceElevated else DarkSurfaceVariant)
                                    .border(1.dp, if (isSel) AnimePurple else BorderSubtle, RoundedCornerShape(10.dp))
                                    .clickable { resolution = resKey }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSel) AnimePurpleLight else TextSecondary
                                )
                            }
                        }
                    }

                    // FPS selector
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        listOf(24 to "24 FPS (سينمائي)", 30 to "30 FPS (سلس)").forEach { (fpsVal, label) ->
                            val isSel = fps == fpsVal
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSel) DarkSurfaceElevated else DarkSurfaceVariant)
                                    .border(1.dp, if (isSel) AnimeCyan else BorderSubtle, RoundedCornerShape(10.dp))
                                    .clickable { fps = fpsVal }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSel) AnimeCyanLight else TextSecondary
                                )
                            }
                        }
                    }

                    // Subtitles Burn-in toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (isArabic) "حرق الترجمات في الفيديو (Burn Subtitles)" else "Burn In Subtitles",
                                fontSize = 12.sp,
                                color = TextPrimary
                            )
                            Text(
                                text = if (isArabic) "إدراج النصوص والشارات داخل إطارات الفيديو" else "Embed captions onto video frames",
                                fontSize = 10.sp,
                                color = TextSecondary
                            )
                        }
                        Switch(
                            checked = burnSubtitles,
                            onCheckedChange = { burnSubtitles = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = AnimePurple)
                        )
                    }
                }
            }
        }

        // Live Export Progress Box
        if (exportState.isExporting) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(DarkSurfaceElevated)
                        .border(1.5.dp, AnimePurple, RoundedCornerShape(14.dp))
                        .padding(16.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isArabic) "جاري ترميز وريندر الفيديو..." else "Encoding video frames...",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = TextPrimary
                            )
                            Text(
                                text = "${(exportState.progress * 100).toInt()}%",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 16.sp,
                                color = AnimeCyanLight
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        LinearProgressIndicator(
                            progress = { exportState.progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = AnimeCyan,
                            trackColor = DarkSurfaceVariant,
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = exportState.statusMessage,
                            fontSize = 12.sp,
                            color = TextSecondary
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedButton(
                            onClick = { viewModel.cancelExport() },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFEF4444)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(if (isArabic) "إلغاء التصدير" else "Cancel Export")
                        }
                    }
                }
            }
        }

        // Export Success Result Box
        if (exportState.outputFileUri != null) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFF064E3B), DarkSurface)
                            )
                        )
                        .border(1.5.dp, AnimeGreen, RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = AnimeGreen, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isArabic) "تم إنتاج الفيديو بنجاح!" else "Video Rendered Successfully!",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = TextPrimary
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (isArabic)
                                "الملف جاهز الآن بصيغة MP4 القياسية ويمكنك مشاركته مباشرة أو رفعه على يوتيوب شورتس."
                            else
                                "MP4 file is ready. You can share it or publish it directly.",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = {
                                val file = File(exportState.outputFileUri!!)
                                val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
                                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "video/mp4"
                                    putExtra(Intent.EXTRA_STREAM, uri)
                                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                }
                                context.startActivity(Intent.createChooser(shareIntent, "Share Anime Video"))
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AnimeGreen),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("share_video_btn"),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Filled.Share, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isArabic) "مشاركة أو فتح الفيديو (MP4)" else "Share / Open MP4 Video",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Export Error Box
        if (exportState.errorMessage != null && !exportState.isExporting) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0x33EF4444))
                        .border(1.dp, Color(0xFFEF4444), RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Text(text = exportState.errorMessage!!, color = Color(0xFFFCA5A5), fontSize = 12.sp)
                }
            }
        }

        // Start Export Button
        if (!exportState.isExporting) {
            item {
                Button(
                    onClick = {
                        val aspect = selectedProject?.aspectRatio ?: AspectRatio.SHORTS_9_16
                        val (w, h) = if (resolution == "1080p") {
                            when (aspect) {
                                AspectRatio.SHORTS_9_16 -> 1080 to 1920
                                AspectRatio.YOUTUBE_16_9 -> 1920 to 1080
                                AspectRatio.SQUARE_1_1 -> 1080 to 1080
                            }
                        } else {
                            when (aspect) {
                                AspectRatio.SHORTS_9_16 -> 720 to 1280
                                AspectRatio.YOUTUBE_16_9 -> 1280 to 720
                                AspectRatio.SQUARE_1_1 -> 720 to 720
                            }
                        }

                        viewModel.startVideoExport(
                            resolutionWidth = w,
                            resolutionHeight = h,
                            fps = fps,
                            burnSubtitles = burnSubtitles
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("start_export_btn"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AnimePurple)
                ) {
                    Icon(Icons.Filled.VideoCameraBack, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isArabic) "بدء تصدير فيديو MP4 الحقيقي" else "Start Real MP4 Export",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
