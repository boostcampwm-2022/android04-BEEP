import org.gradle.api.artifacts.dsl.DependencyHandler

object Versions {
    const val APP_COMPAT = "1.5.1"
    const val CORE = "1.9.0"
    const val CORE_SPLASH = "1.0.0"
    const val CONSTRAINT_LAYOUT = "2.1.4"
    const val MATERIAL = "1.7.0"
    const val VIEWMODEL_KTX = "2.5.1"
    const val FRAGMENT_KTX = "1.5.4"
    const val COROUTINE = "1.6.4"

    const val ROOM = "2.4.3"
    const val PAGING_KTX = "3.1.1"

    const val RETROFIT = "2.9.0"
    const val MOSHI = "1.14.0"

    const val FIREBASE_BOM = "31.0.2"
    const val FIREBASE_AUTH = "21.1.0"
    const val FIREBASE_FIRESTORE_KTX = "24.4.0"
    const val FIREBASE_STORAGE_KTX = "20.1.0"
    const val TEXT_RECOGNITION_KOREAN = "16.0.0-beta6"
    const val PLAY_SERVICES_AUTH = "20.3.0"

    const val BIOMETRIC = "1.1.0"

    const val WORK_MANAGER = "2.7.1"

    const val HILT = "2.44"
    const val INJECT = "1"

    const val NAVER_MAP = "3.16.0"
    const val PLAY_SERVICES_LOCATION = "21.0.1"

    const val GLIDE = "4.14.2"

    const val JUNIT = "4.13.2"
    const val ANDROID_JUNIT = "1.1.3"
    const val ESPRESSO = "3.4.0"
    const val JUNIT5 = "5.8.2"
    const val MOCK = "1.12.0"
    const val GOOGLE_TRUTH = "1.1.3"
    const val COROUTINES_TEST = "1.6.0"
}

object Libraries {
    // androidX + KTX
    private const val CORE = "androidx.core:core-ktx:${Versions.CORE}"
    private const val CORE_SPLASH = "androidx.core:core-splashscreen:${Versions.CORE_SPLASH}"
    private const val APP_COMPAT = "androidx.appcompat:appcompat:${Versions.APP_COMPAT}"
    private const val CONSTRAINT_LAYOUT = "androidx.constraintlayout:constraintlayout:${Versions.CONSTRAINT_LAYOUT}"
    private const val MATERIAL = "com.google.android.material:material:${Versions.MATERIAL}"
    private const val VIEWMODEL_KTX = "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.VIEWMODEL_KTX}"
    private const val FRAGMENT_KTX = "androidx.fragment:fragment-ktx:${Versions.FRAGMENT_KTX}"

    private const val COROUTINE_CORE = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.COROUTINE}"
    private const val COROUTINE_ANDROID = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.COROUTINE}"

    private const val ROOM_RUNTIME = "androidx.room:room-runtime:${Versions.ROOM}"
    private const val ROOM_KTX = "androidx.room:room-ktx:${Versions.ROOM}"
    private const val ROOM_COMMON = "androidx.room:room-common:${Versions.ROOM}"

    private const val RETROFIT = "com.squareup.retrofit2:retrofit:${Versions.RETROFIT}"
    private const val MOSHI_KOTLIN = "com.squareup.moshi:moshi-kotlin:${Versions.MOSHI}"
    private const val MOSHI_ADAPTERS = "com.squareup.moshi:moshi-adapters:${Versions.MOSHI}"
    private const val CONVERTER_MOSHI = "com.squareup.retrofit2:converter-moshi:${Versions.RETROFIT}"

    private const val PAGING_COMMON_KTX = "androidx.paging:paging-common-ktx:${Versions.PAGING_KTX}"
    private const val PAGING_RUNTIME_KTX = "androidx.paging:paging-runtime:${Versions.PAGING_KTX}"

    const val FIREBASE_BOM = "com.google.firebase:firebase-bom:${Versions.FIREBASE_BOM}"

    private const val FIREBASE_AUTH_KTX = "com.google.firebase:firebase-auth-ktx:${Versions.FIREBASE_AUTH}"
    private const val PLAY_SERVICES_AUTH = "com.google.android.gms:play-services-auth:${Versions.PLAY_SERVICES_AUTH}"
    private const val FIREBASE_FIRESTORE_KTX =
        "com.google.firebase:firebase-firestore-ktx:${Versions.FIREBASE_FIRESTORE_KTX}"
    private const val FIREBASE_STORAGE_KTX = "com.google.firebase:firebase-storage-ktx:${Versions.FIREBASE_STORAGE_KTX}"
    private const val TEXT_RECOGNITION_KOREAN =
        "com.google.mlkit:text-recognition-korean:${Versions.TEXT_RECOGNITION_KOREAN}"

    private const val BIOMETRIC = "androidx.biometric:biometric:${Versions.BIOMETRIC}"

    private const val WORK_MANAGER = "androidx.work:work-runtime-ktx:${Versions.WORK_MANAGER}"

    private const val HILT = "com.google.dagger:hilt-android:${Versions.HILT}"
    private const val INJECT = "javax.inject:javax.inject:${Versions.INJECT}"

    private const val NAVER_MAP = "com.naver.maps:map-sdk:${Versions.NAVER_MAP}"
    private const val PLAY_SERVICES_LOCATION =
        "com.google.android.gms:play-services-location:${Versions.PLAY_SERVICES_LOCATION}"

    private const val GLIDE = "com.github.bumptech.glide:glide:${Versions.GLIDE}"

    val VIEW_LIBRARIES = arrayListOf(
        CORE,
        CORE_SPLASH,
        APP_COMPAT,
        CONSTRAINT_LAYOUT,
        MATERIAL,
        COROUTINE_CORE,
        COROUTINE_ANDROID,
        HILT,
        VIEWMODEL_KTX,
        FRAGMENT_KTX,
        PAGING_RUNTIME_KTX,
        FIREBASE_AUTH_KTX,
        PLAY_SERVICES_AUTH,
        TEXT_RECOGNITION_KOREAN,
        BIOMETRIC,
        NAVER_MAP,
        PLAY_SERVICES_LOCATION,
        GLIDE
    )
    val DATA_LIBRARIES = arrayListOf(
        ROOM_RUNTIME,
        ROOM_KTX,
        COROUTINE_CORE,
        RETROFIT,
        MOSHI_KOTLIN,
        MOSHI_ADAPTERS,
        CONVERTER_MOSHI,
        INJECT,
        FIREBASE_FIRESTORE_KTX,
        FIREBASE_STORAGE_KTX,
        WORK_MANAGER
    )
    val DOMAIN_LIBRARIES = arrayListOf(
        COROUTINE_CORE,
        INJECT,
        PAGING_COMMON_KTX,
        ROOM_COMMON
    )
    val APP_LIBRARIES = arrayListOf(
        HILT,
        RETROFIT,
        MOSHI_KOTLIN,
        MOSHI_ADAPTERS,
        CONVERTER_MOSHI
    )
}

object TestImpl {
    private const val JUNIT4 = "junit:junit:${Versions.JUNIT}"
    private const val PAGING_COMMON = "androidx.paging:paging-common:${Versions.PAGING_KTX}"

    private const val JUNIT_JUPITER_PARAMS = "org.junit.jupiter:junit-jupiter-params:${Versions.JUNIT5}"
    private const val JUNIT_JUPITER_ENGINE = "org.junit.jupiter:junit-jupiter-engine:${Versions.JUNIT5}"
    private const val JUNIT_VINTAGE_ENGINE = "org.junit.vintage:junit-vintage-engine:${Versions.JUNIT5}"
    private const val MOCK = "io.mockk:mockk:${Versions.MOCK}"
    private const val GOOGLE_TRUTH = "com.google.truth:truth:${Versions.GOOGLE_TRUTH}"
    private const val COROUTINES_TEST = "org.jetbrains.kotlinx:kotlinx-coroutines-test:${Versions.COROUTINES_TEST}"

    val TEST_LIBRARIES = arrayListOf(
        JUNIT4,
        PAGING_COMMON,
        JUNIT_JUPITER_PARAMS,
        JUNIT_JUPITER_ENGINE,
        JUNIT_VINTAGE_ENGINE,
        MOCK,
        GOOGLE_TRUTH,
        COROUTINES_TEST
    )
}

object AndroidTestImpl {
    private const val ANDROID_JUNIT = "androidx.test.ext:junit:${Versions.ANDROID_JUNIT}"
    private const val ESPRESSO = "androidx.test.espresso:espresso-core:${Versions.ESPRESSO}"
    private const val MOCKITO_CORE = "org.mockito:mockito-core:2.28.2"
    private const val MOCKITO_ANDROID = "org.mockito:mockito-android:2.28.2"

    private const val WORK_MANAGER = "androidx.work:work-testing:${Versions.WORK_MANAGER}"

    val VIEW_LIBRARIES = arrayListOf(
        ANDROID_JUNIT,
        ESPRESSO,
        WORK_MANAGER
    )

    val DATA_LIBRARIES = arrayListOf(
        MOCKITO_CORE,
        MOCKITO_ANDROID
    )
}

object AnnotationProcessors {
    private const val ROOM_COMPILER = "androidx.room:room-compiler:${Versions.ROOM}"
    private const val GLIDE_COMPILER = "com.github.bumptech.glide:compiler:${Versions.GLIDE}"

    val VIEW_LIBRARIES = arrayListOf(
        GLIDE_COMPILER
    )

    val DATA_LIBRARIES = arrayListOf(
        ROOM_COMPILER
    )

    val APP_LIBRARIES = arrayListOf(
        ROOM_COMPILER
    )
}

object Kapt {
    private const val HILT = "com.google.dagger:hilt-android-compiler:${Versions.HILT}"

    private const val ROOM_COMPILER = "androidx.room:room-compiler:${Versions.ROOM}"
    private const val MOSHI_KOTLIN_CODEGEN = "com.squareup.moshi:moshi-kotlin-codegen:${Versions.MOSHI}"

    val VIEW_LIBRARIES = arrayListOf(
        HILT
    )

    val DATA_LIBRARIES = arrayListOf(
        ROOM_COMPILER,
        MOSHI_KOTLIN_CODEGEN
    )

    val APP_LIBRARIES = arrayListOf(
        HILT,
        ROOM_COMPILER,
        MOSHI_KOTLIN_CODEGEN
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
