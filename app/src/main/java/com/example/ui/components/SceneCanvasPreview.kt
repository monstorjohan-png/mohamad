package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AspectRatio
import com.example.data.model.CameraMotionType
import com.example.data.model.SceneItem
import com.example.ui.theme.*

@Composable
fun SceneCanvasPreview(
    scene: SceneItem?,
    aspectRatio: AspectRatio,
    modifier: Modifier = Modifier,
    isPlaying: Boolean = true,
    showSubtitles: Boolean = true
) {
    val context = LocalContext.current

    // Animated motion progress (0f to 1f) looping or playing
    val infiniteTransition = rememberInfiniteTransition(label = "motion_anim")
    val animProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = ((scene?.durationSeconds ?: 5f) * 1000).toInt().coerceAtLeast(1000),
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "progress"
    )

    val activeProgress = if (isPlaying) animProgress else 0.5f

    // Calculate motion transforms based on camera motion type
    val (scale, transX, transY) = remember(scene?.cameraMotion, activeProgress) {
        when (scene?.cameraMotion) {
            CameraMotionType.KEN_BURNS_ZOOM_IN -> {
                Triple(1.0f + (0.18f * activeProgress), 0f, 0f)
            }
            CameraMotionType.KEN_BURNS_ZOOM_OUT -> {
                Triple(1.18f - (0.18f * activeProgress), 0f, 0f)
            }
            CameraMotionType.PAN_LEFT_TO_RIGHT -> {
                Triple(1.10f, (activeProgress - 0.5f) * 40f, 0f)
            }
            CameraMotionType.PAN_RIGHT_TO_LEFT -> {
                Triple(1.10f, (0.5f - activeProgress) * 40f, 0f)
            }
            CameraMotionType.DRAMATIC_SHAKE -> {
                val shake = kotlin.math.sin(activeProgress * 30.0).toFloat() * 8f
                Triple(1.06f, shake, shake * 0.5f)
            }
            else -> Triple(1.0f, 0f, 0f)
        }
    }

    Box(
        modifier = modifier
            .aspectRatio(aspectRatio.displayRatio)
            .clip(RoundedCornerShape(16.dp))
            .background(DarkBg)
            .border(1.5.dp, BorderSubtle, RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.Center
    ) {
        if (scene != null) {
            // Background Layer
            val bgResId = remember(scene.backgroundValue) {
                context.resources.getIdentifier(scene.backgroundValue, "drawable", context.packageName)
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        translationX = transX
                        translationY = transY
                    }
            ) {
                if (bgResId != 0) {
                    Image(
                        painter = painterResource(id = bgResId),
                        contentDescription = "Scene Background",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(DarkSurfaceVariant)
                    )
                }
            }

            // Character Layer
            if (scene.characterLayerType != "NONE" && !scene.characterLayerValue.isNullOrBlank()) {
                val charResId = remember(scene.characterLayerValue) {
                    context.resources.getIdentifier(scene.characterLayerValue, "drawable", context.packageName)
                }
                if (charResId != 0) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = 16.dp),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        Image(
                            painter = painterResource(id = charResId),
                            contentDescription = "Character Layer",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .fillMaxHeight(0.75f * scene.layerScale)
                                .offset(x = (scene.layerOffsetX * 50).dp, y = (scene.layerOffsetY * 50).dp)
                        )
                    }
                }
            }

            // Subtitle overlay
            if (showSubtitles) {
                val subtitleText = scene.subtitleText.ifBlank { scene.narrationText }.ifBlank { scene.dialogueText }
                if (subtitleText.isNotBlank()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                            .padding(horizontal = 16.dp, vertical = 20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xCC0B0E17))
                                .border(1.dp, Color(0x668B5CF6), RoundedCornerShape(12.dp))
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = subtitleText,
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            // Motion & Scene indicator chip in top corner
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(10.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xBB141926))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "${scene.title} • ${scene.cameraMotion.labelAr}",
                    color = AnimeCyanLight,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        } else {
            Text(
                text = "لا يوجد مشهد محدد",
                color = TextSecondary,
                fontSize = 14.sp
            )
        }
    }
}
