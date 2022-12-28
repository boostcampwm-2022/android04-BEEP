import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import java.io.FileInputStream
import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    kotlin("kapt")
    id("dagger.hilt.android.plugin")
    id("com.google.android.gms.oss-licenses-plugin")
}

android {
    val keystorePropertiesFile = rootProject.file("app/keystore/keystore.properties")

    signingConfigs {
        create("release") {
            val keystoreProperties = Properties()
            keystoreProperties.load(FileInputStream(keystorePropertiesFile))

            keyAlias = keystoreProperties["keyAlias"] as String
            keyPassword = keystoreProperties["keyPassword"] as String
            storeFile = file(keystoreProperties["storeFile"]!!)
            storePassword = keystoreProperties["storePassword"] as String
        }
    }

    namespace = "com.lighthouse.beep"
    compileSdk = AppConfig.compileSdk
    buildToolsVersion = AppConfig.buildToolsVersion

    defaultConfig {
        applicationId = "com.lighthouse.beep"
        minSdk = AppConfig.minSdk
        targetSdk = AppConfig.targetSdk
        versionCode = AppConfig.versionCode
        versionName = AppConfig.versionName

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "kakaoSearchId", getApiKey("kakao_search_id"))
        manifestPlaceholders["naver_map_api_id"] = getApiKey("naver_map_api_id")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            setProguardFiles(
                listOf(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    "proguard-rules.pro"
                )
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = AppConfig.jvmTarget
    }
    buildFeatures {
        dataBinding = true
    }

    packagingOptions {
        resources.excludes.add("META-INF/LICENSE*")
    }
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":presentation"))
    implementation(project(":data"))
    implementation(platform(Libraries.FIREBASE_BOM))
    kapt(Kapt.APP_LIBRARIES)
    implementation(Libraries.APP_LIBRARIES)
    annotationProcessor(AnnotationProcessors.APP_LIBRARIES)
}

fun getApiKey(propertyKey: String): String {
    return gradleLocalProperties(rootDir).getProperty(propertyKey)
}
