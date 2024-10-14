plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("maven-publish")
    id("ai.fd.thinklet.camerax.mic.builder")
}

android {
    namespace = "ai.fd.thinklet.camerax.mic.xfe"
}

dependencies {
    implementation(project(":core"))
    implementation(libs.androidx.annotation)
    implementation(thinkletLibs.camerax.camera.video)
    implementation(thinkletLibs.sdk.audio)
    testImplementation(libs.junit)
}
