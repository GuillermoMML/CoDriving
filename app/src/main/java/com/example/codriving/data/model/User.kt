package com.example.codriving.data.model

data class User(
    val fullName: String? = "",
    val email:String? = "",
    val phone:String? = "",
    var imageProfile:String = "https://firebasestorage.googleapis.com/v0/b/codriving-92f34.appspot.com/o/profileImages%2FProfileImage.png?alt=media&token=54154453-7ee8-46ac-bda3-72a8cf665e6b",
    val location: String = "",
    val rentalHistory: List<Car> = emptyList(),
    val ratings: List<RentReview> = emptyList()
)
