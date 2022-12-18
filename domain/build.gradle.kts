plugins {
    id("java-library")
    id("org.jetbrains.kotlin.jvm")
    kotlin("kapt")
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

dependencies {
    implementation(Libraries.DOMAIN_LIBRARIES)
    testImplementation(TestImpl.TEST_LIBRARIES)
}
kapt {
    correctErrorTypes = true
}
