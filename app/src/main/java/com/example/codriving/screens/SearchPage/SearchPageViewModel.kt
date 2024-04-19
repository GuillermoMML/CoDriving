package com.example.codriving.screens.SearchPage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.codriving.data.Car
import com.example.codriving.data.RentCars
import com.example.codriving.data.repository.SearchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class SearchPageViewModel @Inject constructor(
    private val searchRepository: SearchRepository,
) : ViewModel() {
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private var _rentListCar = MutableStateFlow<List<RentCars>>(emptyList())
    val rentListCar: StateFlow<List<RentCars>> get()  = _rentListCar

    private var _carListEnables = MutableStateFlow<HashMap<String, Car>>(HashMap())
    val carListEnable: StateFlow<HashMap<String, Car>> = _carListEnables



    suspend fun getRentsFromRange(startDate:Date,endDate:Date){
        _isLoading.value = true
        _rentListCar.value = searchRepository.findAvailableCars(startDate,endDate)

        _carListEnables.value = searchRepository.getCarReferences(rentCars = _rentListCar.value)
        _isLoading.value = false

    }
}