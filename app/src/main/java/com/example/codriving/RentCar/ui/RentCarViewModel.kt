package com.example.codriving.RentCar.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.codriving.Homepage.data.HomeState
import com.example.codriving.data.RentCars
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class RentCarViewModel(
) : ViewModel() {

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _rentCar = MutableLiveData<RentCars?>(null)
    val rentCar: LiveData<RentCars?> get() = _rentCar

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    suspend fun loadData(id: Int?) {
        delay(2000)
        if (id == null) {
            // Handle invalid ID or provide UI feedback
            return
        }

        _error.value = null


        viewModelScope.launch() {
            try {

                val state = HomeState()
                val rentCar = state.getFeaturedCarById(id)
                _rentCar.value = rentCar
            } catch (e: Exception) {
                Log.e("RentCarViewModel", "Error fetching RentCar data", e)
                _error.value = e.message
            } finally {
                _isLoading.value = true
            }
        }
    }
}


