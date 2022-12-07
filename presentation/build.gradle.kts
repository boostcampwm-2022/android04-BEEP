import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.android.library")
    id("kotlin-parcelize")
    id("org.jetbrains.kotlin.android")
    id("dagger.hilt.android.plugin")
    id("com.google.gms.google-services")
    kotlin("kapt")
    kotlin("plugin.serialization") version "1.5.0"
}

android {
    namespace = "com.lighthouse.presentation"
    compileSdk = AppConfig.compileSdk

    defaultConfig {
        minSdk = AppConfig.minSdk
        targetSdk = AppConfig.targetSdk

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        testInstrumentationRunnerArguments["runnerBuilder"] = "de.mannodermaus.junit5.AndroidJUnit5Builder"
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
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = Versions.KOTLIN_COMPILER_EXTENSION
    }

    testOptions {
        unitTests {
            isReturnDefaultValues = true
            isIncludeAndroidResources = true
        }
    }
}

dependencies {
    implementation(project(":domain"))

    val composeBom = platform("androidx.compose:compose-bom:${Versions.COMPOSE_BOM}")
    implementation(composeBom)

    implementation(Libraries.VIEW_LIBRARIES)
    testImplementation(TestImpl.TEST_LIBRARIES)
    kapt(Kapt.VIEW_LIBRARIES)
    debugImplementation(DebugImpl.VIEW_LIBRARIES)
    androidTestImplementation(AndroidTestImpl.VIEW_LIBRARIES)
    annotationProcessor(AnnotationProcessors.VIEW_LIBRARIES)
}

kapt {
    correctErrorTypes = true
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "1.8"
    }
}

// JUnit5
tasks.withType<Test> {
    useJUnitPlatform()
}
