package com.example.stepsync.hiltModules

import android.content.Context
import com.example.stepsync.sharedpreferanceUtils.UserData
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class userDataModule {

    @Provides
    @Singleton
    fun getUserData(
        @ApplicationContext context: Context
    ): UserData {
        return UserData(context)
    }
}