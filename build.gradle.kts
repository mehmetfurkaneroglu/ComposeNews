// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.ksp) apply false
    // Serialization
    id("org.jetbrains.kotlin.plugin.serialization") version "2.0.21" // Kotlin sürümünle aynı olmalı

    // Ktorfit Plugin
    id("de.jensklingenberg.ktorfit") version "1.11.1"
}