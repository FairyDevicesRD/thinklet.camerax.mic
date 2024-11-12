pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        listOf(
            "https://maven.pkg.github.com/FairyDevicesRD/thinklet.app.sdk",
        ).forEach { url ->
            maven {
                name = "GitHubPackages"
                setUrl(url)
                content {
                    includeGroup("ai.fd.thinklet")
                }
                credentials {
                    val properties = java.util.Properties()
                    properties.load(file("local.properties").inputStream())
                    username = System.getenv("USERNAME") ?: properties.getProperty("USERNAME") ?: ""
                    password = System.getenv("TOKEN") ?:properties.getProperty("TOKEN") ?: ""
                }
            }
        }
    }
    versionCatalogs {
        create("thinkletLibs") {
            from(files("gradle/thinklet.versions.toml"))
        }
    }
}

rootProject.name = "MultiMicCameraXRecorder"
include(":app")
