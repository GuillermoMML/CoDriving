package com.example.codriving.login.domain

data class SignInState(
    val isSignSucessful: Boolean = false,
    val signInError: String? = null
)
