package com.lighthouse.repository

import com.lighthouse.datasource.auth.AuthDataSource
import com.lighthouse.domain.repository.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authDataSource: AuthDataSource
) : AuthRepository {

    override fun getCurrentUserId(): String {
        return authDataSource.getCurrentUserId()
    }
}
