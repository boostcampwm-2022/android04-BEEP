plugins {
    id("java-library")
    id("org.jetbrains.kotlin.jvm")
    kotlin("kapt")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
    implementation(Libraries.DOMAIN_LIBRARIES)
    testImplementation(TestImpl.TEST_LIBRARIES)
}
kapt {
    correctErrorTypes = true
}
