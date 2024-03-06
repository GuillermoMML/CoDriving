package com.example.codriving.data

import java.time.LocalDate

data class RentCars(
    val id: String, //Id que almacena el carId del firestore
    val ownerName: String,
    val pricePerDay: Double,
    val startDate: LocalDate,
    val rating: Double,
    val endDate: LocalDate
)
