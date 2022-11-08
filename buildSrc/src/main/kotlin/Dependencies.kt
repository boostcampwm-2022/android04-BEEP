import org.gradle.api.artifacts.dsl.DependencyHandler

object Versions {
    const val APP_COMPAT = "1.5.1"
    const val CORE = "1.7.0"
    const val CONSTRAINT_LAYOUT = "2.1.4"
    const val NAVIGATION_FRAGMENT = "2.5.3"
    const val JUNIT = "4.13.2"
    const val ANDROID_JUNIT = "1.1.3"
    const val ESPRESSO = "3.4.0"
    const val MATERIAL = "1.7.0"
}

object Libraries {
    // androidX + KTX
    private const val CORE = "androidx.core:core-ktx:${Versions.CORE}"
    private const val APP_COMPAT = "androidx.appcompat:appcompat:${Versions.APP_COMPAT}"
    private const val CONSTRAINT_LAYOUT = "androidx.constraintlayout:constraintlayout:${Versions.CONSTRAINT_LAYOUT}"
    private const val NAVIGATION_FRAGMENT_KTX =
        "androidx.navigation:navigation-fragment-ktx:${Versions.NAVIGATION_FRAGMENT}"
    private const val NAVIGATION_UI_KTX = "androidx.navigation:navigation-ui-ktx:${Versions.NAVIGATION_FRAGMENT}"
    private const val MATERIAL = "com.google.android.material:material:${Versions.MATERIAL}"

    val VIEW_LIBRARIES = arrayListOf<String>().apply {
        add(CORE)
        add(APP_COMPAT)
        add(CONSTRAINT_LAYOUT)
        add(NAVIGATION_FRAGMENT_KTX)
        add(NAVIGATION_UI_KTX)
        add(MATERIAL)
    }
}

object TestImpl {
    private const val JUNIT4 = "junit:junit:${Versions.JUNIT}" // TODO 5 쓰는 쪽으로 바꿔야함

    val TEST_LIBRARIES = arrayListOf<String>().apply {
        add(JUNIT4)
    }
}

object AndroidTestImpl {
    private const val ANDROID_JUNIT = "androidx.test.ext:junit:${Versions.ANDROID_JUNIT}"
    private const val ESPRESSO = "androidx.test.espresso:espresso-core:${Versions.ESPRESSO}"

    val ANDROID_LIBRARIES = arrayListOf<String>().apply {
        add(ANDROID_JUNIT)
        add(ESPRESSO)
    }
}

fun DependencyHandler.kapt(list: List<String>) {
    list.forEach { dependency ->
        add("kapt", dependency)
    }
}

fun DependencyHandler.implementation(list: List<String>) {
    list.forEach { dependency ->
        add("implementation", dependency)
    }
}

fun DependencyHandler.androidTestImplementation(list: List<String>) {
    list.forEach { dependency ->
        add("androidTestImplementation", dependency)
    }
}

fun DependencyHandler.testImplementation(list: List<String>) {
    list.forEach { dependency ->
        add("testImplementation", dependency)
    }
}
