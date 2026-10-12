package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
fun SettingsScreen(
    viewModel: StudioViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isArabic by viewModel.isArabic.collectAsStateWithLifecycle()
    val hasKey = viewModel.geminiService.hasApiKey()
    val projects by viewModel.allProjects.collectAsStateWithLifecycle()

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
                text = if (isArabic) "الإعدادات ومزودو الذكاء الاصطناعي" else "Settings & AI Providers",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (isArabic) "إدارة مزودي الخدمة، حالة الاتصال، وحصص الاستخدام" else "Manage AI providers, connection status, and quotas",
                fontSize = 12.sp,
                color = TextSecondary
            )
        }

        // Gemini AI Provider Info Card
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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.AutoAwesome, contentDescription = null, tint = AnimePurpleLight)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Google Gemini API",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = TextPrimary
                            )
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (hasKey) Color(0x3310B981) else Color(0x33F59E0B))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = if (hasKey) (if (isArabic) "متصل" else "Connected") else (if (isArabic) "غير مهيأ" else "Not Configured"),
                                color = if (hasKey) AnimeGreen else AnimeAmber,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Text(
                        text = "${if (isArabic) "النموذج:" else "Model:"} models/gemini-3.8-flash (Preview)",
                        fontSize = 12.sp,
                        color = AnimeCyanLight
                    )

                    Text(
                        text = if (isArabic)
                            "• لتفعيل الذكاء الاصطناعي الخارجي: أضف GEMINI_API_KEY في لوحة الأسرار (Secrets Panel).\n" +
                            "• في حال عدم توفر المفتاح: التطبيق يعمل بكامل ميزاته مع قوالب القصص المجانية محلياً."
                        else
                            "• To enable AI: set GEMINI_API_KEY in the Secrets Panel.\n" +
                            "• If missing: The app continues functioning locally with rich offline story templates.",
                        fontSize = 11.sp,
                        color = TextSecondary,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        // Interface Language Card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(DarkSurface)
                    .border(1.dp, BorderSubtle, RoundedCornerShape(14.dp))
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (isArabic) "لغة الواجهة (Interface Language)" else "Interface Language",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = TextPrimary
                        )
                        Text(
                            text = if (isArabic) "العربية (RTL) / English (LTR)" else "Arabic (RTL) / English (LTR)",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }

                    Button(
                        onClick = { viewModel.toggleLanguage() },
                        colors = ButtonDefaults.buttonColors(containerColor = AnimePurple),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(if (isArabic) "English" else "عربي")
                    }
                }
            }
        }

        // Local Storage & Stats Card
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
                        text = if (isArabic) "إحصائيات التخزين المحلي" else "Local Storage & Stats",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = TextPrimary
                    )

                    Text(
                        text = "${if (isArabic) "عدد المشاريع النشطة:" else "Active Projects:"} ${projects.size}",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    Text(
                        text = if (isArabic) "قاعدة البيانات: Room Database (SQLite محلي)" else "Database: Room Database (Local SQLite)",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    Text(
                        text = if (isArabic) "ترميز الفيديو: Hardware H.264 (MediaCodec)" else "Video Encoding: Hardware H.264 (MediaCodec)",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            }
        }
    }
}
