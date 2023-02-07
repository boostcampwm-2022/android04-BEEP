plugins {
    id("beep.android.library")
    id("beep.android.hilt")
}

android {
    namespace = "com.lighthouse.beep.common"
}

dependencies {
    implementation(projects.core)
    implementation(projects.coreAndroid)
    implementation(projects.model)

//    implementation(libs.androidX.activity.ktx)
//    implementation(libs.androidX.appcompat)
//    implementation(libs.androidX.core.ktx)
//    implementation(libs.androidX.fragment.ktx)
//    implementation(libs.zxing.core)
}
