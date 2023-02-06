@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("beep.android.library")
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.lighthouse.data"
}

dependencies {
    implementation(projects.core)
    implementation(projects.domain)

    implementation(libs.androidX.room.runtime)
    implementation(libs.androidX.room.ktx)
    implementation(libs.androidX.hilt.work)
    implementation(libs.androidX.paging.runtime)
    implementation(libs.androidX.work.runtime.ktx)
    implementation(libs.androidX.datastore.preferences)
    implementation(libs.androidX.core.ktx)

    implementation(libs.kotlin.coroutine.core)

    implementation(libs.squareup.retrofit2)
    implementation(libs.squareup.retrofit2.converter.moshi)
    implementation(libs.squareup.moshi.kotlin)
    implementation(libs.squareup.moshi.adapters)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth.ktx)
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.firebase.storage.ktx)
    implementation(libs.mlkit.text.recognition.korean)

    implementation(libs.gms.play.services.location)

    implementation(libs.timber)

    ksp(libs.androidX.room.compiler)
    ksp(libs.squareup.moshi.kotlin.codegen)

    testImplementation(libs.junit4)
    testImplementation(libs.junit5.jupiter.params)
    testImplementation(libs.junit5.jupiter.engine)
    testImplementation(libs.junit5.vintage.engine)
    testImplementation(libs.mockk)
    testImplementation(libs.google.truth)
    testImplementation(libs.kotlin.coroutine.test)
    testImplementation(libs.robolectric)
    testImplementation(libs.turbine)

    androidTestImplementation(libs.test.core)
    androidTestImplementation(libs.mockito.core)
    androidTestImplementation(libs.mockito.android)
}
