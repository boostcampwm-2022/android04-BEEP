import com.lighthouse.convention.findVersionCatalog
import com.lighthouse.convention.implementation
import com.lighthouse.convention.kapt
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class AndroidHiltConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("kotlin-kapt")
                apply("dagger.hilt.android.plugin")
            }

            val libs = findVersionCatalog()

            dependencies {
                implementation(libs.findLibrary("dagger-hilt-android"))
                kapt(libs.findLibrary("dagger-hilt-android-compiler"))
            }

            kapt {
                correctErrorTypes = true
            }
        }
    }
}
