package com.example.stepsync.roomUtils

import androidx.room.Database
import androidx.room.DeleteTable
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@TypeConverters(ProjectTypeConverter::class)
@Database(entities = [Project::class,Step::class], version = 1)
abstract class DBProjectHolder: RoomDatabase() {


    abstract fun getProjectDao(): ProjectDao
}