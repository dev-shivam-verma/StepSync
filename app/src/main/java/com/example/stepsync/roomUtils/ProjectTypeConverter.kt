package com.example.stepsync.roomUtils

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import java.util.Date

@ProvidedTypeConverter
class ProjectTypeConverter {

    @TypeConverter
    fun fromDate(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun toDate(time: Long?): Date? {
        return if (time == null) null else Date(time)
    }

    @TypeConverter
    fun fromListInt(list: List<Int>?): String? {
        return if (list == null) "" else list.joinToString(",")
    }

    @TypeConverter
    fun toListInt(string: String?): List<Int>? {
        if (string.isNullOrEmpty())
            return  listOf<Int>()
        else{
            val list = string.split(",")
            val listInt = mutableListOf<Int>()
            list.forEach{
                if (!it.isNullOrBlank())
                    listInt.add(it.toInt())
            }

            return listInt
        }
    }

}