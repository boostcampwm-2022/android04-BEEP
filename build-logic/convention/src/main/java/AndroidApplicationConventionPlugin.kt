@file:Suppress("UnstableApiUsage")

import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import com.lighthouse.convention.configureAndroid
import com.lighthouse.convention.configureKotlin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.application")
                apply("org.jetbrains.kotlin.android")
            }

            configureAndroid()

            extensions.configure<BaseAppModuleExtension> {
                configureKotlin(this)

                buildTypes {
                    release {
                        isMinifyEnabled = false
                        proguardFile("proguard-rules.pro")
                    }
                }
            }
        }
    }
}
