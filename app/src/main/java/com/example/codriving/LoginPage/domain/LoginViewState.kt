package com.example.codriving.LoginPage.domain

data class LoginViewState(
    val email: String,
    val password: String,
    val isValidEmail: Boolean,
    val isValidPassword: Boolean
)