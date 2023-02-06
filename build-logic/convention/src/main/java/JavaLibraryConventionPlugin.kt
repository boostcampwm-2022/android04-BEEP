import com.lighthouse.convention.ProjectConfigurations
import com.lighthouse.convention.java
import org.gradle.api.Plugin
import org.gradle.api.Project

class JavaLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("java-library")
                apply("org.jetbrains.kotlin.jvm")
            }

            java {
                sourceCompatibility = ProjectConfigurations.javaVer
                targetCompatibility = ProjectConfigurations.javaVer
            }
        }
    }
}
