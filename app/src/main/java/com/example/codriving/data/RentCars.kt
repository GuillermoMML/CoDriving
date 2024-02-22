package com.example.codriving.data

import java.time.LocalDate

data class RentCars(
    val car: Car,
    val id: Int,
    val pricePerDay: Double,
    val startDate: LocalDate,
    val rating: Double,
    val endDate: LocalDate
)
