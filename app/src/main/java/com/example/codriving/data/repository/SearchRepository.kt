package com.example.codriving.data.repository

import android.util.Log
import com.example.codriving.data.model.Car
import com.example.codriving.data.model.RentCars
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.tasks.await
import java.util.Date
import javax.inject.Inject

class SearchRepository @Inject constructor(private val firestore: FirebaseFirestore) {

    suspend fun findAvailableCars(
        startDate: Date,
        endDate: Date,
        pickUp: String,
        dropOff: String,
        findAvailable: Boolean
    ): List<RentCars> {

        val firebaseStartDate = Timestamp(startDate)
        val firebaseEndDate = Timestamp(endDate)

        // Realizar la consulta a Firestore
        val query = firestore.collection("RentCar")
            .whereGreaterThanOrEqualTo("startDate", firebaseStartDate)
            .whereLessThanOrEqualTo("endDate", firebaseEndDate)

        val snapshot = query.get().await()

        // Mapear los documentos a objetos RentCars
        val cars = snapshot.documents.mapNotNull { document ->
            val carId = document["carId"] as? DocumentReference ?: return@mapNotNull null
            val ownerName = document["ownerName"] as? String ?: return@mapNotNull null
            val busy = document["busy"] as? Boolean ?: return@mapNotNull null

            val pricePerDay = document["pricePerDay"] as? Double ?: return@mapNotNull null
            val startDate = document["startDate"] as? Timestamp ?: return@mapNotNull null
            val endDate = document["endDate"] as? Timestamp ?: return@mapNotNull null
            val pickUpLocation = document["pickUpLocation"] as? String
            val dropOffLocation = document["dropOffLocation"] as? String

            RentCars(
                carId,
                ownerName,
                busy,
                pricePerDay,
                startDate,
                endDate,
                pickUpLocation,
                dropOffLocation
            )
        }
        // Filtrar los autos en memoria según las condiciones parciales
        val filteredCars = cars.filter { car ->
            if (findAvailable) {
                car.busy == false // Only include cars with busy == true when findAvailable is true
            } else {
                true // Include all cars when findAvailable is false (no busy filter)
            } &&
                    (pickUp.isBlank() || (car.pickUpLocation?.contains(pickUp, ignoreCase = true)
                        ?: false)) &&
                    (dropOff.isBlank() || (car.dropOffLocation?.contains(dropOff, ignoreCase = true)
                        ?: false))
        }.distinctBy { it.carId }

        // Devolver la lista de RentCars filtrada
        return filteredCars
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
}