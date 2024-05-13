package com.example.codriving.data.model

import java.time.LocalDate

data class RentReview(
    val id: Int,//Id del RentView
    val author: String,
    val rating: Int,
    val comment: String,
    val date: LocalDate,
    val RentCars: RentCars
)
