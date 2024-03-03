package com.example.codriving.repository

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class AuthRepository @Inject constructor(){
    private val auth = Firebase.auth

    fun signOut(){
        auth.signOut()
    }

    fun getCurrentUser(): FirebaseUser?{
        return auth.currentUser
    }
}
