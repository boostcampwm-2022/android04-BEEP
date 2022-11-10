plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    kotlin("kapt")
    id("dagger.hilt.android.plugin")
}

android {
    namespace = "com.lighthouse.presentation"
    compileSdk = AppConfig.compileSdk

    defaultConfig {
        minSdk = AppConfig.minSdk
        targetSdk = AppConfig.targetSdk

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        dataBinding = true
    }
}

dependencies {
    implementation(project(":domain"))

//    implementation(platform(Libraries.FIREBASE_BOM))

    implementation(Libraries.VIEW_LIBRARIES)
    testImplementation(TestImpl.TEST_LIBRARIES)
    kapt(Kapt.VIEW_LIBRARIES)
    androidTestImplementation(AndroidTestImpl.VIEW_LIBRARIES)
    annotationProcessor(AnnotationProcessors.VIEW_LIBRARIES)
}
kapt {
    correctErrorTypes = true
}
