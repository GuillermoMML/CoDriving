package com.example.codriving.data

import com.google.firebase.firestore.DocumentReference
import java.sql.Timestamp

data class RentCars(
    val carId: DocumentReference, //Id que almacena el carId del firestore
    val ownerName: String,
    val pricePerDay: Double,
    val startDate: Timestamp,
    val rating: Double = 0.0,
    val endDate: Timestamp
)
