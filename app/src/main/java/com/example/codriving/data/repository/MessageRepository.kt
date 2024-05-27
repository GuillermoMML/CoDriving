package com.example.codriving.data.repository

import com.example.codriving.data.model.Conversations
import com.example.codriving.data.model.Message
import com.example.codriving.data.model.User
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import javax.inject.Inject


class MessagesRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {

    private var listenerRegistration: ListenerRegistration? = null
    private var currentConversationsRef: DocumentReference? = null

    fun getChatAvailable(
        onSuccess: (Map<String, Conversations>) -> Unit,
        onError: (Exception) -> Unit
    ) {

        firestore.collection("conversations").orderBy("date")
            .whereArrayContains("userIds", auth.uid!!)
            .addSnapshotListener { value, e ->
                if (e != null) {
                    onError(e)
                    return@addSnapshotListener
                }
                val conversationsMap = emptyMap<String, Conversations>().toMutableMap()

                value?.forEach { doc ->

                    val docId = doc.id
                    val date = doc.getTimestamp("date") ?: Timestamp.now()
                    val lastMessage = doc.getString("lastMessage") ?: ""
                    val userIds = doc.get("userIds") as? List<String> ?: emptyList()
                    val conversation = Conversations(date, lastMessage, userIds)

                    conversationsMap[docId] = conversation
                }

                onSuccess(conversationsMap)

            }


    }

    /*
* Obtener los perfiles [idMessage] = Usuario
 */
    fun getProfilesMessages(
        conversations: Map<String, Conversations>,
        onError: (Exception) -> Unit,
        onSuccess: (Map<String, User>) -> Unit
    ) {
        val profilesMap = mutableMapOf<String, User>()
        var pendingRequests = conversations.size

        conversations.forEach { (key, value) ->
            val userIds = value.userIds ?: emptyList()
            val otherUserId = userIds.firstOrNull { it != auth.uid }

            if (otherUserId != null) {
                firestore.collection("Users").document(otherUserId)
                    .get()
                    .addOnSuccessListener { document ->
                        if (document != null && document.exists()) {
                            val user = document.toObject(User::class.java)
                            if (user != null) {
                                if (user.imageProfile.isEmpty() == true) {
                                    user.imageProfile =
                                        "https://static-00.iconduck.com/assets.00/profile-default-icon-2048x2045-u3j7s5nj.png"
                                }
                                profilesMap[key] = user
                            } else {
                                onError(IllegalStateException("Invalid data"))
                                return@addOnSuccessListener
                            }
                        } else {
                            onError(IllegalStateException("Document does not exist"))
                            return@addOnSuccessListener
                        }

                        pendingRequests--
                        if (pendingRequests == 0) {
                            onSuccess(profilesMap)
                        }
                    }
                    .addOnFailureListener { exception ->
                        onError(exception)
                        return@addOnFailureListener
                    }
            } else {
                onError(IllegalStateException("Other user ID is null"))
                return@forEach
            }
        }
    }

    /* /*
     *Lista de mensajes por identificador del emisor
      */
     fun getMessages(idDocument: String, value: (List<Message>) -> Unit) {
         val messagesRef = firestore.collection("conversations").document(idDocument).collection("mensajes")

         val messages = mutableListOf<Message>()

         val listener = messagesRef.addSnapshotListener { snapshot, error ->
             if (error != null) {
                 // Handle errors
                 return@addSnapshotListener
             }

             if (snapshot != null) {
                 messages.clear() // Clear existing messages before adding new ones
                 for (document in snapshot.documents) {
                     val message = document.toObject(Message::class.java)!!
                     if (message.idSender != null && message.idSender.isNotEmpty()) {
                         messages.add(message)
                     }
                 }
                 value(messages.toList()) // Update UI with the latest messages
             }
         }

         // Unregister the listener when the ViewModel/Activity is destroyed
         viewModelScope.onCleared { listener.remove() }
     }*/

    /*  fun getMessages(idDocument: String,onSuccess: (messages: MutableList<Message>) -> Unit,onError: (Exception) -> Unit) {
          currentConversationsRef = firestore.collection("conversations").document(idDocument)

          val messagesRef = currentConversationsRef!!.collection("messages").orderBy("date",Query.Direction.ASCENDING)

          val messages = mutableListOf<Message>()

          messagesRef.addSnapshotListener { snapshot, error ->
              if (error != null) {
                  // Handle errors
                  onError(error)
                  return@addSnapshotListener
              }

              if (snapshot != null) {
                  messages.clear() // Clear existing messages before adding new ones
                  for (document in snapshot.documents) {
                      val message = document.toObject(Message::class.java)!!
                      if (message.idSender != null && message.idSender.isNotEmpty()) {
                          messages.add(message)
                      }
                  }
                  onSuccess(messages)

              }
          }

      }*/
    fun getMessages2(
        documentReference: DocumentReference,
        onSuccess: (messages: MutableList<Message>) -> Unit,
        onError: (Exception) -> Unit
    ) {

        val messagesRef =
            Firebase.firestore.collection("conversations").document(documentReference.id)
                .collection("messages").orderBy("date", Query.Direction.ASCENDING)

        val messages = mutableListOf<Message>()

        messagesRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                // Handle errors
                onError(error)
                return@addSnapshotListener
            }

            if (snapshot != null) {
                messages.clear() // Clear existing messages before adding new ones
                for (document in snapshot.documents) {
                    val message = document.toObject(Message::class.java)!!
                    if (message.idSender != null && message.idSender.isNotEmpty()) {
                        messages.add(message)
                    }
                }
                onSuccess(messages)

            }
        }

    }

    fun getMessagesListener(): ListenerRegistration? {
        return listenerRegistration
    }


}

