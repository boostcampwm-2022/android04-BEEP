plugins {
    id("beep.java.library")
}

dependencies {
    implementation(projects.core)
    implementation(projects.model)

    implementation(libs.kotlin.coroutine.core)
    implementation(libs.androidX.paging.common.ktx)
    implementation(libs.javax.inject)
}
