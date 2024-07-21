package com.example.codriving.data.model

import com.google.firebase.firestore.DocumentReference

data class Car(
    var id: String = "",
    val plate: String = "",
    val brand: String = "",
    val enable: Boolean = false,
    val model: String = "",
    var year: String = "",
    val kilometers: Int = 0,
    var owner: DocumentReference? = null,
    val image: List<String> = emptyList(),
    val rating: Double? = 0.0,
    var rentCars: List<DocumentReference?> = emptyList(), // Campo de referencia a las RentCars
    val numberOfReviews: Int = 0,
)

