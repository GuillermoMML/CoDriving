package com.example.codriving.login.domain

data class SignInResult(
    val data: UserData?,
    val errorMessage:String?
)

data class UserData(
    val userId: String,
    val username: String?,
    val profilePictureUrl:String?
)

