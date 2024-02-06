import org.jetbrains.kotlin.gradle.plugin.kotlinToolingVersion

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false
    kotlin("kapt") version "1.9.22"
    id("com.google.dagger.hilt.android") version "2.48.1" apply false
}

