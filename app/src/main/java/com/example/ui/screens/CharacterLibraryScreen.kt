package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.CharacterProfile
import com.example.ui.theme.*
import com.example.ui.viewmodel.StudioViewModel
import java.util.UUID

@Composable
fun CharacterLibraryScreen(
    viewModel: StudioViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isArabic by viewModel.isArabic.collectAsStateWithLifecycle()
    val characters by viewModel.allCharacters.collectAsStateWithLifecycle()
    val selectedCharacter by viewModel.selectedCharacter.collectAsStateWithLifecycle()
    val selectedScene by viewModel.selectedScene.collectAsStateWithLifecycle()

    var showCreateDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 10.dp, bottom = 32.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (isArabic) "مكتبة الشخصيات الأصلية" else "Original Characters Library",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = if (isArabic) "ابتكر شخصيات متكررة بمواصفات مظهر وملابس وأصوات متناسقة" else "Create recurring anime heroes with consistent profiles and attire",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }
                IconButton(
                    onClick = { showCreateDialog = true },
                    modifier = Modifier.testTag("add_char_btn")
                ) {
                    Icon(Icons.Filled.PersonAdd, contentDescription = "Add Character", tint = AnimePurpleLight)
                }
            }
        }

        if (characters.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(DarkSurface)
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isArabic) "لا توجد شخصيات محفوظة. انقر لإضافة شخصية جديدة!" else "No saved characters yet. Click to add a hero!",
                        color = TextSecondary,
                        fontSize = 13.sp
                    )
                }
            }
        } else {
            items(characters) { char ->
                val isSelected = char.id == selectedCharacter?.id
                CharacterCard(
                    character = char,
                    isSelected = isSelected,
                    isArabic = isArabic,
                    onSelect = { viewModel.selectCharacter(char) },
                    onAttachToScene = {
                        if (selectedScene != null) {
                            viewModel.updateScene(
                                selectedScene!!.copy(
                                    characterId = char.id,
                                    characterLayerType = "DRAWABLE_RES",
                                    characterLayerValue = char.imageValue
                                )
                            )
                        }
                    },
                    onDelete = { viewModel.deleteCharacter(char.id) }
                )
            }
        }
    }

    if (showCreateDialog) {
        CreateCharacterDialog(
            isArabic = isArabic,
            onDismiss = { showCreateDialog = false },
            onSave = { newChar ->
                viewModel.saveCharacter(newChar)
                showCreateDialog = false
            }
        )
    }
}

@Composable
fun CharacterCard(
    character: CharacterProfile,
    isSelected: Boolean,
    isArabic: Boolean,
    onSelect: () -> Unit,
    onAttachToScene: () -> Unit,
    onDelete: () -> Unit
) {
    val context = LocalContext.current
    val imgResId = remember(character.imageValue) {
        context.resources.getIdentifier(character.imageValue, "drawable", context.packageName)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(if (isSelected) DarkSurfaceElevated else DarkSurface)
            .border(1.5.dp, if (isSelected) AnimePurple else BorderSubtle, RoundedCornerShape(16.dp))
            .clickable { onSelect() }
            .padding(14.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(DarkSurfaceVariant)
                    .border(1.dp, AnimePurpleLight, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (imgResId != 0) {
                    Image(
                        painter = painterResource(id = imgResId),
                        contentDescription = character.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(Icons.Filled.Face, contentDescription = null, tint = AnimeCyan, modifier = Modifier.size(36.dp))
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = character.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Filled.Delete, contentDescription = "Delete", tint = Color(0xFFEF4444), modifier = Modifier.size(16.dp))
                    }
                }

                if (character.description.isNotBlank()) {
                    Text(
                        text = character.description,
                        fontSize = 11.sp,
                        color = TextSecondary,
                        maxLines = 2
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "${if (isArabic) "السمات:" else "Traits:"} ${character.personality} • ${character.clothing}",
                    fontSize = 11.sp,
                    color = AnimeCyanLight
                )

                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = onAttachToScene,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AnimePurple),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isArabic) "إدراج في المشهد الحالي" else "Assign to Current Scene",
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

@Composable
fun CreateCharacterDialog(
    isArabic: Boolean,
    onDismiss: () -> Unit,
    onSave: (CharacterProfile) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var personality by remember { mutableStateOf("شجاع وحماسي") }
    var clothing by remember { mutableStateOf("سترة نيون تكنولوجية") }
    var hairEyes by remember { mutableStateOf("شعر نيلي وعيون كهرمانية") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isArabic) "إنشاء شخصية أنمي جديدة" else "Create New Character") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(if (isArabic) "اسم الشخصية" else "Character Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = personality,
                    onValueChange = { personality = it },
                    label = { Text(if (isArabic) "الشخصية والطباع" else "Personality") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = clothing,
                    onValueChange = { clothing = it },
                    label = { Text(if (isArabic) "الملابس والأزياء" else "Attire & Clothing") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = hairEyes,
                    onValueChange = { hairEyes = it },
                    label = { Text(if (isArabic) "الشعر والعيون" else "Hair & Eyes") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val char = CharacterProfile(
                        id = UUID.randomUUID().toString(),
                        name = name.ifBlank { "بطل أنمي" },
                        description = description,
                        personality = personality,
                        clothing = clothing,
                        hairAndEyes = hairEyes,
                        imageType = "DRAWABLE_RES",
                        imageValue = "sample_character_ren"
                    )
                    onSave(char)
                },
                colors = ButtonDefaults.buttonColors(containerColor = AnimePurple)
            ) {
                Text(if (isArabic) "حفظ الشخصية" else "Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(if (isArabic) "إلغاء" else "Cancel")
            }
        },
        containerColor = DarkSurface
    )
}
