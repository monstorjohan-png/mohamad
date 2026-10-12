package com.example

import com.example.data.model.AspectRatio
import com.example.data.model.CameraMotionType
import com.example.data.model.Project
import com.example.data.model.SceneItem
import com.example.data.render.SrtExporter
import org.junit.Assert.*
import org.junit.Test
import java.util.UUID

class ExampleUnitTest {

    @Test
    fun testTimelineDurationCalculation() {
        val scenes = listOf(
            SceneItem(id = "1", projectId = "p1", orderIndex = 0, title = "Scene 1", durationSeconds = 6.5f),
            SceneItem(id = "2", projectId = "p1", orderIndex = 1, title = "Scene 2", durationSeconds = 8.5f),
            SceneItem(id = "3", projectId = "p1", orderIndex = 2, title = "Scene 3", durationSeconds = 15.0f)
        )
        val total = scenes.sumOf { it.durationSeconds.toDouble() }.toFloat()
        assertEquals(30.0f, total, 0.001f)
    }

    @Test
    fun testFiveMinuteStoryTargetCalculation() {
        val targetSeconds = 300 // 5 minutes
        val sceneCount = 15
        val durationPerScene = targetSeconds.toFloat() / sceneCount
        assertEquals(20.0f, durationPerScene, 0.001f)

        val scenes = (0 until sceneCount).map { i ->
            SceneItem(id = "$i", projectId = "p", orderIndex = i, title = "Scene $i", durationSeconds = durationPerScene)
        }
        val calculatedTotal = scenes.sumOf { it.durationSeconds.toDouble() }.toFloat()
        assertEquals(300.0f, calculatedTotal, 0.001f)
    }

    @Test
    fun testSceneOrderIndex() {
        val scenes = listOf(
            SceneItem(id = "a", projectId = "p", orderIndex = 2, title = "Third"),
            SceneItem(id = "b", projectId = "p", orderIndex = 0, title = "First"),
            SceneItem(id = "c", projectId = "p", orderIndex = 1, title = "Second")
        ).sortedBy { it.orderIndex }

        assertEquals("First", scenes[0].title)
        assertEquals("Second", scenes[1].title)
        assertEquals("Third", scenes[2].title)
    }

    @Test
    fun testSrtSubtitleGeneration() {
        val scenes = listOf(
            SceneItem(
                id = "1",
                projectId = "p",
                orderIndex = 0,
                title = "S1",
                durationSeconds = 5.0f,
                subtitleText = "أهلاً بكم في عالم الأنمي"
            ),
            SceneItem(
                id = "2",
                projectId = "p",
                orderIndex = 1,
                title = "S2",
                durationSeconds = 6.0f,
                subtitleText = "Welcome to Anime Studio AI"
            )
        )
        val srt = SrtExporter.generateSrtContent(scenes)
        assertTrue(srt.contains("1\n00:00:00,000 --> 00:00:05,000\nأهلاً بكم في عالم الأنمي"))
        assertTrue(srt.contains("2\n00:00:05,000 --> 00:00:11,000\nWelcome to Anime Studio AI"))
    }

    @Test
    fun testAspectRatioDisplayRatio() {
        val shorts = AspectRatio.SHORTS_9_16
        val youtube = AspectRatio.YOUTUBE_16_9
        val square = AspectRatio.SQUARE_1_1

        assertEquals(9f / 16f, shorts.displayRatio, 0.001f)
        assertEquals(16f / 9f, youtube.displayRatio, 0.001f)
        assertEquals(1f, square.displayRatio, 0.001f)
    }
}
