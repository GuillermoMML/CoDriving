package com.example.codriving.view.LoginPage.data

import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseClient @Inject constructor(){
    val auth: FirebaseAuth get() = FirebaseAuth.getInstance()
}