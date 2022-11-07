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
    const val CORE = "androidx.core:core-ktx:${Versions.CORE}"
    const val APP_COMPAT = "androidx.appcompat:appcompat:${Versions.APP_COMPAT}"
    const val CONSTRAINT_LAYOUT = "androidx.constraintlayout:constraintlayout:${Versions.CONSTRAINT_LAYOUT}"
    const val NAVIGATION_FRAGMENT_KTX = "androidx.navigation:navigation-fragment-ktx:${Versions.NAVIGATION_FRAGMENT}"
    const val NAVIGATION_UI_KTX = "androidx.navigation:navigation-ui-ktx:${Versions.NAVIGATION_FRAGMENT}"
    const val MATERIAL = "com.google.android.material:material:${Versions.MATERIAL}"
}

object TestImpl {
    const val JUNIT4 = "junit:junit:${Versions.JUNIT}" // TODO 5 쓰는 쪽으로 바꿔야함
}

object AndroidTestImpl {
    const val ANDROID_JUNIT = "androidx.test.ext:junit:${Versions.ANDROID_JUNIT}"
    const val ESPRESSO = "androidx.test.espresso:espresso-core:${Versions.ESPRESSO}"
}
