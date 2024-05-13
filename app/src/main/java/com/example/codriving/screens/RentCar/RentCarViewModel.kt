package com.example.codriving.screens.RentCar

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.codriving.data.model.Car
import com.example.codriving.data.model.RentCars
import com.example.codriving.data.model.User
import com.example.codriving.data.repository.UploadCarRepository
import com.example.codriving.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RentCarViewModel @Inject constructor(private val uploadCarRepository: UploadCarRepository,private val userRepository: UserRepository
) : ViewModel() {
    private val _ownerUser = MutableLiveData(User())
    val ownerUser:LiveData<User> get() = _ownerUser


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
         _listOfRents.value =
             uploadCarRepository.getRentsByCar(_rentCar.value!!.rentCars).values.toList()
         _ownerUser.value =
             _rentCar.value!!.owner?.let { userRepository.documentSnapshotToUser(it) }
         _isLoading.value = true
         _error.value = null

     }



}


