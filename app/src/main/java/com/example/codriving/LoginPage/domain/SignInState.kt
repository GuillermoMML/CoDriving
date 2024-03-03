package com.example.codriving.LoginPage.domain

data class SignInState(
    val isSignSucessful: Boolean = false,
    val signInError: String? = null
)
