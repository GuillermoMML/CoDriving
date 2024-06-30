package com.example.codriving.data.repository

import android.util.Log
import com.example.codriving.data.model.Contracts
import com.example.codriving.data.model.Conversations
import com.example.codriving.data.model.Message
import com.example.codriving.data.model.Notification
import com.example.codriving.data.model.RequestContracts
import com.example.codriving.data.model.User
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import java.io.FileNotFoundException
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
            type = type ?: 2
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

    suspend fun removeNotify(notify: Notification) {
        val collectionRef = FirebaseFirestore.getInstance().collection("carNotifications")

        val query = collectionRef.whereEqualTo(
            "idNotification",
            notify.idNotification.toString()
        )
        try {
            val task = query.get().await()
            for (document in task.documents) {
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
        } catch (e: Exception) {
            Log.e(TAG, "Error removing notification: ${e.message}", e)
            throw UserRepositoryException("Error removing notification: ${e.message}", e)
        }
    }

    suspend fun removeNotifyType1(
        it: Notification,
        cause: String?,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        removeNotify(it)
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


    // Otras funciones relacionadas con operaciones de usuario si es necesario

    suspend fun acceptNotify(
        removeNotify: Notification,
        message: String,
        productId: String,
        rentsCar: MutableSet<DocumentReference>,
        idReceiver: String,
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
            type = 3
        )
        notificationCarCollection
            .add(notifyProductInfo)

        removeNotify(notify = removeNotify)
    }

    fun modifyUser(
        fullName: String,
        email: String,
        phone: String,
        location: String,
        profileImage: String,
        password: String,
        onSuccess: (String) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val userUpdates = hashMapOf<String, Any>(
            "fullName" to fullName,
            "email" to email,
            "phone" to phone,
            "location" to location,
            "imageProfile" to profileImage
        )
        auth.uid?.let {
            firestore.collection("Users").document(it).update(
                userUpdates
            )
                .addOnSuccessListener {
                    if (password.isNotEmpty()) {
                        updatePassword(
                            password,
                            onFailure = { onFailure(it) })

                    } else {
                        onSuccess("User updated successfully")
                    }
                }
                .addOnFailureListener {
                    onFailure(it)
                }
        }
    }

    fun updatePassword(newPassword: String, onFailure: (Exception) -> Unit) {
        val user = auth.currentUser
        user?.let {
            if (newPassword.length >= 6) {
                it.updatePassword(newPassword)
                    .addOnCompleteListener { task ->
                        if (!task.isSuccessful) {
                            task.exception?.let { exception ->
                                onFailure(exception)
                            }
                        }
                    }
            } else {
                onFailure(Exception("Password must be at least 6 characters long"))
            }
        } ?: run {
            onFailure(Exception("No authenticated user found"))
        }
    }

    fun generateContract(lastEndDate: Timestamp, notification: Notification) {
        val contract = Contracts(
            idContracts = notification.idNotification!!,
            idProduct = notification.idProduct!!,
            idReceiver = notification.idReceiver!!,
            idSender = notification.idSender,
            timestamp = lastEndDate,
            rentCars = notification.rentsCars
        )
        firestore.collection("Users").document(notification.idSender).collection("contracts")
            .add(contract)
        firestore.collection("Users").document(notification.idReceiver).collection("contracts")
            .add(contract)

    }

    suspend fun getContractsByUser(
        auth: String,
        onSuccess: (List<Contracts>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val contracts = mutableListOf<Contracts>()  // Use a mutable list

        try {
            val docRef =
                firestore.collection("Users").document(auth).collection("contracts").get().await()

            docRef.forEach {
                if (it.exists()) {
                    contracts.add(it.toObject(Contracts::class.java))
                }
            }
            if (contracts.isNotEmpty()) {
                onSuccess(contracts.toList())

            } else {
                onFailure(throw FileNotFoundException("No se encontro ningun contrato"))
            }
        } catch (e: Exception) {
            onFailure(
                 e
            )
        }

    }


    fun addReviewService(
        contract: RequestContracts,
        review: String,
        rating: Double,
        onComplete: (String) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val newReview = hashMapOf(
            "reviewSender" to auth.uid.toString(),
            "comment" to review,
            "rating" to rating
        )
        var resultMessage = "Comment Added"
        var newRating: Double
        var numberOfRating: Int
        val carRef = contract.rentCars[0].carId

        carRef.collection("reviews").document(newReview["reviewSender"].toString()).get()
            .addOnSuccessListener {
                if (it.exists()) {
                    resultMessage = "You have already commented it"
                    onComplete(resultMessage)
                } else {
                    carRef.get()
                        .addOnSuccessListener {
                            numberOfRating = it.getLong("numberOfReviews")!!.toInt() + 1
                            newRating = (rating + it.getLong("rating")!!) / numberOfRating
                            carRef.update("rating", newRating)
                            carRef.update("numberOfReviews", numberOfRating)
                                .addOnSuccessListener {
                                    onComplete(resultMessage)
                                    carRef.collection("reviews")
                                        .document(newReview["reviewSender"].toString()).set(
                                            newReview
                                        )
                                }
                                .addOnFailureListener { e ->
                                    resultMessage = e.message.toString()
                                    onFailure(e)
                                }


                        }
                        .addOnFailureListener {
                            onFailure(it)
                        }
                }
            }
            .addOnFailureListener { e ->
                resultMessage = e.message.toString()
                onFailure(e)
            }
    }

    suspend fun addConversation(conversation: List<String>) {
        if (conversation[0] == auth.uid && conversation[1] == auth.uid) {
            return
        }
        val sortedConversation = conversation.sorted()


        val existingConversations = Firebase.firestore.collection("conversations")
            .whereArrayContains("userIds", sortedConversation[0])
            .get()
            .await()
            .documents

        val conversationExists = existingConversations.any { doc ->
            val userIds = doc.get("userIds") as List<String>
            userIds.sorted() == sortedConversation
        }

        // If a matching conversation is found, do not add a new one
        if (conversationExists) {
            return
        }

        val newConversation = Conversations(
            date = Timestamp(Date()),
            lastMessage = null,
            userIds = sortedConversation
        )
        val message = Message(
            message = "",
            idSender = "",
            type_message = 0,
            date = Timestamp(Date())
        )
        val conversationRef =
            Firebase.firestore.collection("conversations").add(newConversation)

        // Get the document reference for the newly created conversation
        val docRef = conversationRef.await()
        Firebase.firestore.collection("conversations").document(docRef.id)
            .collection("messages").add(message).await()

    }

    companion object {
        private const val TAG = "UserRepository"
    }


}


class UserRepositoryException(message: String, cause: Throwable?) : Exception(message, cause)
