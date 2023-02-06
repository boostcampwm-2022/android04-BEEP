plugins {
    id("beep.java.library")
}

dependencies {
    implementation(libs.kotlin.coroutine.core)
    implementation(libs.androidX.paging.common.ktx)
    implementation(libs.androidX.room.common)
    implementation(libs.javax.inject)

    testImplementation(libs.junit4)
    testImplementation(libs.junit5.jupiter.params)
    testImplementation(libs.junit5.jupiter.engine)
    testImplementation(libs.junit5.vintage.engine)
    testImplementation(libs.mockk)
    testImplementation(libs.google.truth)
    testImplementation(libs.kotlin.coroutine.test)
    testImplementation(libs.robolectric)
    testImplementation(libs.turbine)
}
