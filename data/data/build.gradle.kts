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

//    implementation(libs.androidX.room.runtime)
//    implementation(libs.androidX.room.ktx)
//    implementation(libs.androidX.hilt.work)
//    implementation(libs.androidX.paging.runtime)
//    implementation(libs.androidX.work.runtime.ktx)
//    implementation(libs.androidX.datastore.preferences)
//    implementation(libs.androidX.core.ktx)

    implementation(libs.androidX.paging.common.ktx)

    implementation(libs.kotlin.coroutine.core)

    implementation(libs.javax.inject)

//    implementation(libs.squareup.retrofit2)
//    implementation(libs.squareup.retrofit2.converter.moshi)
//    implementation(libs.squareup.moshi.kotlin)
//    implementation(libs.squareup.moshi.adapters)
//
//    implementation(platform(libs.firebase.bom))
//    implementation(libs.firebase.auth.ktx)
//    implementation(libs.firebase.firestore.ktx)
//    implementation(libs.firebase.storage.ktx)
//    implementation(libs.mlkit.text.recognition.korean)

    implementation(libs.timber)

//    ksp(libs.androidX.room.compiler)
//    ksp(libs.squareup.moshi.kotlin.codegen)
}
