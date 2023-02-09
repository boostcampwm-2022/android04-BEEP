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
includeProject(":auth")
includeProject(":common")
includeProject(":common-android")
includeProject(":utils-location", "utils")
includeProject(":utils-recognizer", "utils")
includeProject(":data", "data")
includeProject(":data-database", "data")
includeProject(":data-remote", "data")
includeProject(":data-preference", "data")
includeProject(":presentation")
includeProject(":domain")
