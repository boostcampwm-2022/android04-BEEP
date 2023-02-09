@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("beep.android.library")
    id("beep.android.hilt")
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.lighthouse.data"
}

dependencies {
    implementation(projects.core)
    implementation(projects.coreAndroid)
    implementation(projects.model)
    implementation(projects.common)
    implementation(projects.commonAndroid)
    implementation(projects.domain)
    implementation(projects.utilsLocation)
    implementation(projects.utilsRecognizer)

    implementation(libs.androidX.paging.common.ktx)

    implementation(libs.kotlin.coroutine.core)

    implementation(libs.javax.inject)

    implementation(libs.timber)
}
