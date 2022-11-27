package com.lighthouse.presentation.ui.security

interface AuthCallback {
    fun onAuthSuccess()
    fun onAuthFailure()
}
