package com.example.codriving.data.model

import com.google.firebase.Timestamp
import java.util.Date

data class Message(
    val message: String = "",
    val idSender: String = "",
    val type_message: Int = 0,
    val date: Timestamp = Timestamp(Date())

)