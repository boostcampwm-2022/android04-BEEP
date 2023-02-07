plugins {
    id("beep.android.library")
    id("beep.android.hilt")
}

android {
    namespace = "com.lighthouse.beep.common.location"
}

dependencies {
    implementation(projects.core)
    implementation(projects.coreAndroid)
    implementation(projects.model)

    implementation(libs.gms.play.services.location)

//    implementation(libs.androidX.activity.ktx)
//    implementation(libs.androidX.appcompat)
//    implementation(libs.androidX.core.ktx)
//    implementation(libs.androidX.fragment.ktx)
//    implementation(libs.zxing.core)
}
