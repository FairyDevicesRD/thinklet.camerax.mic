package ai.fd.thinklet.camerax.mic.builder

import com.android.build.gradle.LibraryExtension
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.ConfigurationContainer
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.publish.PublishingExtension
import org.gradle.kotlin.dsl.getByType

internal fun Project.android(block: LibraryExtension.() -> Unit) =
    extensions.getByType(LibraryExtension::class.java).block()

internal fun Project.publishing(block: PublishingExtension.() -> Unit) =
    extensions.getByType(PublishingExtension::class.java).block()

internal val Project.libs: VersionCatalog
    get() = extensions.getByType<VersionCatalogsExtension>().named("libs")
