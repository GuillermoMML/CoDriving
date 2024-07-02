package com.example.codriving.data.repository

import android.util.Log
import com.example.codriving.data.model.Car
import com.example.codriving.data.model.RentCars
import com.example.codriving.data.model.RentReview
import com.example.codriving.data.model.User
import com.google.android.gms.tasks.Tasks
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import java.util.Date
import javax.inject.Inject

class UploadCarRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
) {


    fun busyRentCar(documentReference: DocumentReference) {
        try {
            Tasks.await(documentReference.update("busy", true))
        } catch (e: Exception) {
            throw e  // Lanza la excepción para que sea manejada por el llamador
        }
    }

    suspend fun getModelsByMark(marca: String): List<String> {
        val modelos = mutableListOf<String>()
        try {
            val query = firestore.collection("makes").document(marca).get()
            val documentSnapshot = query.await()

            if (documentSnapshot != null) {
                val data = documentSnapshot.data
                val modelosArrayList =
                    data!!.get("modelos") as? ArrayList<HashMap<String, Any>>

                if (modelosArrayList != null && modelosArrayList.isNotEmpty()) {
                    for (models in modelosArrayList) {
                        val nombre = models["nombre"] as String
                        modelos.add(nombre)
                    }
                }
                return modelos
            } else {
                throw Exception("Error en la petición")
            }

        } catch (e: Exception) {
            throw UploadCarRepositoryException("Error al buscar: ${e.message}", e)


        }
        return modelos

    }

    suspend fun getMarks(): MutableList<String> {
        val marcas = mutableListOf<String>()
        try {
            // Use await to suspend execution until Firestore returns
            val querySnapshot = firestore.collection("makes").get().await()

            for (marca in querySnapshot.documents) {
                marcas.add(marca.id)
            }
        } catch (e: Exception) {
            Log.d("Error", "${e.message}")
        }
        return marcas // Return after Firestore data is processed
    }

    suspend fun createCar(car: Car) {
        val auth = FirebaseAuth.getInstance().uid
        try {
            val carDocRef = firestore.collection("Cars").document()
            carDocRef.set(car)
                .await()
            //Referencia user
            val userDocRef = firestore.collection("Users")
                .document(auth!!)
            // Agrega el ID del carro recién creado a la lista en el documento del usuario
            userDocRef.update("cars", FieldValue.arrayUnion(carDocRef.id))
                .await()
            // Operación exitosa
        } catch (e: Exception) {
            throw UserRepositoryException("Error al crear el usuario: ${e.message}", e)
        }
    }


    suspend fun getCurretCars(): Map<String, Car> {
        val carMap = mutableMapOf<String, Car>()
        try {
            val userDocRef =
                firestore.collection("Users").document(FirebaseAuth.getInstance().uid.toString())
            val document = userDocRef.get().await()
            val carIds: List<String>? = document.get("cars") as? List<String>
            if (carIds != null) {
                for (carId in carIds) {
                    val carDocument = firestore.collection("Cars").document(carId).get().await()
                    if (carDocument != null && carDocument.exists()) {
                        // Aquí puedes trabajar con el documento del coche
                        val id = carDocument.id // ID del documento
                        val plate = carDocument.getString("plate") ?: ""
                        val brand = carDocument.getString("brand") ?: ""
                        val model = carDocument.getString("model") ?: ""
                        val enable = carDocument.getBoolean("enable") ?: false
                        val year = carDocument.getString("year") ?: ""
                        val kilometers = carDocument.getLong("kilometers")?.toInt() ?: 0
                        val rating = carDocument.getLong("rating")?.toDouble() ?: 0.0
                        val owner = carDocument.getDocumentReference("owner")
                        val image = carDocument.get("image") as? List<String> ?: emptyList()

                        // Obtener las referencias de las rentas asociadas al coche
                        val rentCarsRefs =
                            carDocument.get("rentCars") as? List<DocumentReference?> ?: emptyList()

                        // Crear el objeto Car y agregarlo al mapa
                        val car = Car(
                            id,
                            plate,
                            brand,
                            enable,
                            model,
                            year,
                            kilometers,
                            owner,
                            image,
                            rating,
                            rentCarsRefs
                        )
                        carMap[id] = car
                    } else {
                        // El documento del coche no existe o es null
                    }
                }
            }
            return carMap
        } catch (e: Exception) {
            throw UserRepositoryException("Error al obtener los coches: ${e.message}", e)
        }
    }

    suspend fun deleteCar(carId: String) {
        val auth = FirebaseAuth.getInstance()
        try {
            // Elimina el documento del coche
            firestore.collection("Cars").document(carId).delete().await()
            // Elimina la referencia del coche de todos los usuarios que lo tienen alquilado
            val userQuerySnapshot =
                firestore.collection("Users").document(auth.currentUser!!.uid).get().await()
            val usersCars = userQuerySnapshot.get("cars") as? List<String>
            if (usersCars != null) {
                val updatedCars = usersCars.toMutableList()
                for (userDocument in usersCars) {
                    if (userDocument == carId) {
                        updatedCars.remove(carId)
                    }
                }
                userQuerySnapshot.reference.update("cars", updatedCars).await()
            }
        } catch (e: Exception) {
            throw UploadCarRepositoryException("Error al eliminar el coche: ${e.message}", e)
        }
    }

    suspend fun getCarById(id: String): Car {
        val carDocument = firestore.collection("Cars").document(id).get().await()
        val car =
            carDocument.toObject(Car::class.java) ?: throw IllegalStateException("Invalid data")
        return car
    }


    suspend fun getMostRatingCars(index: Long = 0): HashMap<String, Car> {
        val carsRef = firestore.collection("Cars")

        val querySnapshot = carsRef
            .whereEqualTo("enable", true) // Filtrar solo los coches habilitados
            .orderBy(
                "rating",
                Query.Direction.DESCENDING
            ) // Ordenar por rating en orden descendente
            .limit(index + 5) // Obtener los 5 mejores coches
            .get()
            .await()

        val resultMap = HashMap<String, Car>()

        querySnapshot.documents.forEach { document ->
            val car =
                document.toObject(Car::class.java) ?: throw IllegalStateException("Invalid data")
            resultMap[document.id] = car
        }

        val sortedMap = resultMap.entries
            .sortedByDescending { it.value.rating }
            .associateBy({ it.key }, { it.value })
            .toMap(LinkedHashMap())

        return sortedMap
    }

    suspend fun getRentsByCar(listDocument: List<DocumentReference?>): HashMap<DocumentReference, RentCars> {

        val rentCarsMap = hashMapOf<DocumentReference, RentCars>()

        for (rentCarRef in listDocument) {
            val rentCarDocument = rentCarRef!!.get().await()
            val busy = rentCarDocument["busy"] as? Boolean
                ?: true // Default to true if "busy" is not present or null

            if (!busy) {
                val documentId = rentCarDocument  // Get the document ID as the key

                val rentCar = RentCars(
                    carId = rentCarDocument["carId"] as DocumentReference,
                    ownerName = rentCarDocument["ownerName"] as String,
                    pricePerDay = rentCarDocument["pricePerDay"] as Double,
                    startDate = rentCarDocument["startDate"] as Timestamp,
                    endDate = rentCarDocument["endDate"] as Timestamp,
                    busy = rentCarDocument["busy"] as Boolean
                )
                rentCarsMap[documentId.reference] =
                    rentCar  // Add RentCar to map with document ID as key
            }
        }

        return rentCarsMap
    }
    suspend fun publishRentCar(carIdwithBrackets: String, start: Date, end: Date, price: String) {
        try {

            val carId = carIdwithBrackets.replace(Regex("\\[(.*?)\\]"), "$1")

            val startStamp = Timestamp(start)
            val endStamp = Timestamp(end)

            val userDoc =
                firestore.collection("Users").document(FirebaseAuth.getInstance().uid.toString())
                    .get().await()
            val ownerName = userDoc.getString("fullName") ?: ""

            // Get the car reference directly
            val carRef =
                firestore.collection("Cars").document(carId) // Double-check collection name

            val rentClass = RentCars(
                carId = carRef, // Assign the DocumentReference directly
                endDate = endStamp,
                startDate = startStamp,
                ownerName = ownerName,
                pricePerDay = price.toDouble()
            )

            val rentDocRef = firestore.collection("RentCar").document()
            rentDocRef.set(rentClass).await()

            carRef.get().addOnSuccessListener { carSnapshot ->
                if (carSnapshot.exists()) {
                    // Proceed with updating the rentCars array
                    val existingRentCars =
                        carSnapshot.get("rentCars") as? List<DocumentReference> ?: emptyList()
                    val updatedRentCars = existingRentCars + rentDocRef

                    carRef.update("rentCars", updatedRentCars)
                    carRef.update("enable", true)

                } else {
                    // Handle the case where the "Cars" collection or document doesn't exist
                    Log.w(
                        "Firestore",
                        "Car document not found: $carId"
                    ) // Or show an error message to the user
                }
            }
                .addOnFailureListener { exception ->
                    // Handle errors during document retrieval
                    Log.e("Firestore", "Error fetching car document", exception)
                }

        } catch (e: Exception) {
            throw UserRepositoryException("Error al crear el usuario: ${e.message}", e)
        }
    }

    suspend fun getCarRentByReference(ref: DocumentReference): RentCars? {
        val documentSnapshot = ref.get().await()
        if (documentSnapshot != null && documentSnapshot.exists()) {

            val rentCar = RentCars(
                carId = documentSnapshot["carId"] as DocumentReference,
                ownerName = documentSnapshot["ownerName"] as String,
                pricePerDay = documentSnapshot["pricePerDay"] as Double,
                startDate = documentSnapshot["startDate"] as Timestamp,
                endDate = documentSnapshot["endDate"] as Timestamp,
                busy = documentSnapshot["busy"] as Boolean
            )
            return rentCar

        } else {
            return null
        }
    }

    /*suspend fun getCarPrice(rentRef: DocumentReference): String {
        val rentCarDocument = rentRef.get().await()
        return rentCarDocument["pricePerDay"] as String
    }*/


    fun getPreviewReviewByCar(id: String, onComplete: (Map<User, RentReview>?) -> Unit) {
        val result = mutableMapOf<User, RentReview>()
        firestore.collection("Cars").document(id).collection("reviews").get()
            .addOnSuccessListener { reviewSnapshot ->
                val reviewPromises = reviewSnapshot.documents.map { reviewDoc ->
                    val review = reviewDoc.toObject(RentReview::class.java)
                    val userId = reviewDoc.getString("reviewSender")

                    if (review != null && userId != null) {
                        val userPromise = firestore.collection("Users").document(userId).get()
                            .addOnSuccessListener { userDoc ->
                                val user = userDoc.toObject(User::class.java)
                                if (user != null) {
                                    result[user] = review
                                }
                            }
                            .addOnFailureListener {
                                onComplete(null)
                            }

                        val existingReviewPromise = firestore.collection("Cars").document(id)
                            .collection("reviews").document(userId).get()

                        Tasks.whenAll(userPromise, existingReviewPromise).addOnSuccessListener {
                            val existingReview =
                                existingReviewPromise.result?.toObject(RentReview::class.java)
                            if (existingReview != null) {
                                // Si ya existe una revisión del usuario, actualiza esa revisión
                                firestore.collection("Cars").document(id)
                                    .collection("reviews").document(userId).set(review)
                                    .addOnSuccessListener {
                                        onComplete(result.toMap())
                                    }
                                    .addOnFailureListener {
                                        onComplete(null)
                                    }
                            } else {
                                // Si no existe una revisión del usuario, agrega la nueva revisión
                                onComplete(result.toMap())
                            }
                        }
                    } else {
                        null // Si la review o el userId son nulos, devuelve null
                    }
                }

                Tasks.whenAll(reviewPromises).addOnCompleteListener {
                    if (!it.isSuccessful) {
                        onComplete(null)
                    }
                }
            }
            .addOnFailureListener {
                onComplete(null) // En caso de error, se devuelve null
            }
    }

}

class UploadCarRepositoryException(message: String, cause: Throwable?) : Exception(message, cause)



