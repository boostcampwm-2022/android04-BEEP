@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("beep.android.library")
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.lighthouse.data.database"
}

dependencies {
    implementation(projects.core)
    implementation(projects.coreAndroid)
    implementation(projects.model)
    implementation(projects.common)
    implementation(projects.commonAndroid)
    implementation(projects.commonLocation)
    implementation(projects.commonRecognizer)
    implementation(projects.data)

    implementation(libs.androidX.room.runtime)
    implementation(libs.androidX.room.ktx)
    implementation(libs.androidX.hilt.work)
    implementation(libs.androidX.paging.runtime)
    implementation(libs.androidX.work.runtime.ktx)
    implementation(libs.androidX.core.ktx)

    implementation(libs.kotlin.coroutine.core)
    implementation(libs.kotlin.coroutine.android)

    implementation(libs.timber)

    ksp(libs.androidX.room.compiler)
}
