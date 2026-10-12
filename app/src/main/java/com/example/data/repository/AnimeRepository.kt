package com.example.data.repository

import com.example.data.local.AnimeDatabase
import com.example.data.model.CharacterProfile
import com.example.data.model.Project
import com.example.data.model.SceneItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import java.util.UUID

class AnimeRepository(private val database: AnimeDatabase) {
    val allProjects: Flow<List<Project>> = database.projectDao().getAllProjects()
    val allCharacters: Flow<List<CharacterProfile>> = database.characterDao().getAllCharacters()

    fun getScenesForProject(projectId: String): Flow<List<SceneItem>> {
        return database.sceneDao().getScenesForProject(projectId)
    }

    suspend fun getScenesList(projectId: String): List<SceneItem> {
        return database.sceneDao().getScenesListForProject(projectId)
    }

    fun getProjectFlow(id: String): Flow<Project?> {
        return database.projectDao().getProjectFlow(id)
    }

    suspend fun getProjectById(id: String): Project? {
        return database.projectDao().getProjectById(id)
    }

    suspend fun insertProject(project: Project) {
        database.projectDao().insertProject(project)
    }

    suspend fun updateProject(project: Project) {
        database.projectDao().updateProject(project.copy(updatedAt = System.currentTimeMillis()))
    }

    suspend fun deleteProject(projectId: String) {
        database.sceneDao().deleteScenesForProject(projectId)
        database.projectDao().deleteProject(projectId)
    }

    suspend fun duplicateProject(sourceProjectId: String, newTitle: String): String {
        val original = database.projectDao().getProjectById(sourceProjectId) ?: return ""
        val newProjectId = UUID.randomUUID().toString()
        val duplicatedProject = original.copy(
            id = newProjectId,
            title = newTitle,
            isSample = false,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        database.projectDao().insertProject(duplicatedProject)

        val originalScenes = database.sceneDao().getScenesListForProject(sourceProjectId)
        val duplicatedScenes = originalScenes.map { scene ->
            scene.copy(
                id = UUID.randomUUID().toString(),
                projectId = newProjectId
            )
        }
        database.sceneDao().insertScenes(duplicatedScenes)
        return newProjectId
    }

    suspend fun insertScene(scene: SceneItem) {
        database.sceneDao().insertScene(scene)
        touchProject(scene.projectId)
    }

    suspend fun insertScenes(scenes: List<SceneItem>) {
        if (scenes.isNotEmpty()) {
            database.sceneDao().insertScenes(scenes)
            touchProject(scenes.first().projectId)
        }
    }

    suspend fun updateScene(scene: SceneItem) {
        database.sceneDao().updateScene(scene)
        touchProject(scene.projectId)
    }

    suspend fun deleteScene(scene: SceneItem) {
        database.sceneDao().deleteScene(scene.id)
        touchProject(scene.projectId)
    }

    suspend fun duplicateScene(scene: SceneItem) {
        val newScene = scene.copy(
            id = UUID.randomUUID().toString(),
            orderIndex = scene.orderIndex + 1,
            title = "${scene.title} (نسخة)"
        )
        database.sceneDao().insertScene(newScene)
        touchProject(scene.projectId)
    }

    suspend fun insertCharacter(character: CharacterProfile) {
        database.characterDao().insertCharacter(character)
    }

    suspend fun updateCharacter(character: CharacterProfile) {
        database.characterDao().updateCharacter(character)
    }

    suspend fun deleteCharacter(id: String) {
        database.characterDao().deleteCharacter(id)
    }

    suspend fun ensureDefaultDataLoaded() {
        val projects = allProjects.firstOrNull()
        if (projects.isNullOrEmpty()) {
            AnimeDatabase.seedDefaultData(database)
        }
    }

    private suspend fun touchProject(projectId: String) {
        val p = database.projectDao().getProjectById(projectId)
        if (p != null) {
            database.projectDao().updateProject(p.copy(updatedAt = System.currentTimeMillis()))
        }
    }
}
