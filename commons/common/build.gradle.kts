plugins {
    id("beep.java.library")
}

dependencies {
    implementation(projects.core)
    implementation(projects.model)

//    implementation(libs.androidX.activity.ktx)
//    implementation(libs.androidX.appcompat)
//    implementation(libs.androidX.core.ktx)
//    implementation(libs.androidX.fragment.ktx)
//    implementation(libs.zxing.core)
}
