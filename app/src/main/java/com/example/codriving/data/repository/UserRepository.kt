package com.example.codriving.data.repository

import android.util.Log
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

    suspend fun getName(uid: String): String {
        val db = FirebaseFirestore.getInstance()
        val userDocument = db.collection("Users").document(uid)

        var fullName = ""

        userDocument.get().addOnSuccessListener { document ->
            if (document.exists()) {
                fullName = document.getString("fullName").toString()
            } else {
                Log.e(TAG, "El usuario no existe")
            }
        }


        return fullName

    }


    // Otras funciones relacionadas con operaciones de usuario si es necesario
    companion object {
        private const val TAG = "UserRepository"
    }
}

class UserRepositoryException(message: String, cause: Throwable?) : Exception(message, cause)
