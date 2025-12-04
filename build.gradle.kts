buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        // Gradle plugin Android
        classpath("com.android.tools.build:gradle:8.1.0")
        // 🔥 Plugin Google Services pour Firebase
        classpath("com.google.gms:google-services:4.4.0")
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
}
