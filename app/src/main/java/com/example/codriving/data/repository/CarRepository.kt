package com.example.codriving.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.codriving.data.Car
import com.google.firebase.firestore.FirebaseFirestore


class CarRepository() {
    private val firestore = FirebaseFirestore.getInstance()
    fun getTopRatedCars(): LiveData<List<Car>> {
        return MutableLiveData<List<Car>>()

    }

    fun getCarsByBrand(): LiveData<Map<String, List<Car>>> {
        return MutableLiveData<Map<String, List<Car>>>()

    }

}
