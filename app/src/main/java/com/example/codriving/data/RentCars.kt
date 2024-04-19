package com.example.codriving.data

import com.google.firebase.firestore.DocumentReference


data class RentCars(
    val carId: DocumentReference, //Id que almacena el carId del firestore
    val ownerName: String,
    val pricePerDay: Double,
    val startDate: com.google.firebase.Timestamp,
    val endDate: com.google.firebase.Timestamp
)
