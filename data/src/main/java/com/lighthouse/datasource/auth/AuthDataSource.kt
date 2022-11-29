package com.lighthouse.datasource.auth

interface AuthDataSource {

    fun getCurrentUserId(): String
}
