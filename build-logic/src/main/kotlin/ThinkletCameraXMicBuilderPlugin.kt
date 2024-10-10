import ai.fd.thinklet.camerax.mic.builder.configureCommon
import ai.fd.thinklet.camerax.mic.builder.configurePublish
import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.gradle.LibraryPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.withType

class ThinkletCameraXMicBuilderPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            plugins.withType<LibraryPlugin>().configureEach {
                extensions.getByType(AndroidComponentsExtension::class.java).apply {
                    configureCommon()
                    configurePublish()
                }
            }
        }
    }
}
