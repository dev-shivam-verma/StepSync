package com.example.stepsync.roomUtils

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface ProjectDao  {

    @Insert
    suspend fun insertProject(project: Project)

    @Update
    suspend fun updateProject(project: Project)

    @Delete
    suspend fun deleteProject(project: Project)

    @Query("SELECT * FROM project")
    suspend fun getAllProjects(): List<Project?>

    @Query("SELECT * FROM project WHERE id = :projectId")
    suspend fun getProjectByProjectId(projectId: Int): Project

    @Query("SELECT * FROM project WHERE name LIKE '%' || :projectName || '%'")
    suspend fun getProjectsByProjectName(projectName: String): List<Project>

    @Query("SELECT * FROM step ORDER BY id ")
    suspend fun getStepsOrderedByIds(): List<Step>

    @Insert
    suspend fun insertStep(step: Step)

    @Update
    suspend fun updateStep(step: Step)

    @Delete
    suspend fun deleteStep(step: Step)

    @Query("SELECT * FROM step")
    suspend fun getAllSteps(): List<Step?>

    @Query("SELECT * FROM step WHERE id = :stepId")
    suspend fun getStepByStepId(stepId: Int): Step


}