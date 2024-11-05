package ai.fd.thinklet.camerax.mic.builder

import org.gradle.api.Project
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.create
import java.util.Properties

internal fun Project.configurePublish() {
    val libraryVersion = libs.findVersion("projectVersion").get().requiredVersion
    afterEvaluate {
        publishing {
            publications {
                create<MavenPublication>("maven") {
                    from(components.getByName("release"))
                    groupId = "ai.fd.thinklet"
                    artifactId = "camerax-mic-${project.name}"
                    version = libraryVersion

                    pom {
                        name = "THINKLET CameraX mic library"
                        description = "THINKLET CameraX ${project.name} module"
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
    }
}
