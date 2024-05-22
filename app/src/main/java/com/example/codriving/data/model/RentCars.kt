package com.example.codriving.data.model

import com.google.firebase.firestore.DocumentReference


data class RentCars(
    val carId: DocumentReference, //Id que almacena el carId del firestore
    val ownerName: String,
    val busy: Boolean? = false,
    val pricePerDay: Double = 0.0,
    val startDate: com.google.firebase.Timestamp,
    val endDate: com.google.firebase.Timestamp
)
