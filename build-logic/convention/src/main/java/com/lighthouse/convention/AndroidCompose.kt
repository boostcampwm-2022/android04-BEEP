@file:Suppress("UnstableApiUsage") // ktlint-disable filename

package com.lighthouse.convention

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

internal fun Project.configureAndroidCompose(
    commonExtension: CommonExtension<*, *, *, *>
) {
    val libs = findVersionCatalog()

    commonExtension.apply {
        buildFeatures.compose = true

        composeOptions {
            kotlinCompilerExtensionVersion = libs.findVersion("compose-compiler").get().toString()
        }
    }

    dependencies {
        api(platform(libs.findLibrary("androidX-compose-bom").get()))
        implementation(libs.findBundle("androidX-compose"))
        implementation(libs.findBundle("androidX-compose-lifecycle"))
        debugImplementation(libs.findBundle("androidX-compose-debug"))
    }
}
