@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("beep.android.library")
    id("beep.android.hilt")
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.lighthouse.data.database"
}

dependencies {
    implementation(projects.core)
    implementation(projects.coreAndroid)
    implementation(projects.model)
    implementation(projects.commonAndroid)
    implementation(projects.data)

    implementation(libs.androidX.room.runtime)
    implementation(libs.androidX.room.ktx)
    implementation(libs.androidX.paging.runtime)
    implementation(libs.androidX.core.ktx)

    implementation(libs.kotlin.coroutine.core)

    implementation(libs.timber)

    ksp(libs.androidX.room.compiler)
}
