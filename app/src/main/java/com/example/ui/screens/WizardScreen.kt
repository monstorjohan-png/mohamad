package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.example.ui.theme.*
import com.example.ui.viewmodel.StudioViewModel

@Composable
fun WizardScreen(
    viewModel: StudioViewModel,
    modifier: Modifier = Modifier
) {
    val isArabic by viewModel.isArabic.collectAsStateWithLifecycle()

    var projectTitle by remember { mutableStateOf("") }
    var selectedAspect by remember { mutableStateOf(AspectRatio.SHORTS_9_16) }
    var selectedDuration by remember { mutableIntStateOf(60) }
    var selectedStyle by remember { mutableStateOf(AnimeStyle.ANIME_2D) }
    var selectedLanguage by remember { mutableStateOf(if (isArabic) "ar" else "en") }
    var selectedNarration by remember { mutableStateOf(NarrationType.LOCAL_TTS) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 32.dp)
    ) {
        item {
            Text(
                text = if (isArabic) "معالج إنشاء فيديو أنمي جديد" else "New Anime Video Wizard",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (isArabic) "حدد أبعاد الفيديو، مدته، أسلوبه الفني وطريقة السرد الصوتي" else "Configure video format, target duration, art style, and narration engine",
                fontSize = 12.sp,
                color = TextSecondary
            )
        }

        // Title Input
        item {
            OutlinedTextField(
                value = projectTitle,
                onValueChange = { projectTitle = it },
                label = { Text(if (isArabic) "عنوان المشروع" else "Project Title") },
                placeholder = { Text(if (isArabic) "مثال: شرارة نيو-طوكيو 2099" else "e.g., Neo-Tokyo Spark 2099") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("wizard_title_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AnimePurple,
                    unfocusedBorderColor = BorderSubtle,
                    focusedLabelColor = AnimePurple,
                    cursorColor = AnimePurple,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                shape = RoundedCornerShape(12.dp)
            )
        }

        // Step 1: Aspect Ratio
        item {
            WizardSectionHeader(
                step = "1",
                title = if (isArabic) "تنسيق وأبعاد الفيديو" else "Aspect Ratio & Format"
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AspectRatio.entries.forEach { aspect ->
                    val isSelected = selectedAspect == aspect
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) DarkSurfaceElevated else DarkSurface)
                            .border(1.5.dp, if (isSelected) AnimePurple else BorderSubtle, RoundedCornerShape(12.dp))
                            .clickable { selectedAspect = aspect }
                            .padding(vertical = 12.dp, horizontal = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = when (aspect) {
                                    AspectRatio.SHORTS_9_16 -> Icons.Filled.Smartphone
                                    AspectRatio.YOUTUBE_16_9 -> Icons.Filled.LiveTv
                                    AspectRatio.SQUARE_1_1 -> Icons.Filled.CropSquare
                                },
                                contentDescription = null,
                                tint = if (isSelected) AnimePurpleLight else TextSecondary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = if (isArabic) aspect.labelAr else aspect.labelEn,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) TextPrimary else TextSecondary
                            )
                        }
                    }
                }
            }
        }

        // Step 2: Duration selection
        item {
            WizardSectionHeader(
                step = "2",
                title = if (isArabic) "المدة الزمنية المستهدفة" else "Target Duration"
            )
            Spacer(modifier = Modifier.height(8.dp))

            val durations = listOf(
                15 to "15s (Shorts)",
                30 to "30s (Shorts)",
                60 to "60s (Shorts)",
                90 to "90s",
                180 to "3 Min",
                300 to "5 Min (قصة كاملة)",
                480 to "8 Min",
                600 to "10 Min"
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                durations.take(4).forEach { (seconds, label) ->
                    DurationChip(
                        label = label,
                        isSelected = selectedDuration == seconds,
                        onClick = { selectedDuration = seconds },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                durations.drop(4).forEach { (seconds, label) ->
                    DurationChip(
                        label = label,
                        isSelected = selectedDuration == seconds,
                        onClick = { selectedDuration = seconds },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Step 3: Visual Style
        item {
            WizardSectionHeader(
                step = "3",
                title = if (isArabic) "الأسلوب الفني والرسومي" else "Visual & Art Style"
            )
            Spacer(modifier = Modifier.height(8.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                AnimeStyle.entries.forEach { style ->
                    val isSelected = selectedStyle == style
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) DarkSurfaceElevated else DarkSurface)
                            .border(1.5.dp, if (isSelected) AnimeCyan else BorderSubtle, RoundedCornerShape(12.dp))
                            .clickable { selectedStyle = style }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = isSelected,
                            onClick = { selectedStyle = style },
                            colors = RadioButtonDefaults.colors(selectedColor = AnimeCyan)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = if (isArabic) style.labelAr else style.labelEn,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = style.descriptionAr,
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                    }
                }
            }
        }

        // Step 4: Narration & Language
        item {
            WizardSectionHeader(
                step = "4",
                title = if (isArabic) "لغة ومحرك التعليق الصوتي" else "Language & Narration"
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = { selectedLanguage = "ar" },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedLanguage == "ar") AnimePurple else DarkSurface
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("العربية (Arabic)")
                }
                Button(
                    onClick = { selectedLanguage = "en" },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedLanguage == "en") AnimePurple else DarkSurface
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("English")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            NarrationType.entries.forEach { narration ->
                val isSelected = selectedNarration == narration
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) DarkSurfaceElevated else DarkSurface)
                        .border(1.dp, if (isSelected) AnimePurpleLight else BorderSubtle, RoundedCornerShape(10.dp))
                        .clickable { selectedNarration = narration }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = isSelected,
                        onClick = { selectedNarration = narration },
                        colors = RadioButtonDefaults.colors(selectedColor = AnimePurpleLight)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isArabic) narration.labelAr else narration.labelEn,
                        fontSize = 12.sp,
                        color = TextPrimary
                    )
                }
            }
        }

        // Submit Button
        item {
            Button(
                onClick = {
                    viewModel.createProject(
                        title = projectTitle.ifBlank { if (isArabic) "مشروع أنمي جديد" else "New Anime Project" },
                        aspectRatio = selectedAspect,
                        targetDurationSeconds = selectedDuration,
                        style = selectedStyle,
                        language = selectedLanguage,
                        narrationType = selectedNarration
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("wizard_create_submit_btn"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AnimePurple)
            ) {
                Icon(Icons.Filled.CheckCircle, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isArabic) "إنشاء المشروع وبدء التحرير" else "Create Project & Start Editing",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun WizardSectionHeader(step: String, title: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(AnimePurple)
                .padding(2.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(text = step, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
    }
}

@Composable
fun DurationChip(label: String, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (isSelected) DarkSurfaceElevated else DarkSurface)
            .border(1.dp, if (isSelected) AnimeCyan else BorderSubtle, RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) AnimeCyanLight else TextSecondary
        )
    }
}
