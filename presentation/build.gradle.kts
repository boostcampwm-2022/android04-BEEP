@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("beep.android.library")
    id("beep.android.library.compose")
    id("beep.android.hilt")
    id("kotlin-parcelize")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.lighthouse.presentation"
}

dependencies {
    implementation(projects.domain)

    implementation(libs.androidX.core.ktx)
    implementation(libs.androidX.core.splashscreen)
    implementation(libs.androidX.appcompat)
    implementation(libs.androidX.constraintlayout)
    implementation(libs.androidX.hilt.work)
    implementation(libs.androidX.lifecycle.viewmodel.ktx)
    implementation(libs.androidX.fragment.ktx)
    implementation(libs.androidX.paging.runtime)
    implementation(libs.androidX.biometric)
    implementation(libs.androidX.viewpager2)
    implementation(libs.androidX.work.runtime.ktx)
    implementation(libs.androidX.glance.appwidget)

    implementation(libs.kotlin.coroutine.core)
    implementation(libs.kotlin.coroutine.android)
    implementation(libs.kotlin.serialization.json)

    implementation(libs.material)

    implementation(libs.accompanist.appcompat.theme)
    implementation(libs.accompanist.flowlayout)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth.ktx)
    implementation(libs.firebase.crashlytics.ndk)
    implementation(libs.firebase.crashlytics.ktx)
    implementation(libs.firebase.analytics.ktx)

    implementation(libs.gms.play.services.auth)
    implementation(libs.gms.play.services.location)
    implementation(libs.gms.play.services.oss.licences)

    implementation(libs.glide)
    implementation(libs.landscapist.glide)

    implementation(libs.naver.map.sdk)

    implementation(libs.zxing.core)

    implementation(libs.timber)

    implementation(libs.facebook.shimmer)

    implementation(libs.airbnb.lottie)

    ksp(libs.glide.ksp)

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
}

// JUnit5
// tasks.withType<Test> {
//    useJUnitPlatform()
// }
