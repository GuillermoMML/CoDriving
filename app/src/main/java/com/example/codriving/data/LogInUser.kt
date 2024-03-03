package com.example.codriving.data

data class NewUser(
    val fullName:String,
    val email:String,
    val phone:String?,
    val location:String?,
    val rentalHistory: List<RentCars> = emptyList(),
    val ratings: List<RentReview> = emptyList()
    )
