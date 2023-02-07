plugins {
    id("beep.android.library")
    id("beep.android.hilt")
}

android {
    namespace = "com.lighthouse.beep.common.recognizer"
}

dependencies {
    implementation(projects.core)
    implementation(projects.coreAndroid)
    implementation(projects.model)

    implementation(platform(libs.firebase.bom))
    implementation(libs.mlkit.text.recognition.korean)
}
