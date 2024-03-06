package com.example.codriving.Homepage.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.codriving.data.Car
import com.example.codriving.data.repository.CarRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val carRepository: CarRepository): ViewModel(){
    val topRatedCars: LiveData<List<Car>> = carRepository.getTopRatedCars()
    val carsByBrand: LiveData<Map<String, List<Car>>> = carRepository.getCarsByBrand()

//Encargarse  de la logica de cargar datos (poner maximo y al alcanzar pedir a la base de datos más)
}