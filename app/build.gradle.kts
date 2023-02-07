@file:Suppress("UnstableApiUsage")

import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("beep.android.application")
    id("beep.android.hilt")
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.lighthouse.beep"

    defaultConfig {
        applicationId = "com.lighthouse.beep"
        versionCode = 1
        versionName = "1.0.0"

        buildConfigField("String", "kakaoSearchId", getApiKey("kakao_search_id"))
        manifestPlaceholders["naver_map_api_id"] = getApiKey("naver_map_api_id")
    }

    buildFeatures {
        dataBinding = true
    }

    packagingOptions {
        resources.excludes.add("META-INF/LICENSE*")
    }
}

dependencies {
    implementation(projects.core)
    implementation(projects.coreAndroid)
    implementation(projects.model)
    implementation(projects.common)
    implementation(projects.commonAndroid)
    implementation(projects.commonLocation)
    implementation(projects.domain)
    implementation(projects.presentation)
    implementation(projects.data)

    implementation(libs.androidX.hilt.work)
    implementation(libs.androidX.work.runtime.ktx)
    implementation(libs.androidX.room.ktx)
    implementation(libs.androidX.room.runtime)
    implementation(libs.androidX.datastore.preferences)

    implementation(libs.kotlin.coroutine.core)
    implementation(libs.kotlin.coroutine.android)

    implementation(libs.squareup.retrofit2)
    implementation(libs.squareup.retrofit2.converter.moshi)
    implementation(libs.squareup.moshi.kotlin)
    implementation(libs.squareup.moshi.adapters)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth.ktx)

    implementation(libs.gms.play.services.oss.licences)

    implementation(libs.timber)

    ksp(libs.androidX.room.compiler)
    ksp(libs.glide.ksp)
}

kapt {
    useBuildCache = true
}

fun getApiKey(propertyKey: String): String {
    return gradleLocalProperties(rootDir).getProperty(propertyKey)
}
