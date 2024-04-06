package com.example.codriving.MyCars

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.codriving.data.Car
import com.example.codriving.data.repository.UploadCarRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class ListMyCarsViewModel @Inject constructor(private val uploadCarRepository: UploadCarRepository
) : ViewModel() {
    private var _carListState = MutableStateFlow<Map<String, Car>>(emptyMap())
    val carListState: StateFlow<Map<String, Car>> = _carListState

    private val _price = MutableLiveData("")
    val price: LiveData<String> get() = _price

    private val _startDay = MutableStateFlow(Date())
    val startDay:StateFlow<Date> get() =_startDay
    val endDay:StateFlow<Date> get() =_endDay

    private val _endDay = MutableStateFlow(Date())


    private val _isLoaded = MutableLiveData(false)
    val isLoaded: LiveData<Boolean> get() = _isLoaded

    private val _isLoadPublished = MutableLiveData(false)
    val isLoadPublished: LiveData<Boolean> get() = _isLoadPublished


    init {
        viewModelScope.launch {
            _carListState.value = uploadCarRepository.getCurretCars()
            _isLoaded.value = true
        }
    }

    suspend fun removeCarFromList(id: String) {
        uploadCarRepository.deleteCar(id)
        _carListState.value = uploadCarRepository.getCurretCars()
    }


    fun updatePrice(newPrice: String) {
        _price.value = newPrice
    }

    fun setStartDay(day: Date){
        _startDay.value = day

    }
    fun setEndDay(day: Date){
        _endDay.value = day
    }

    suspend fun verifyPublishFields(idCar: String): Boolean {

        if(_startDay.value.equals(null) || _endDay.value.equals(null) || _price.value == ""){
            return false
        }else{
            _isLoadPublished.value = true
            uploadCarRepository.publishRentCar(idCar,_startDay.value,_endDay.value,_price.value!!)
            _carListState.value = uploadCarRepository.getCurretCars()
            _price.value = ""

        }
        _isLoadPublished.value = false
        return true
    }
}

