package com.example.codriving.data

import com.google.firebase.firestore.DocumentReference

data class Car(
    val plate: String,
    val brand: String,
    val model: String,
    val year: String,
    val kilometers: Int,
    val image: List<String>,
    val rentCars: List<DocumentReference> // Campo de referencia a las RentCars
)

