@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("beep.android.library")
    id("beep.android.hilt")
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.lighthouse.data.preference"
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

    implementation(libs.androidX.datastore.preferences)
    implementation(libs.javax.inject)
}
