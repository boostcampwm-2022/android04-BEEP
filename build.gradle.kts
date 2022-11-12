plugins {
    id("com.android.application") version "7.3.1" apply false
    id("com.android.library") version "7.3.1" apply false
    id("org.jetbrains.kotlin.android") version "1.7.20" apply false
    id("org.jetbrains.kotlin.jvm") version "1.7.20" apply false
    id("com.google.dagger.hilt.android") version "2.44" apply false
}

buildscript {
    dependencies {
        classpath("com.google.gms:google-services:4.3.14")
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
