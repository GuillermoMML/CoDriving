package com.example.codriving.data.repository

import   com.example.codriving.data.RentReview

class ReviewRepository {

    private val reviews = mutableListOf<RentReview>()

    fun agregarReview(review: RentReview){
        reviews.add(review)
    }

}