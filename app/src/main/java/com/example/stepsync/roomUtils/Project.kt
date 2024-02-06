package com.example.stepsync.roomUtils

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.util.Date


@Entity(tableName = "project")
@Parcelize
data class Project(
    var name: String,
    var description: String?,
    var startDate: Date,
    var endDate: Date?,
    var status: Status,
    @ColumnInfo(name = "steps")
    var steps: List<Int>,
    var priority: Priority = Priority.MEDIUM,

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
): Parcelable {
}
