package com.example.codriving.data.model

import com.google.firebase.Timestamp

data class RequestContracts(
    val car: Car,
    val owner: User,
    val client: User,
    val rentCars: List<RentCars>,
    val expiredDate: Timestamp
)
