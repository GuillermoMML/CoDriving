package com.example.codriving.view.ChatPage

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.example.codriving.data.model.Message
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor() :
    ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages
    private var conversationId: String = ""

    fun fetchMessages(conversationId: String) {
        this.conversationId = conversationId
        firestore.collection("conversations").document(conversationId).collection("messages")
            .orderBy("date")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    // Handle error
                    return@addSnapshotListener
                }

                val chatMessages = snapshot?.documents?.map { document ->
                    Message(
                        message = document.getString("message") ?: "",
                        idSender = document.getString("idSender") ?: "",
                        type_message = (document.getLong("type_message") ?: 0L).toInt(),
                        date = document.getTimestamp("date") ?: Timestamp(Date())
                    )
                } ?: emptyList()

                _messages.value = chatMessages
            }
    }

    fun sendMessage(message: String, type: Int = 0) {
        var lastMessage = message
        firestore.collection("conversations").document(conversationId).collection("messages")
            .add(
                Message(
                    message = message,
                    idSender = Firebase.auth.uid.toString(),
                    type_message = type,
                    date = Timestamp(Date())
                )
            )
        if (type == 2) {
            lastMessage = "Picture"
        }
        firestore.collection("conversations").document(conversationId)
            .update("lastMessage", lastMessage)

    }

    suspend fun uploadImage(uri: Uri, onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit) {
        try {
            val storageRef = storage.reference.child("chatimages/${uri.lastPathSegment}")
            //val uploadTask = storageRef.putFile(uri).await()
            val downloadUrl = storageRef.downloadUrl.await()
            onSuccess(downloadUrl.toString())
        } catch (e: Exception) {
            onFailure(e)
        }
    }


}