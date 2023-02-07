@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("beep.android.library")
    id("beep.android.hilt")
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.lighthouse.beep.common.android"
}

dependencies {
    implementation(projects.core)
    implementation(projects.coreAndroid)
    implementation(projects.model)

    implementation(libs.squareup.moshi.kotlin)
    implementation(libs.squareup.moshi.adapters)

    ksp(libs.squareup.moshi.kotlin.codegen)
}
