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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.*
import com.example.ui.viewmodel.StudioViewModel

@Composable
fun StoryStudioScreen(
    viewModel: StudioViewModel,
    modifier: Modifier = Modifier
) {
    val isArabic by viewModel.isArabic.collectAsStateWithLifecycle()
    val selectedProject by viewModel.selectedProject.collectAsStateWithLifecycle()
    val isGenerating by viewModel.isGeneratingStory.collectAsStateWithLifecycle()
    val genError by viewModel.storyGenerationError.collectAsStateWithLifecycle()

    var storyIdea by remember { mutableStateOf("") }
    var selectedGenre by remember { mutableStateOf("SCIENCE") }
    var targetDuration by remember { mutableIntStateOf(selectedProject?.targetDurationSeconds ?: 60) }
    var useAi by remember { mutableStateOf(viewModel.geminiService.hasApiKey()) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 32.dp)
    ) {
        item {
            Text(
                text = if (isArabic) "مؤلف القصص ومولد السيناريو" else "Story Studio & Script Writer",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (isArabic) "اكتب فكرتك واختر التصنيف لتوليد حبكة كاملة ومشاهد مقسمة زمنياً" else "Enter story idea and select genre to generate structured scenes and script",
                fontSize = 12.sp,
                color = TextSecondary
            )
        }

        // Active project indicator
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(DarkSurface)
                    .border(1.dp, BorderSubtle, RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.MovieCreation, contentDescription = null, tint = AnimeCyan, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isArabic) "المشروع المستهدف: " else "Target Project: ",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    Text(
                        text = selectedProject?.title ?: (if (isArabic) "لم يتم تحديد مشروع" else "No project selected"),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = AnimeCyanLight
                    )
                }
            }
        }

        // Mode Selector: Gemini AI vs Offline Template
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                val hasKey = viewModel.geminiService.hasApiKey()
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (useAi) DarkSurfaceElevated else DarkSurface)
                        .border(1.5.dp, if (useAi) AnimePurple else BorderSubtle, RoundedCornerShape(12.dp))
                        .clickable { if (hasKey) useAi = true }
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Filled.AutoAwesome, contentDescription = null, tint = if (useAi) AnimePurpleLight else TextSecondary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isArabic) "Gemini AI" else "Gemini AI",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (useAi) TextPrimary else TextSecondary
                        )
                        Text(
                            text = if (hasKey) (if (isArabic) "مفعل ومتاح" else "Connected") else (if (isArabic) "يتطلب مفتاح" else "Key Required"),
                            fontSize = 10.sp,
                            color = if (hasKey) AnimeGreen else TextTertiary
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (!useAi) DarkSurfaceElevated else DarkSurface)
                        .border(1.5.dp, if (!useAi) AnimeCyan else BorderSubtle, RoundedCornerShape(12.dp))
                        .clickable { useAi = false }
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Filled.OfflinePin, contentDescription = null, tint = if (!useAi) AnimeCyanLight else TextSecondary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isArabic) "قوالب مجانية أوفلاين" else "Offline Templates",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (!useAi) TextPrimary else TextSecondary
                        )
                        Text(
                            text = if (isArabic) "سريع وبدون إنترنت" else "Instant & Free",
                            fontSize = 10.sp,
                            color = AnimeGreen
                        )
                    }
                }
            }
        }

        // Genre Picker
        item {
            Text(
                text = if (isArabic) "اختر تصنيف القصة" else "Story Genre",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))

            val genres = listOf(
                "SCIENCE" to (if (isArabic) "علوم واستكشاف فضائي" else "Science & Exploration"),
                "CYBERPUNK" to (if (isArabic) "سايبربانك ونيون" else "Cyberpunk & Neon"),
                "SHONEN" to (if (isArabic) "شونين ومغامرة ملحمية" else "Shonen Adventure"),
                "COMEDY" to (if (isArabic) "كوميديا ويوميات" else "Comedy & Fun"),
                "MYSTERY" to (if (isArabic) "غموض وتحقيق سري" else "Mystery Detective")
            )

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                genres.forEach { (genreKey, label) ->
                    val isSelected = selectedGenre == genreKey
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) DarkSurfaceElevated else DarkSurface)
                            .border(1.dp, if (isSelected) AnimePurpleLight else BorderSubtle, RoundedCornerShape(10.dp))
                            .clickable { selectedGenre = genreKey }
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = isSelected,
                            onClick = { selectedGenre = genreKey },
                            colors = RadioButtonDefaults.colors(selectedColor = AnimePurpleLight)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = label, fontSize = 13.sp, color = TextPrimary, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                    }
                }
            }
        }

        // Story Prompt / Idea input
        item {
            Text(
                text = if (isArabic) "فكرة أو ملخص القصة" else "Story Idea & Prompt",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = storyIdea,
                onValueChange = { storyIdea = it },
                placeholder = {
                    Text(
                        if (isArabic)
                            "اكتب ملخص فكرتك هنا (مثال: بطل يسافر عبر الزمن لشرح سر المادة المظلمة وتأثيرها على النجوم)"
                        else
                            "Describe your story idea here (e.g. hero travels through time explaining dark matter)"
                    )
                },
                minLines = 3,
                maxLines = 6,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("story_prompt_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AnimePurple,
                    unfocusedBorderColor = BorderSubtle,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                shape = RoundedCornerShape(12.dp)
            )
        }

        // Target Duration row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isArabic) "المدة الزمنية المستهدفة:" else "Target Duration:",
                    fontSize = 13.sp,
                    color = TextPrimary
                )
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf(30 to "30s", 60 to "60s", 180 to "3m", 300 to "5m").forEach { (sec, txt) ->
                        DurationChip(
                            label = txt,
                            isSelected = targetDuration == sec,
                            onClick = { targetDuration = sec },
                            modifier = Modifier.width(52.dp)
                        )
                    }
                }
            }
        }

        // Error message if any
        if (genError != null) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0x33EF4444))
                        .border(1.dp, Color(0xFFEF4444), RoundedCornerShape(10.dp))
                        .padding(12.dp)
                ) {
                    Text(text = genError!!, color = Color(0xFFFCA5A5), fontSize = 12.sp)
                }
            }
        }

        // Generate Action Button
        item {
            Button(
                onClick = {
                    viewModel.generateStory(
                        prompt = storyIdea.ifBlank { "قصة أنمي مشوقة" },
                        genre = selectedGenre,
                        targetDurationSeconds = targetDuration,
                        useAi = useAi
                    )
                },
                enabled = !isGenerating && selectedProject != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("generate_story_btn"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AnimePurple)
            ) {
                if (isGenerating) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (isArabic) "جاري صياغة القصة والمشاهد..." else "Crafting story & scenes...")
                } else {
                    Icon(Icons.Filled.AutoStories, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isArabic) "توليد القصة وإضافتها للمخطط الزمني" else "Generate Story & Populate Timeline",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}
