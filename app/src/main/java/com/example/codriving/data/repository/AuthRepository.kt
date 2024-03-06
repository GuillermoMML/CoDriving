package com.example.codriving.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


class FirebaseAuthRepository @Inject constructor(private val auth: FirebaseAuth) {

    suspend fun signIn(email: String, password: String): Boolean {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            true
        } catch (e: Exception) {
            Log.e("FireBaseRepository", "Error al iniciar sesión: ${e.message}", e)
            false
        }
    }

    suspend fun signUp(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password).await()

    }

    fun signOut() {
        auth.signOut()
    }

    fun getCurrentUser() = auth.currentUser

    companion object {
        private const val TAG = "FirebaseAuthRepository"
    }
}
