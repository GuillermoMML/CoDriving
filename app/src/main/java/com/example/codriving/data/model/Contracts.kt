package com.example.codriving.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import java.util.Date

data class Contracts(
    val idContracts: String? = "",
    val idProduct: String? = "",
    val idReceiver: String? = "",
    val idSender: String = "",
    val timestamp: Timestamp? = Timestamp(Date()),
    val rentCars: List<DocumentReference?> = emptyList(), // Campo de referencia a las RentCars

)