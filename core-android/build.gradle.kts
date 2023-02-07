plugins {
    id("beep.android.library")
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
}
