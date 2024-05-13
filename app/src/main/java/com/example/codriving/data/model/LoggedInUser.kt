package com.example.codriving.data.model

// Model
data class LoggedInUser(
    var isLoggedIn: Boolean,
    var userId: String,
    var authToken: String
)
