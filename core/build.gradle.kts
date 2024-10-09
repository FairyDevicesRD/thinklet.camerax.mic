import java.util.Properties

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("maven-publish")
}

android {
    namespace = "ai.fd.thinklet.camerax.mic"
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
                description = "THINKLET CameraX core module"
                licenses {
                    license {
                        name = "The Apache License, Version 2.0"
                        url = "http://www.apache.org/licenses/LICENSE-2.0.txt"
                    }
                }
                developers {
                    developer {
                        id = "Fairydevices"
                        name = "Fairy Devices"
                    }
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
    repositories {
        maven {
            val properties = Properties()
            properties.load(project.rootProject.file("local.properties").inputStream())
            name = "GithubPackages"
            val u = properties.getProperty("url") ?: System.getenv("URL")
            url = uri(u ?: "undefine")
            credentials {
                username = properties.getProperty("username") ?: System.getenv("USERNAME")
                password = properties.getProperty("token") ?: System.getenv("TOKEN")
            }
        }
    }
}
