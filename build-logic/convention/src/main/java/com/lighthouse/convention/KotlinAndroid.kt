@file:Suppress("RemoveRedundantBackticks")

package com.lighthouse.convention

import com.android.build.api.dsl.CommonExtension
import com.android.build.gradle.BaseExtension
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions
import org.jetbrains.kotlin.gradle.plugin.KaptExtension

internal fun Project.configureAndroid() {
    extensions.configure<BaseExtension> {
        compileSdkVersion(ProjectConfigurations.compileSdk)

        defaultConfig {
            minSdk = ProjectConfigurations.minSdk
            targetSdk = ProjectConfigurations.targetSdk

            testInstrumentationRunner = "android.support.test.runner.AndroidJUnitRunner"
            vectorDrawables.useSupportLibrary = true
        }

        dataBinding.enable = true

        compileOptions {
            sourceCompatibility = ProjectConfigurations.javaVer
            targetCompatibility = ProjectConfigurations.javaVer
        }
    }
}

internal fun Project.configureKotlin(
    commonExtension: CommonExtension<*, *, *, *>
) {
    commonExtension.kotlinOptions {
        freeCompilerArgs = freeCompilerArgs + listOf(
            "-Xjsr305=strict"
        )
        jvmTarget = ProjectConfigurations.javaVer.toString()
    }
}

internal fun Project.`kapt`(
    configure: Action<KaptExtension>
) {
    (this as ExtensionAware).extensions.configure("kapt", configure)
}

internal fun Project.`java`(
    configure: Action<JavaPluginExtension>
) {
    (this as ExtensionAware).extensions.configure("java", configure)
}

internal fun CommonExtension<*, *, *, *>.kotlinOptions(
    block: KotlinJvmOptions.() -> Unit
) {
    (this as ExtensionAware).extensions.configure("kotlinOptions", block)
}
