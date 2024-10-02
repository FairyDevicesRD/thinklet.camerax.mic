plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("maven-publish")
}

android {
    namespace = "ai.fd.thinklet.camerax.mic.xfe"
    compileSdk = 34

    defaultConfig {
        minSdk = 27

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    // TODO: buildSrcなどに移行したいな．．．
    libraryVariants.all {
        val variant = this
        variant.outputs
            .map { it as com.android.build.gradle.internal.api.BaseVariantOutputImpl }
            .forEach { output ->
                if (output.outputFileName?.endsWith("-release.aar") == true) {
                    val outputFileName = "thinklet.cameraX.mic_${project.name}_v${libs.versions.projectVersion.get()}.aar"
                    output.outputFileName = outputFileName
                }
            }
    }
}

dependencies {
    implementation(project(":core"))
    implementation(libs.androidx.annotation)
    implementation(thinkletLibs.camerax.camera.video)
    implementation(thinkletLibs.sdk.audio)
    testImplementation(libs.junit)
}

// maven-publish //
publishing {
    publications {
        create<MavenPublication>("maven") {
            val libVersion = libs.versions.projectVersion.get()

            // TODO: ここ微妙...
            artifact("${layout.buildDirectory.get()}/outputs/aar/thinklet.cameraX.mic_${project.name}_v${libVersion}.aar")
            groupId = "ai.fd.thinklet.camerax"
            artifactId = project.name
            version = libVersion

            pom {
                name = "THINKLET CameraX mic library"
                description = "THINKLET CameraX XFE module"
                licenses {
                    developers {
                        developer {
                            id = "Fairydevices"
                            name = "Fairy Devices"
                        }
                    }
                    // TODO: apply Apache 2?
                }
                withXml {
                    val dependenciesNode = asNode().appendNode("dependencies")
                    project.configurations.implementation.get().allDependencies.forEach {
                        if (it.group == null || it.version == null || it.version == "unspecified") return@forEach
                        println("Added dependency. ${it.group}:${it.name}:${it.version}")

                        val dependencyNode = dependenciesNode.appendNode("dependency")
                        dependencyNode.appendNode("groupId", it.group)
                        dependencyNode.appendNode("artifactId", it.name)
                        dependencyNode.appendNode("version", it.version)
                    }
                }
            }
        }
    }
}
