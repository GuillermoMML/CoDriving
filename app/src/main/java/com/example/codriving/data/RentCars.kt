package com.example.codriving.data

import java.time.LocalDate

data class RentCars(
    val id: Int,
    val brand: String,
    val model: String,
    val year: Int,
    val image: Array<String>,
    val kilometros: Int,
    val pricePerDay: Double,
    val startDate: LocalDate,
    val endDate: LocalDate
)
