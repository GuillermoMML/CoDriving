package com.example.codriving.data

class ReviewRepository {

    private val reviews = mutableListOf<RentReview>()

    fun agregarReview(review: RentReview){
        reviews.add(review)
    }
    fun obtenerReviewsPorRentCar(RentCarId: Int):List<RentReview>{
        return reviews.filter { it.RentCars.id == RentCarId }
    }

}