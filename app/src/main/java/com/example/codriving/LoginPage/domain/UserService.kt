package com.example.codriving.LoginPage.domain

import com.example.codriving.LoginPage.data.FirebaseClient
import javax.inject.Inject

class UserService @Inject constructor(private val firebase: FirebaseClient) {

    companion object{
        const val USER_COLLECTION = "users"
    }
    suspend fun createUserTable(signInResult: SignInResult) = runCatching {}

}