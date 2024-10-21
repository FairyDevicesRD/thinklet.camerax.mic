plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("maven-publish")
    id("ai.fd.thinklet.camerax.mic.builder")
}

android {
    namespace = "ai.fd.thinklet.camerax.mic"
}
