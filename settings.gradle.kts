@file:Suppress("UnstableApiUsage")

pluginManagement {
    includeBuild("build-logic")
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://naver.jfrog.io/artifactory/maven/")
    }
}
rootProject.name = "BEEP"

fun includeProject(moduleName: String, rootFolderName: String = "") {
    settings.include(moduleName)

    if (rootFolderName.isNotEmpty()) {
        project(moduleName).projectDir =
            File(rootDir, "$rootFolderName/${moduleName.substring(startIndex = 1)}")
    }
}

includeProject(":app")
includeProject(":core")
includeProject(":core-android")
includeProject(":model")
includeProject(":common", "commons")
includeProject(":common-auth", "commons")
includeProject(":common-android", "commons")
includeProject(":common-location", "commons")
includeProject(":common-recognizer", "commons")
includeProject(":data", "data")
includeProject(":data-database", "data")
includeProject(":data-remote", "data")
includeProject(":data-content", "data")
includeProject(":data-preference", "data")
includeProject(":presentation")
includeProject(":domain")
