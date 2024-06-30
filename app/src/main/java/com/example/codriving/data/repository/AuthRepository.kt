package com.example.codriving.data.repository

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


class FirebaseAuthRepository @Inject constructor(private val auth: FirebaseAuth) {

    suspend fun signUp(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password).await()

    }


    fun getCurrentUser() = auth.currentUser


}
