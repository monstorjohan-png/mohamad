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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.*
import com.example.ui.viewmodel.StudioViewModel

@Composable
fun HelpScreen(
    viewModel: StudioViewModel,
    modifier: Modifier = Modifier
) {
    val isArabic by viewModel.isArabic.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 10.dp, bottom = 32.dp)
    ) {
        item {
            Text(
                text = if (isArabic) "دليل الاستخدام واستكشاف الأخطاء" else "Help & Troubleshooting Guide",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (isArabic) "إرشادات إنتاج القصص وفيديوهات 5 دقائق وحل المشاكل التقنية" else "Production guidelines for 5-minute stories and technical FAQs",
                fontSize = 12.sp,
                color = TextSecondary
            )
        }

        // 5-Minute Story Workflow Guide
        item {
            HelpExpandableCard(
                title = if (isArabic) "كيفية إنتاج فيديو أنمي بطول 5 دقائق (300 ثانية)؟" else "How to produce a 5-minute anime video (300s)?",
                content = if (isArabic)
                    "1. أنشئ مشروعاً جديداً واختر المدة 300 ثانية وتنسيق 16:9.\n" +
                    "2. استخدم مؤلف القصص لتوليد سيناريو مقسم إلى 10-15 مشهداً بمعدل 15-20 ثانية لكل مشهد.\n" +
                    "3. نسق حركات الكاميرا (كين بيرنز تقريب أو ابتعاد أو تحريك أفقي) لكل مشهد في استوديو المشاهد لإعطاء حيوية للصور الثابتة.\n" +
                    "4. اضبط نصوص التعليق الصوتي واستمع إليها عبر محرك TTS.\n" +
                    "5. توجه لمركز التصدير واختر دقة 720p لريندر سريع ومستقر على معظم الأجهزة."
                else
                    "1. Create a project with 300 seconds target duration and 16:9 ratio.\n" +
                    "2. Use Story Studio to generate 10-15 scenes averaging 15-20s each.\n" +
                    "3. Apply Ken Burns motions (zoom/pan) to bring still images to life.\n" +
                    "4. Add narration and dialogue using local Android TTS.\n" +
                    "5. Export at 720p in Export Center for smooth hardware encoding."
            )
        }

        // Voice & Audio FAQ
        item {
            HelpExpandableCard(
                title = if (isArabic) "ماذا أفعل إذا لم ينطق الصوت العربي؟" else "What if Arabic voice is not speaking?",
                content = if (isArabic)
                    "• يعتمد محرك الصوت على أصوات نظام Android المثبتة في هاتفك (Google Speech Services).\n" +
                    "• يمكنك التأكد من تثبيت حزمة الصوت العربي من خلال إعدادات هاتفك: النظام > اللغات والإدخال > تحويل النص إلى كلام > تنزيل بيانات الصوت العربي."
                else
                    "• The local voice-over uses Android's installed TTS engine.\n" +
                    "• Ensure your phone has the language voice pack downloaded in Android Settings > System > Languages > Text-to-Speech."
            )
        }

        // Export & Hardware FAQ
        item {
            HelpExpandableCard(
                title = if (isArabic) "كيف يعمل تصدير الفيديو داخل التطبيق؟" else "How does the MP4 video exporter work?",
                content = if (isArabic)
                    "• يستخدم التطبيق مكتبات MediaCodec و MediaMuxer الرسمية لنظام Android.\n" +
                    "• يتم ريندر كل إطار بحركات كين بيرنز وطبقات الشخصيات والترجمات وحفظها في ملف MP4 قياسي يمكن تشغيله ومشاركته في أي مكان بدون قيود."
                else
                    "• The export engine uses native Android MediaCodec and MediaMuxer.\n" +
                    "• Each frame is rendered with dynamic camera pan/zoom and burned-in subtitles into a standard H.264 MP4 file."
            )
        }

        // Monetization & Copyright FAQ
        item {
            HelpExpandableCard(
                title = if (isArabic) "حقوق النشر وتحقيق الدخل في يوتيوب" else "Copyright & YouTube Monetization Safety",
                content = if (isArabic)
                    "• احرص دائماً على إضافة لمستك الإبداعية البشرية ومراجعة السيناريو.\n" +
                    "• استخدم أدوات وكلمات مفتاحية أصلية وتجنب القنوات الآلية المكررة.\n" +
                    "• يمكنك الاطلاع على قائمة فحص النشر في صفحة 'نشر يوتيوب والربح'."
                else
                    "• Always review scripts with human creative direction.\n" +
                    "• Avoid mass repetitive automated content to adhere to YouTube Partner policies.\n" +
                    "• Refer to the pre-publication checklist in the Monetization Center."
            )
        }
    }
}

@Composable
fun HelpExpandableCard(title: String, content: String) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(DarkSurface)
            .border(1.dp, BorderSubtle, RoundedCornerShape(14.dp))
            .clickable { expanded = !expanded }
            .padding(14.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary, modifier = Modifier.weight(1f))
                Icon(
                    imageVector = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = null,
                    tint = AnimeCyan
                )
            }
            if (expanded) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(text = content, fontSize = 12.sp, color = TextSecondary, lineHeight = 18.sp)
            }
        }
    }
}
