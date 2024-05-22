package com.example.codriving.data.repository

import android.util.Log
import com.example.codriving.data.model.Notification
import com.example.codriving.data.model.User
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.Date
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val firestore: FirebaseFirestore,

    ) {

    val auth = FirebaseAuth.getInstance()


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


    fun getUserReferenceById(userId: String): DocumentReference {
        return firestore.collection("Users").document(userId)
    }

    suspend fun getUserById(userId: String): User? {
        return try {
            val documentSnapshot: DocumentSnapshot =
                firestore.collection("Users").document(userId).get().await()
            if (documentSnapshot.exists()) {
                documentSnapshot.toObject(User::class.java)
            } else {
                Log.d(TAG, "getUserById: User not found")
                null
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

    fun productNotify(
        message: String,
        productId: String,
        rentsCar: MutableSet<DocumentReference>,
        type: Int? = 1,
        idReceiver: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        val notificationCarCollection = firestore.collection("carNotifications")
        val notificationId = notificationCarCollection.document().id

        val notifyProductInfo = Notification(
            idNotification = notificationId,
            idProduct = productId,
            idReceiver = idReceiver,
            idSender = auth.currentUser!!.uid,
            rentsCars = rentsCar.toList(),
            title = "Tiene una nueva solicitud",
            message = message,
            timestamp = Timestamp(Date()),
            type = type ?: 1
        )
        notificationCarCollection
            .add(notifyProductInfo)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { e ->
                onFailure(e.message ?: "Error desconocido")
            }

    }

    fun logOut() {
        auth.signOut()
    }

    suspend fun getNotifications(): List<Notification> {
        // Fetch notifications from Firestore
        val notificationCollection = firestore.collection("carNotifications")
        val notificationDocs =
            notificationCollection.whereEqualTo("idReceiver", auth.currentUser!!.uid).get().await()
        return notificationDocs.map { document ->
            document.toObject(Notification::class.java)
        }
    }


    fun removeNotifyType1(
        it: Notification,
        cause: String?,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        try {
            val collectionRef = FirebaseFirestore.getInstance().collection("carNotifications")

            val query = collectionRef.whereEqualTo(
                "idNotification",
                it.idNotification
            )
            query.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Itera sobre los resultados (debería ser solo uno)
                    for (document in task.result!!) {
                        // Borra el documento utilizando la referencia
                        document.reference.delete()
                            .addOnSuccessListener {
                                // Éxito al borrar el documento
                                Log.d(TAG, "DocumentSnapshot successfully deleted!")
                            }
                            .addOnFailureListener { e ->
                                // Error al borrar el documento
                                Log.w(TAG, "Error deleting document", e)
                            }
                    }
                } else {
                    // Error al ejecutar la consulta
                    Log.d(TAG, "Error getting documents: ", task.exception)
                }
            }

        } catch (e: Exception) {
            Log.e(TAG, "Error removing notification: ${e.message}", e)
            throw UserRepositoryException("Error removing notification: ${e.message}", e)
        }

        //Comprobamos si era una solicitud y enviamos un mensaje de respuesta
        if (it.type == 1) {
            val auxNotify = it
            auxNotify.type = 2
            productNotify(
                cause!!,
                auxNotify.idProduct!!,
                auxNotify.rentsCars.toMutableSet(),
                auxNotify.type,
                auxNotify.idSender,
                onSuccess = onSuccess,
                onFailure = onFailure
            )
        }
    }

    fun removeNotifyType2(it: Notification) {
        try {
            val collectionRef = FirebaseFirestore.getInstance().collection("carNotifications")

            val query = collectionRef.whereEqualTo(
                "idNotification",
                it.idNotification
            )
            query.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Itera sobre los resultados (debería ser solo uno)
                    for (document in task.result!!) {
                        // Borra el documento utilizando la referencia
                        document.reference.delete()
                            .addOnSuccessListener {
                                // Éxito al borrar el documento
                                Log.d(TAG, "DocumentSnapshot successfully deleted!")
                            }
                            .addOnFailureListener { e ->
                                // Error al borrar el documento
                                Log.w(TAG, "Error deleting document", e)
                            }
                    }
                } else {
                    // Error al ejecutar la consulta
                    Log.d(TAG, "Error getting documents: ", task.exception)
                }
            }

        } catch (e: Exception) {
            Log.e(TAG, "Error removing notification: ${e.message}", e)
            throw UserRepositoryException("Error removing notification: ${e.message}", e)
        }
    }

    // Otras funciones relacionadas con operaciones de usuario si es necesario
    companion object {
        private const val TAG = "UserRepository"
    }
}

class UserRepositoryException(message: String, cause: Throwable?) : Exception(message, cause)
