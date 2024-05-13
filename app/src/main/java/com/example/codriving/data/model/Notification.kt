package com.example.codriving.data.model

import com.google.firebase.Timestamp
import java.util.Date

data class Notification(
    val idNotification: String? = "",
    val idProduct: String? = "",
    val idReceiver: String? = "",
    val idSender: String = "",
    val title: String? = "",
    val message: String? = "",
    val timestamp: Timestamp? = Timestamp(Date()),
    val type: Int = 0, //tipo = 1 usuarios interesado , tipo 2  = informativo
    val rentsCars: List<String> = emptyList()
)
