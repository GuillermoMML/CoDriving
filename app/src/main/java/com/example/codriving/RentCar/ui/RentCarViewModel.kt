package com.example.codriving.RentCar.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.codriving.data.Car
import com.example.codriving.data.RentCars
import com.example.codriving.data.repository.ReviewRepository
import com.example.codriving.data.repository.UploadCarRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RentCarViewModel @Inject constructor(private val uploadCarRepository: UploadCarRepository
) : ViewModel() {

    val repositoryReview = ReviewRepository()

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _rentCar = MutableLiveData<Car?>()
    val rentCar: LiveData<Car?> get() = _rentCar

    private val _listOfRents = MutableLiveData<List<RentCars>>(emptyList())
    val listOfRents: LiveData<List<RentCars>> get() = _listOfRents

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

     suspend fun loadData(id: String) {
         _rentCar.value = uploadCarRepository.getCarById(id)
         _listOfRents.value = uploadCarRepository.getRentsByCar(_rentCar.value!!.rentCars)
         _isLoading.value =true
        _error.value = null

    }



}


