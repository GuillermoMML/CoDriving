package com.example.codriving.data.repository

import javax.inject.Inject

class RentalsRepository @Inject constructor(
    userRepository: UserRepository,
    firebaseAuthRepository: FirebaseAuthRepository
) {
    private val curretUserId = firebaseAuthRepository.getCurrentUser()!!.uid


}