package com.example.codriving.RentCar.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.saveable
import com.example.codriving.Homepage.data.HomeState
import com.example.codriving.data.RentCars

class RentCarViewModel constructor(private val savedStateHandle: SavedStateHandle) : ViewModel() {
    val rentCar = MutableLiveData<RentCars>()

    fun randomQuote(){
        val currentRentCar = HomeState().getFeaturedCarById(2)
        rentCar.postValue(currentRentCar)
    }
}
