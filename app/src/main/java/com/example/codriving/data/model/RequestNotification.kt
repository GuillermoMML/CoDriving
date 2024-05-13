package com.example.codriving.data.model

import androidx.compose.material3.ExperimentalMaterial3Api

data class RequestNotification @OptIn(ExperimentalMaterial3Api::class) constructor(
    val car: Car,
    val user: User,

    )
