package com.example.codriving.data.repository

import android.util.Log
import com.example.codriving.data.User
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuthRepository

) {

    val currentUser: FirebaseUser? = auth.getCurrentUser()
    val curretUserId: String? = currentUser?.uid


    suspend fun createUser(newUser: User, uid: String) {
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
    suspend fun getUserReferenceById(userId:String): DocumentReference {
        return firestore.collection("Users").document(userId)
    }
    suspend fun getUserById(userId: String = curretUserId?: ""): User? {
        try {
            val documentSnapshot: DocumentSnapshot =
                firestore.collection("Users").document(userId).get().await()
            if (documentSnapshot.exists()) {
                return documentSnapshot.toObject(User::class.java)
            } else {
                Log.d(TAG, "getUserById: User not found")
                return null
            }
        } catch (e: Exception) {
            Log.w(TAG, "getUserById: Error retrieving user", e)
            throw e
        }
    }
    suspend fun documentSnapshotToUser(docRef: DocumentReference): User? {
        val documentSnapshot = docRef.get().await()
        // Verifica si el documento existe
        if (documentSnapshot.exists()) {
            // Extrae los datos del DocumentSnapshot
            return documentSnapshot.toObject(User::class.java)

        } else {
            // Si el documento no existe, devuelve null
            return null
        }
    }

    fun logOut() {
        auth.signOut()
    }

    // Otras funciones relacionadas con operaciones de usuario si es necesario
    companion object {
        private const val TAG = "UserRepository"
    }
}

class UserRepositoryException(message: String, cause: Throwable?) : Exception(message, cause)
