package com.example.data.render

import com.example.data.model.SceneItem
import java.io.File
import java.util.Locale

object SrtExporter {

    fun generateSrtContent(scenes: List<SceneItem>): String {
        val sb = StringBuilder()
        var currentStartSeconds = 0.0f
        var counter = 1

        for (scene in scenes) {
            val text = scene.subtitleText.ifBlank { scene.narrationText }.ifBlank { scene.dialogueText }
            if (text.isNotBlank()) {
                val endSeconds = currentStartSeconds + scene.durationSeconds
                sb.append(counter++).append("\n")
                sb.append(formatTime(currentStartSeconds)).append(" --> ").append(formatTime(endSeconds)).append("\n")
                sb.append(text).append("\n\n")
            }
            currentStartSeconds += scene.durationSeconds
        }
        return sb.toString()
    }

    fun exportToFile(scenes: List<SceneItem>, destinationFile: File): Boolean {
        return try {
            val srt = generateSrtContent(scenes)
            destinationFile.writeText(srt, Charsets.UTF_8)
            true
        } catch (_: Exception) {
            false
        }
    }

    private fun formatTime(seconds: Float): String {
        val totalMillis = (seconds * 1000).toLong()
        val hours = totalMillis / 3600000
        val minutes = (totalMillis % 3600000) / 60000
        val secs = (totalMillis % 60000) / 1000
        val millis = totalMillis % 1000
        return String.format(Locale.US, "%02d:%02d:%02d,%03d", hours, minutes, secs, millis)
    }
}
