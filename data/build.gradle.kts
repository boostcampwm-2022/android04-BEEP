plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    kotlin("kapt")
}

android {
    namespace = "com.lighthouse.data"
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
}

dependencies {
    implementation(project(":domain"))

    implementation(platform(Libraries.FIREBASE_BOM))
    implementation(Libraries.DATA_LIBRARIES)
    annotationProcessor(AnnotationProcessors.DATA_LIBRARIES)
    kapt(Kapt.DATA_LIBRARIES)
    implementation(TestImpl.TEST_LIBRARIES)
    implementation(TestImpl.ANDROID_TEST_LIBRARIES)
}
kapt {
    correctErrorTypes = true
}
