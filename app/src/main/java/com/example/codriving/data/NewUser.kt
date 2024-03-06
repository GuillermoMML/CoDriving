package com.example.codriving.data

import com.google.type.LatLng

data class NewUser(
    val fullName: String?,
    val email:String,
    val phone:String?,
    val location: LatLng?,
    val rentalHistory: List<Car> = emptyList(),
    val ratings: List<RentReview> = emptyList()
)
