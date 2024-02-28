package com.example.codriving.data

data class Car(
    val plate: String,
    val brand: String,
    val model: String,
    val year: Int,
    val kilometers: Int,
    val image: List<String>,
)

