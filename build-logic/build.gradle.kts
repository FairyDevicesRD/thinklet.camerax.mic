plugins {
    `kotlin-dsl`
}

dependencies {
    compileOnly(libs.android.gradle)
    compileOnly(libs.kotlin.gradle)
}

gradlePlugin {
    plugins {
        register("thinkletCameraXMicBuilder") {
            id = "ai.fd.thinklet.camerax.mic.builder"
            implementationClass = "ThinkletCameraXMicBuilderPlugin"
        }
    }
}
