plugins {
    id("beep.java.library")
}

dependencies {
    implementation(projects.core)
    implementation(projects.model)
    implementation(projects.common)

    implementation(libs.kotlin.coroutine.core)
    implementation(libs.androidX.paging.common.ktx)
    implementation(libs.androidX.room.common)
    implementation(libs.javax.inject)
}
