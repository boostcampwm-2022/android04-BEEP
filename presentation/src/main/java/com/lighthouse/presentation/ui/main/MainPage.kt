package com.lighthouse.presentation.ui.main

sealed class MainPage {

    object Home : MainPage()
    object List : MainPage()
    object Setting : MainPage()
    object Other : MainPage()
}
