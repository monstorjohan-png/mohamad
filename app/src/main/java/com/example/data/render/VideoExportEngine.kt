package com.example.data.render

import android.content.Context
import android.graphics.*
import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaFormat
import android.media.MediaMuxer
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.view.Surface
import com.example.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.math.sin

class VideoExportEngine(private val context: Context) {

    private val sampleBitmaps = mutableMapOf<String, Bitmap>()

    private fun getOrLoadBitmap(key: String): Bitmap? {
        if (sampleBitmaps.containsKey(key)) return sampleBitmaps[key]
        return try {
            val resId = context.resources.getIdentifier(key, "drawable", context.packageName)
            if (resId != 0) {
                val bmp = BitmapFactory.decodeResource(context.resources, resId)
                if (bmp != null) {
                    sampleBitmaps[key] = bmp
                    return bmp
                }
            }
            // Check if it's a file path
            val file = File(key)
            if (file.exists()) {
                val bmp = BitmapFactory.decodeFile(file.absolutePath)
                if (bmp != null) {
                    sampleBitmaps[key] = bmp
                    return bmp
                }
            }
            null
        } catch (_: Exception) {
            null
        }
    }

    suspend fun renderVideo(
        project: Project,
        scenes: List<SceneItem>,
        outputFile: File,
        resolutionWidth: Int = 720,
        resolutionHeight: Int = 1280,
        fps: Int = 30,
        burnSubtitles: Boolean = true,
        onProgress: (progress: Float, currentScene: Int, totalScenes: Int, status: String) -> Unit
    ): Result<File> = withContext(Dispatchers.Default) {
        if (scenes.isEmpty()) {
            return@withContext Result.failure(IllegalArgumentException("المشروع لا يحتوي على أي مشاهد للتصدير"))
        }

        var encoder: MediaCodec? = null
        var inputSurface: Surface? = null
        var muxer: MediaMuxer? = null
        var videoTrackIndex = -1
        var muxerStarted = false

        try {
            onProgress(0.01f, 1, scenes.size, "تهيئة مشفر الفيديو H.264...")

            val mimeType = MediaFormat.MIMETYPE_VIDEO_AVC
            val format = MediaFormat.createVideoFormat(mimeType, resolutionWidth, resolutionHeight).apply {
                setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface)
                setInteger(MediaFormat.KEY_BIT_RATE, (resolutionWidth * resolutionHeight * 3.5).toInt())
                setInteger(MediaFormat.KEY_FRAME_RATE, fps)
                setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1)
            }

            encoder = MediaCodec.createEncoderByType(mimeType)
            encoder.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
            inputSurface = encoder.createInputSurface()
            encoder.start()

            muxer = MediaMuxer(outputFile.absolutePath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)

            val totalDurationSeconds = scenes.sumOf { it.durationSeconds.toDouble() }.toFloat()
            val totalFrames = (totalDurationSeconds * fps).toInt().coerceAtLeast(1)

            val bufferInfo = MediaCodec.BufferInfo()
            val drainTimeoutUs = 5000L

            var currentSceneIndex = 0
            var sceneStartFrame = 0
            var sceneEndFrame = (scenes[0].durationSeconds * fps).toInt()

            val textPaint = TextPaint().apply {
                isAntiAlias = true
                color = Color.WHITE
                textSize = (resolutionWidth * 0.042f).coerceAtLeast(24f)
                typeface = Typeface.DEFAULT_BOLD
                setShadowLayer(8f, 2f, 2f, Color.BLACK)
            }

            val strokePaint = TextPaint().apply {
                isAntiAlias = true
                color = Color.BLACK
                style = Paint.Style.STROKE
                strokeWidth = 6f
                textSize = (resolutionWidth * 0.042f).coerceAtLeast(24f)
                typeface = Typeface.DEFAULT_BOLD
            }

            val bgBoxPaint = Paint().apply {
                isAntiAlias = true
                color = Color.argb(160, 11, 14, 23)
                style = Paint.Style.FILL
            }

            for (frameIndex in 0 until totalFrames) {
                if (!coroutineContext.isActive) {
                    throw InterruptedException("تم إلغاء عملية التصدير بواسطة المستخدم")
                }

                // Check scene transition
                while (frameIndex >= sceneEndFrame && currentSceneIndex < scenes.size - 1) {
                    currentSceneIndex++
                    sceneStartFrame = sceneEndFrame
                    sceneEndFrame = sceneStartFrame + (scenes[currentSceneIndex].durationSeconds * fps).toInt()
                }

                val activeScene = scenes[currentSceneIndex]
                val sceneFrame = frameIndex - sceneStartFrame
                val sceneTotalFrames = (activeScene.durationSeconds * fps).toInt().coerceAtLeast(1)
                val sceneProgress = (sceneFrame.toFloat() / sceneTotalFrames).coerceIn(0f, 1f)

                // Render frame onto inputSurface Canvas
                val canvas = inputSurface.lockCanvas(null)
                try {
                    // 1. Draw Background
                    val bgBitmap = getOrLoadBitmap(activeScene.backgroundValue)
                    if (bgBitmap != null) {
                        drawWithMotion(canvas, bgBitmap, activeScene.cameraMotion, sceneProgress, resolutionWidth, resolutionHeight)
                    } else {
                        // Solid default dark navy background
                        canvas.drawColor(Color.rgb(11, 14, 23))
                    }

                    // 2. Draw Character Layer
                    if (activeScene.characterLayerType != "NONE" && !activeScene.characterLayerValue.isNullOrBlank()) {
                        val charBitmap = getOrLoadBitmap(activeScene.characterLayerValue)
                        if (charBitmap != null) {
                            drawCharacterLayer(canvas, charBitmap, activeScene, resolutionWidth, resolutionHeight)
                        }
                    }

                    // 3. Draw Subtitle / Dialogue overlay
                    if (burnSubtitles) {
                        val subtitle = activeScene.subtitleText.ifBlank { activeScene.narrationText }.ifBlank { activeScene.dialogueText }
                        if (subtitle.isNotBlank()) {
                            drawBurnedInSubtitle(canvas, subtitle, resolutionWidth, resolutionHeight, textPaint, strokePaint, bgBoxPaint)
                        }
                    }
                } finally {
                    inputSurface.unlockCanvasAndPost(canvas)
                }

                // Drain output buffers
                var encoderStatus = encoder.dequeueOutputBuffer(bufferInfo, drainTimeoutUs)
                while (encoderStatus >= 0 || encoderStatus == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                    if (encoderStatus == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                        if (muxerStarted) {
                            throw RuntimeException("Format changed twice")
                        }
                        val newFormat = encoder.outputFormat
                        videoTrackIndex = muxer.addTrack(newFormat)
                        muxer.start()
                        muxerStarted = true
                    } else {
                        val encodedData = encoder.getOutputBuffer(encoderStatus)
                        if (encodedData != null && bufferInfo.size > 0 && muxerStarted) {
                            encodedData.position(bufferInfo.offset)
                            encodedData.limit(bufferInfo.offset + bufferInfo.size)
                            muxer.writeSampleData(videoTrackIndex, encodedData, bufferInfo)
                        }
                        encoder.releaseOutputBuffer(encoderStatus, false)
                    }
                    encoderStatus = encoder.dequeueOutputBuffer(bufferInfo, drainTimeoutUs)
                }

                // Report progress
                val progress = (frameIndex + 1).toFloat() / totalFrames
                if (frameIndex % (fps / 2).coerceAtLeast(1) == 0 || frameIndex == totalFrames - 1) {
                    val percent = (progress * 100).toInt()
                    val statusText = "جاري ترميز المشهد (${currentSceneIndex + 1}/${scenes.size}) - الإطار $frameIndex / $totalFrames ($percent%)"
                    onProgress(progress, currentSceneIndex + 1, scenes.size, statusText)
                }
            }

            // Signal End of Stream
            encoder.signalEndOfInputStream()

            // Drain remaining frames
            var eos = false
            while (!eos) {
                val encoderStatus = encoder.dequeueOutputBuffer(bufferInfo, 10000L)
                if (encoderStatus == MediaCodec.INFO_TRY_AGAIN_LATER) {
                    eos = true
                } else if (encoderStatus >= 0) {
                    if (bufferInfo.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM != 0) {
                        eos = true
                    }
                    val encodedData = encoder.getOutputBuffer(encoderStatus)
                    if (encodedData != null && bufferInfo.size > 0 && muxerStarted) {
                        encodedData.position(bufferInfo.offset)
                        encodedData.limit(bufferInfo.offset + bufferInfo.size)
                        muxer.writeSampleData(videoTrackIndex, encodedData, bufferInfo)
                    }
                    encoder.releaseOutputBuffer(encoderStatus, false)
                } else {
                    eos = true
                }
            }

            onProgress(1.0f, scenes.size, scenes.size, "اكتمل تصدير فيديو MP4 بنجاح!")
            Result.success(outputFile)
        } catch (e: Exception) {
            outputFile.delete()
            Result.failure(e)
        } finally {
            try {
                encoder?.stop()
                encoder?.release()
            } catch (_: Exception) {}
            try {
                inputSurface?.release()
            } catch (_: Exception) {}
            try {
                if (muxerStarted) {
                    muxer?.stop()
                }
                muxer?.release()
            } catch (_: Exception) {}
        }
    }

    private fun drawWithMotion(
        canvas: Canvas,
        bitmap: Bitmap,
        motion: CameraMotionType,
        progress: Float,
        targetWidth: Int,
        targetHeight: Int
    ) {
        val matrix = Matrix()
        val scaleX = targetWidth.toFloat() / bitmap.width
        val scaleY = targetHeight.toFloat() / bitmap.height
        val baseScale = maxOf(scaleX, scaleY)

        val dynamicScale: Float
        val transX: Float
        val transY: Float

        when (motion) {
            CameraMotionType.KEN_BURNS_ZOOM_IN -> {
                dynamicScale = baseScale * (1.0f + 0.15f * progress)
                transX = (targetWidth - bitmap.width * dynamicScale) / 2f
                transY = (targetHeight - bitmap.height * dynamicScale) / 2f
            }
            CameraMotionType.KEN_BURNS_ZOOM_OUT -> {
                dynamicScale = baseScale * (1.15f - 0.15f * progress)
                transX = (targetWidth - bitmap.width * dynamicScale) / 2f
                transY = (targetHeight - bitmap.height * dynamicScale) / 2f
            }
            CameraMotionType.PAN_LEFT_TO_RIGHT -> {
                dynamicScale = baseScale * 1.10f
                val maxPan = (bitmap.width * dynamicScale - targetWidth).coerceAtLeast(0f)
                transX = -maxPan * (1f - progress)
                transY = (targetHeight - bitmap.height * dynamicScale) / 2f
            }
            CameraMotionType.PAN_RIGHT_TO_LEFT -> {
                dynamicScale = baseScale * 1.10f
                val maxPan = (bitmap.width * dynamicScale - targetWidth).coerceAtLeast(0f)
                transX = -maxPan * progress
                transY = (targetHeight - bitmap.height * dynamicScale) / 2f
            }
            CameraMotionType.DRAMATIC_SHAKE -> {
                dynamicScale = baseScale * 1.05f
                val shakeOffset = sin(progress * 40.0).toFloat() * 12f
                transX = ((targetWidth - bitmap.width * dynamicScale) / 2f) + shakeOffset
                transY = ((targetHeight - bitmap.height * dynamicScale) / 2f) + (shakeOffset * 0.5f)
            }
            CameraMotionType.STATIC -> {
                dynamicScale = baseScale
                transX = (targetWidth - bitmap.width * dynamicScale) / 2f
                transY = (targetHeight - bitmap.height * dynamicScale) / 2f
            }
        }

        matrix.setScale(dynamicScale, dynamicScale)
        matrix.postTranslate(transX, transY)
        canvas.drawBitmap(bitmap, matrix, null)
    }

    private fun drawCharacterLayer(
        canvas: Canvas,
        bitmap: Bitmap,
        scene: SceneItem,
        targetWidth: Int,
        targetHeight: Int
    ) {
        val matrix = Matrix()
        val targetCharHeight = targetHeight * 0.70f * scene.layerScale
        val scale = targetCharHeight / bitmap.height

        val xPos = ((targetWidth - bitmap.width * scale) / 2f) + (scene.layerOffsetX * targetWidth)
        val yPos = (targetHeight - bitmap.height * scale) + (scene.layerOffsetY * targetHeight)

        matrix.setScale(scale, scale)
        matrix.postTranslate(xPos, yPos)
        canvas.drawBitmap(bitmap, matrix, null)
    }

    private fun drawBurnedInSubtitle(
        canvas: Canvas,
        text: String,
        targetWidth: Int,
        targetHeight: Int,
        textPaint: TextPaint,
        strokePaint: TextPaint,
        bgBoxPaint: Paint
    ) {
        val horizontalMargin = targetWidth * 0.08f
        val availableWidth = (targetWidth - (horizontalMargin * 2)).toInt().coerceAtLeast(100)

        val staticLayout = StaticLayout.Builder.obtain(text, 0, text.length, textPaint, availableWidth)
            .setAlignment(Layout.Alignment.ALIGN_CENTER)
            .setLineSpacing(4f, 1f)
            .setIncludePad(true)
            .build()

        val textHeight = staticLayout.height
        val bottomMargin = targetHeight * 0.09f
        val topY = targetHeight - bottomMargin - textHeight
        val bgPadding = 24f

        val rect = RectF(
            horizontalMargin - bgPadding,
            topY - bgPadding,
            horizontalMargin + availableWidth + bgPadding,
            topY + textHeight + bgPadding
        )
        canvas.drawRoundRect(rect, 24f, 24f, bgBoxPaint)

        canvas.save()
        canvas.translate(horizontalMargin, topY)

        val strokeLayout = StaticLayout.Builder.obtain(text, 0, text.length, strokePaint, availableWidth)
            .setAlignment(Layout.Alignment.ALIGN_CENTER)
            .setLineSpacing(4f, 1f)
            .setIncludePad(true)
            .build()
        strokeLayout.draw(canvas)
        staticLayout.draw(canvas)

        canvas.restore()
    }
}
