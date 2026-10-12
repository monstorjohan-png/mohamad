package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.*
import com.example.ui.viewmodel.StudioViewModel

@Composable
fun YouTubeCenterScreen(
    viewModel: StudioViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isArabic by viewModel.isArabic.collectAsStateWithLifecycle()
    val selectedProject by viewModel.selectedProject.collectAsStateWithLifecycle()
    val scenes by viewModel.currentScenes.collectAsStateWithLifecycle()

    var videoTitle by remember(selectedProject) {
        mutableStateOf(selectedProject?.title ?: "أنمي نيو-طوكيو 2099")
    }

    var videoDescription by remember(selectedProject) {
        mutableStateOf(
            if (isArabic)
                "شاهد قصة أنمي مثيرة تأخذك إلى شوارع نيو-طوكيو المستقبلية! قصة أصلية من إنتاج Anime Studio AI.\n\n#أنمي #anime #shorts #أفلام_أنمي"
            else
                "Watch this thrilling anime story taking you into futuristic Neo-Tokyo! Original story created with Anime Studio AI.\n\n#anime #shorts #animation"
        )
    }

    var hashtags by remember {
        mutableStateOf("#anime #animeart #shorts #animation #انمي #رسوم_متحركة #يوتيوب_شورتس")
    }

    // Monetization Readiness Checklist states
    var checkOriginalScript by remember { mutableStateOf(true) }
    var checkHumanReview by remember { mutableStateOf(true) }
    var checkLicensedAudio by remember { mutableStateOf(true) }
    var checkAccurateTitle by remember { mutableStateOf(true) }
    var checkYouTubePolicy by remember { mutableStateOf(false) }
    var checkAiDisclosure by remember { mutableStateOf(true) }

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
                text = if (isArabic) "مركز نشر يوتيوب والجاهزية للربح" else "YouTube Optimization & Monetization",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (isArabic)
                    "توليد البيانات الوصفية، التحسين لليوتيوب شورتس، وقائمة فحص الامتثال لشروط تحقيق الدخل"
                else
                    "Metadata generator, YouTube Shorts SEO, and monetization compliance checklist",
                fontSize = 12.sp,
                color = TextSecondary
            )
        }

        // YouTube Monetization Transparency Warning Card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0x22F59E0B))
                    .border(1.dp, AnimeAmber, RoundedCornerShape(14.dp))
                    .padding(14.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Info, contentDescription = null, tint = AnimeAmber, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isArabic) "تنبيه هام حول تحقيق الدخل (YouTube Partner Program)" else "YouTube Monetization Policy Notice",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = TextPrimary
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (isArabic)
                            "• إنتاج الفيديو لا يضمن قبول القناة تلقائياً في برنامج شركاء يوتيوب.\n" +
                            "• تشترط يوتيوب محتوى أصلياً ذا قيمة مضافة وإشراف بشري واضح وتجنب المحتوى المكرر آلياً.\n" +
                            "• يجب التصريح عن استخدام الذكاء الاصطناعي وفق سياسات يوتيوب الأخيرة."
                        else
                            "• Producing videos does not guarantee YouTube monetization acceptance.\n" +
                            "• YouTube requires original value, human review, and non-repetitive content.\n" +
                            "• Disclose AI-generated elements in accordance with YouTube guidelines.",
                        fontSize = 11.sp,
                        color = TextSecondary,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        // Pre-publication Checklist
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(DarkSurface)
                    .border(1.dp, BorderSubtle, RoundedCornerShape(14.dp))
                    .padding(14.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = if (isArabic) "قائمة الفحص قبل النشر (Pre-Publication Checklist)" else "Pre-Publication Checklist",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = TextPrimary
                    )

                    ChecklistRow(
                        text = if (isArabic) "سيناريو أصلي وسرد قصصي حقيقي ومميز" else "Original storyline and narrative value",
                        isChecked = checkOriginalScript,
                        onToggle = { checkOriginalScript = !checkOriginalScript }
                    )
                    ChecklistRow(
                        text = if (isArabic) "مراجعة وتعديل بشري كامل للمحتوى المولد" else "Human review and creative refinement",
                        isChecked = checkHumanReview,
                        onToggle = { checkHumanReview = !checkHumanReview }
                    )
                    ChecklistRow(
                        text = if (isArabic) "أصوات وموسيقى مرخصة أو تم إنشاؤها محلياً" else "Properly licensed or local audio assets",
                        isChecked = checkLicensedAudio,
                        onToggle = { checkLicensedAudio = !checkLicensedAudio }
                    )
                    ChecklistRow(
                        text = if (isArabic) "عنوان ووصف دقيق غير مضلل للمشاهد" else "Accurate title and non-clickbait description",
                        isChecked = checkAccurateTitle,
                        onToggle = { checkAccurateTitle = !checkAccurateTitle }
                    )
                    ChecklistRow(
                        text = if (isArabic) "مراجعة سياسات المحتوى المكرر لبرنامج شركاء يوتيوب" else "Review YouTube Repetitive Content policies",
                        isChecked = checkYouTubePolicy,
                        onToggle = { checkYouTubePolicy = !checkYouTubePolicy }
                    )
                    ChecklistRow(
                        text = if (isArabic) "تفعيل خانة 'محتوى تم تعديله أو إنشاؤه رقمياً' في يوتيوب" else "Enable 'Altered / Synthetic Content' in Studio",
                        isChecked = checkAiDisclosure,
                        onToggle = { checkAiDisclosure = !checkAiDisclosure }
                    )
                }
            }
        }

        // Title & Description Generator Card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(DarkSurface)
                    .border(1.dp, BorderSubtle, RoundedCornerShape(14.dp))
                    .padding(14.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = if (isArabic) "بيانات الفيديو المحسنة لمحركات البحث (SEO)" else "Optimized Video Metadata",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = TextPrimary
                    )

                    OutlinedTextField(
                        value = videoTitle,
                        onValueChange = { videoTitle = it },
                        label = { Text(if (isArabic) "عنوان الفيديو لليوتيوب" else "YouTube Video Title") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AnimePurple,
                            unfocusedBorderColor = BorderSubtle,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )

                    OutlinedTextField(
                        value = videoDescription,
                        onValueChange = { videoDescription = it },
                        label = { Text(if (isArabic) "الوصف المقترح" else "Suggested Description") },
                        minLines = 3,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AnimePurple,
                            unfocusedBorderColor = BorderSubtle,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )

                    OutlinedTextField(
                        value = hashtags,
                        onValueChange = { hashtags = it },
                        label = { Text(if (isArabic) "الهاشتاغات والكلمات المفتاحية" else "Hashtags & Tags") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AnimeCyan,
                            unfocusedBorderColor = BorderSubtle,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Button(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("YouTube Metadata", "$videoTitle\n\n$videoDescription\n\n$hashtags")
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(
                                context,
                                if (isArabic) "تم نسخ العنوان والوصف والهاشتاغات للحافظة!" else "Copied to clipboard!",
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AnimeCyan),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Filled.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isArabic) "نسخ بيانات النشر كاملة" else "Copy All Metadata to Clipboard",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChecklistRow(text: String, isChecked: Boolean, onToggle: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable { onToggle() }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = isChecked,
            onCheckedChange = { onToggle() },
            colors = CheckboxDefaults.colors(checkedColor = AnimeGreen)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text, fontSize = 12.sp, color = TextPrimary)
    }
}
