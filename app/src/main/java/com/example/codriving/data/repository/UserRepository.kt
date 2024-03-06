package com.example.codriving.data.repository

import com.example.codriving.data.NewUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserRepository @Inject constructor(private val firestore: FirebaseFirestore) {

    suspend fun createUser(newUser: NewUser, uid: String) {
        try {
            firestore.collection("Users")
                .document(uid)
                .set(newUser)
                .await()
            // Operación exitosa
        } catch (e: Exception) {
            throw UserRepositoryException("Error al crear el usuario: ${e.message}", e)
        }
    }



    // Otras funciones relacionadas con operaciones de usuario si es necesario
    companion object {
        private const val TAG = "UserRepository"
    }
}

class UserRepositoryException(message: String, cause: Throwable?) : Exception(message, cause)
