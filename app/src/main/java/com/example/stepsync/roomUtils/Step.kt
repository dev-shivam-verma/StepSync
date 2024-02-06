package com.example.stepsync.roomUtils

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "step")
data class Step(
    val name: String,
    var status: Status,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
)
