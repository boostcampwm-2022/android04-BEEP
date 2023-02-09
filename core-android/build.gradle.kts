@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("beep.android.library")
    id("beep.android.hilt")
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.lighthouse.beep.core"
}

dependencies {
    implementation(libs.androidX.activity.ktx)
    implementation(libs.androidX.appcompat)
    implementation(libs.androidX.core.ktx)
    implementation(libs.androidX.fragment.ktx)
    implementation(libs.zxing.core)

    implementation(libs.squareup.moshi.kotlin)
    implementation(libs.squareup.moshi.adapters)

    ksp(libs.squareup.moshi.kotlin.codegen)
}
