package com.example.codriving.data.model

import com.google.firebase.Timestamp
import java.util.Date

data class Conversations(
    val date: Timestamp? = Timestamp(Date()),
    val lastMessage: String? = "",
    val userIds: List<String>? = emptyList()
)
