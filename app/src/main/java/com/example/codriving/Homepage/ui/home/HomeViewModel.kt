package com.example.codriving.Homepage.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.codriving.Homepage.data.HomeState
import com.example.codriving.data.RentCars

class HomeViewModel : ViewModel(){
    private val _homeState = HomeState()
    val featuredCars: List<RentCars>
        get() = _homeState.getFeaturedCarBy()
//Encargarse  de la logica de cargar datos (poner maximo y al alcanzar pedir a la base de datos más)
}