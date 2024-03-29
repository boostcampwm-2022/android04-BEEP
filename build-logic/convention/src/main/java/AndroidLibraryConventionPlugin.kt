import com.android.build.gradle.LibraryExtension
import com.lighthouse.convention.configureAndroid
import com.lighthouse.convention.configureKotlin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.library")
                apply("org.jetbrains.kotlin.android")
            }

            configureAndroid()

            extensions.configure<LibraryExtension> {
                configureKotlin(this)
            }
        }
    }
}
