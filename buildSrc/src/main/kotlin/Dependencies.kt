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
    const val ROOM = "2.4.3"
}

object Libraries {
    // androidX + KTX
    private const val CORE = "androidx.core:core-ktx:${Versions.CORE}"
    private const val APP_COMPAT = "androidx.appcompat:appcompat:${Versions.APP_COMPAT}"
    private const val CONSTRAINT_LAYOUT = "androidx.constraintlayout:constraintlayout:${Versions.CONSTRAINT_LAYOUT}"
    private const val NAVIGATION_FRAGMENT_KTX =
        "androidx.navigation:navigation-fragment-ktx:${Versions.NAVIGATION_FRAGMENT}"
    private const val NAVIGATION_UI_KTX = "androidx.navigation:navigation-ui-ktx:${Versions.NAVIGATION_FRAGMENT}"
    private const val ROOM_RUNTIME = "androidx.room:room-runtime:${Versions.ROOM}"
    private const val ROOM_KTX = "androidx.room:room-ktx:${Versions.ROOM}"
    private const val MATERIAL = "com.google.android.material:material:${Versions.MATERIAL}"

    val VIEW_LIBRARIES = arrayListOf(
        CORE,
        APP_COMPAT,
        CONSTRAINT_LAYOUT,
        NAVIGATION_FRAGMENT_KTX,
        NAVIGATION_UI_KTX,
        MATERIAL
    )
    val DATA_LIBRARIES = arrayListOf(
        ROOM_RUNTIME,
        ROOM_KTX
    )
}

object TestImpl {
    private const val JUNIT4 = "junit:junit:${Versions.JUNIT}" // TODO 5 쓰는 쪽으로 바꿔야함

    val TEST_LIBRARIES = arrayListOf(
        JUNIT4
    )
}

object AndroidTestImpl {
    private const val ANDROID_JUNIT = "androidx.test.ext:junit:${Versions.ANDROID_JUNIT}"
    private const val ESPRESSO = "androidx.test.espresso:espresso-core:${Versions.ESPRESSO}"

    val ANDROID_LIBRARIES = arrayListOf(
        ANDROID_JUNIT,
        ESPRESSO
    )
}

object AnnotationProcessors {
    private const val ROOM_COMPILER = "androidx.room:room-compiler:${Versions.ROOM}"

    val DATA_LIBRARIES = arrayListOf(
        ROOM_COMPILER
    )
}

object Kapt {
    private const val ROOM_COMPILER = "androidx.room:room-compiler:${Versions.ROOM}"

    val DATA_LIBRARIES = arrayListOf(
        ROOM_COMPILER
    )
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

fun DependencyHandler.annotationProcessor(list: List<String>) {
    list.forEach { dependency ->
        add("annotationProcessor", dependency)
    }
}

fun DependencyHandler.testImplementation(list: List<String>) {
    list.forEach { dependency ->
        add("testImplementation", dependency)
    }
}
