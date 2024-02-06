package com.example.stepsync.hiltModules

import android.content.Context
import androidx.room.Room
import com.example.stepsync.roomUtils.DBProjectHolder
import com.example.stepsync.roomUtils.ProjectTypeConverter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

        @Provides
        @Singleton
        fun provideDatabase(@ApplicationContext context: Context): DBProjectHolder {
            return Room.databaseBuilder(
                context,
                DBProjectHolder::class.java,
                "project_db"
            )
                .addTypeConverter(ProjectTypeConverter())
                .build()
        }
    }