package com.lighthouse.datasource.auth

import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

class AuthDataSourceImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : AuthDataSource {

    override fun getCurrentUserId(): String {
        return firebaseAuth.currentUser?.uid ?: GUEST_ID
    }

    companion object {
        private const val GUEST_ID = "Guest"
    }
}
