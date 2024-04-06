package com.example.codriving.data

import com.google.firebase.firestore.DocumentReference

data class Car(
    val id: String,
    val plate: String,
    val brand: String,
    val model: String,
    var year: String,
    val kilometers: Int,
    val image: List<String>,
    val rating: Double? = 0.0,
    val rentCars: List<DocumentReference?> // Campo de referencia a las RentCars
){
}

