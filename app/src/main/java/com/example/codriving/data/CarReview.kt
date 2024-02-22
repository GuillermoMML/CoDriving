package com.example.codriving.data

import java.time.LocalDate

data class Review(
    val id: Int,
    val author: String,
    val rating: Int,
    val comment: String,
    val date: LocalDate
)
