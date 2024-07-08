package com.example.codriving.view.SearchPage

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.codriving.data.model.Car
import com.example.codriving.data.model.RentCars
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

    private var _carListEnables = MutableStateFlow<HashMap<String, Car>>(HashMap())
    val carListEnable: StateFlow<HashMap<String, Car>> = _carListEnables


    suspend fun getRentsFromRange(
        startDate: Date,
        endDate: Date,
        pickUp: String,
        dropOff: String,
        findAvailable: Boolean
    ) {
        _isLoading.value = true
        Log.d("findAvailable", findAvailable.toString())
        _rentListCar.value =
            searchRepository.findAvailableCars(startDate, endDate, pickUp, dropOff, findAvailable)

        _carListEnables.value = searchRepository.getCarReferences(rentCars = _rentListCar.value)
        _isLoading.value = false

    }

}