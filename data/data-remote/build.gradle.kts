import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("beep.android.library")
    id("beep.android.hilt")
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.lighthouse.data.remote"

    defaultConfig {
        val kakaoSearchId = gradleLocalProperties(rootDir).getProperty("kakao_search_id")
        buildConfigField("String", "kakaoSearchId", kakaoSearchId)
    }
}

dependencies {
    implementation(projects.core)
    implementation(projects.coreAndroid)
    implementation(projects.model)
    implementation(projects.commonAndroid)
    implementation(projects.data)

    implementation(libs.squareup.retrofit2)
    implementation(libs.squareup.retrofit2.converter.moshi)

    implementation(libs.timber)
}
