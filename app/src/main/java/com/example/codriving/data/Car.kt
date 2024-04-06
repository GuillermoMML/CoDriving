package com.example.codriving.data

import com.google.firebase.firestore.DocumentReference

data class Car(
    val id: String = "",
    val plate: String = "",
    val brand: String = "",
    val model: String = "",
    var year: String = "",
    val kilometers: Int = 0,
    val image: List<String> = emptyList(),
    val rating: Double? = 0.0,
    val rentCars: List<DocumentReference?> = emptyList() // Campo de referencia a las RentCars
)

