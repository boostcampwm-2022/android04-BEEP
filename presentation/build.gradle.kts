plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
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

    implementation(Libraries.CORE)
    implementation(Libraries.APP_COMPAT)
    implementation(Libraries.MATERIAL)
    implementation(Libraries.CONSTRAINT_LAYOUT)
    implementation(Libraries.NAVIGATION_FRAGMENT_KTX)
    implementation(Libraries.NAVIGATION_UI_KTX)
    testImplementation(TestImpl.JUNIT4)
    androidTestImplementation(AndroidTestImpl.ANDROID_JUNIT)
    androidTestImplementation(AndroidTestImpl.ESPRESSO)
}
