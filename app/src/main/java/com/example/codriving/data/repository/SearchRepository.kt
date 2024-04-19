package com.example.codriving.data.repository

import android.util.Log
import com.example.codriving.data.Car
import com.example.codriving.data.RentCars
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.tasks.await
import java.util.Date
import javax.inject.Inject

class SearchRepository @Inject constructor(private val firestore: FirebaseFirestore) {

    suspend fun findAvailableCars(startDate: Date, endDate: Date): List<RentCars> {

        val firebaseStartDate = Timestamp(startDate)
        val firebaseEndDate = Timestamp(endDate)

        // Crear una consulta que filtre por rango de fechas
        val query = firestore.collection("RentCar")
            .whereGreaterThanOrEqualTo("startDate", firebaseStartDate)
            .whereLessThanOrEqualTo("endDate", firebaseEndDate)
             // Add .distinct() for filtering duplicates based on carId

        // Ejecutar la consulta y obtener los resultados
        val snapshot = query.get().await()

        // Mapear los documentos a objetos RentCars
        val cars = snapshot.documents.map { document ->
            RentCars(
                carId = document["carId"] as DocumentReference,
                ownerName = document["ownerName"] as String,
                pricePerDay = document["pricePerDay"] as Double,
                startDate = document["startDate"] as Timestamp,
                endDate = document["endDate"] as Timestamp
            )
        }
        val deduplicatedCars = cars.distinctBy { it.carId }

        // Devolver la lista de RentCars
        return deduplicatedCars
    }

    suspend fun getCarReferences(rentCars: List<RentCars>): HashMap<String, Car> {

        val cars: HashMap<String, Car> = HashMap()
        rentCars.forEach { rentCar ->
            val carId = rentCar.carId.path // Extract car ID from carId reference
            val docRef = firestore.document(carId) // Create DocumentReference

            val documentSnapshot = docRef.get().await()
            if (documentSnapshot.exists()) {
                val car = documentSnapshot.toObject<Car>() // Replace with your data class
                if (car != null) {
                    cars[documentSnapshot.id] = car
                } else {
                    Log.w("getCarReferences", "Car not found for ID: $carId")
                }
            } else {
                Log.w("getCarReferences", "Document not found for car ID: $carId")
            }
        }

        return cars
    }


    suspend fun getCarPrice(rentRef: DocumentReference): String {
        val rentCarDocument = rentRef.get().await()
        return rentCarDocument["pricePerDay"] as String
    }
}