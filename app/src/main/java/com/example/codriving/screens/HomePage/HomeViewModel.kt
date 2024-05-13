package com.example.codriving.screens.HomePage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.codriving.data.model.Car
import com.example.codriving.data.repository.UploadCarRepository
import com.example.codriving.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.time.OffsetDateTime
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val carRepository: UploadCarRepository,private val userRepository: UserRepository) :
    ViewModel() {
    private val _selectedStartDay = MutableStateFlow(Date())
    val selectedStartDay: MutableStateFlow<Date> get() = _selectedStartDay

    private val _selectedEndDay =
        MutableStateFlow(Date(OffsetDateTime.now().plusDays(8).toInstant().toEpochMilli()))
    val selectedEndDay: MutableStateFlow<Date> get() = _selectedEndDay

    /* private val _selectedStartTime = MutableStateFlow(Date())
     val selectStartTime: StateFlow<Date> get() = _selectedStartTime
     private val _selectedEndTime = MutableStateFlow(Date())
     val selectedEndTime: StateFlow<Int> get() = _selectedEndTime*/

    private val _mostRated = MutableLiveData<HashMap<String, Car>>()
    val mostRated: LiveData<HashMap<String, Car>> get() = _mostRated
    //Encargarse  de la logica de cargar datos (poner maximo y al alcanzar pedir a la base de datos más)

    init {
        viewModelScope.launch {
            _mostRated.value = getTopRatedCars()
        }
    }

    suspend fun getTopRatedCars(): HashMap<String, Car> {
        return carRepository.getMostRatingCars()
    }

    fun setStartDay(day: Date) {
        val calendar = Calendar.getInstance()
        calendar.time = day

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

        val newCalendar = Calendar.getInstance()
        newCalendar.set(Calendar.YEAR, year)
        newCalendar.set(Calendar.MONTH, month)
        newCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

        val selectedStartDayCalendar = Calendar.getInstance()
        selectedStartDayCalendar.time = _selectedStartDay.value

        val hour = selectedStartDayCalendar.get(Calendar.HOUR_OF_DAY)
        val minute = selectedStartDayCalendar.get(Calendar.MINUTE)

        newCalendar.set(Calendar.HOUR_OF_DAY, hour)
        newCalendar.set(Calendar.MINUTE, minute)

        _selectedStartDay.value = newCalendar.time

    }

    fun setEndDay(day: Date) {
        val calendar = Calendar.getInstance()
        calendar.time = day

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

        val newCalendar = Calendar.getInstance()
        newCalendar.set(Calendar.YEAR, year)
        newCalendar.set(Calendar.MONTH, month)
        newCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

        val selectedStartDayCalendar = Calendar.getInstance()
        selectedStartDayCalendar.time = _selectedEndDay.value

        val hour = selectedStartDayCalendar.get(Calendar.HOUR_OF_DAY)
        val minute = selectedStartDayCalendar.get(Calendar.MINUTE)

        newCalendar.set(Calendar.HOUR_OF_DAY, hour)
        newCalendar.set(Calendar.MINUTE, minute)

        _selectedEndDay.value = newCalendar.time
    }

    fun setStartTime(hour: Int, minute: Int) {

        val newCalendar = Calendar.getInstance()
        newCalendar.set(Calendar.HOUR_OF_DAY, hour)
        newCalendar.set(Calendar.MINUTE, minute)

        val selectedStartDayCalendar = Calendar.getInstance()
        selectedStartDayCalendar.time = _selectedStartDay.value

        val year = selectedStartDayCalendar.get(Calendar.YEAR)
        val month = selectedStartDayCalendar.get(Calendar.MONTH)
        val day = selectedStartDayCalendar.get(Calendar.DAY_OF_MONTH)

        newCalendar.set(Calendar.YEAR, year)
        newCalendar.set(Calendar.MONTH, month)
        newCalendar.set(Calendar.DAY_OF_MONTH, day)

        _selectedStartDay.value = newCalendar.time

    }

    fun setEndTime(hour: Int, minute: Int) {

        val newCalendar = Calendar.getInstance()
        newCalendar.set(Calendar.HOUR_OF_DAY, hour)
        newCalendar.set(Calendar.MINUTE, minute)

        val selectedStartDayCalendar = Calendar.getInstance()
        selectedStartDayCalendar.time = _selectedEndDay.value

        val year = selectedStartDayCalendar.get(Calendar.YEAR)
        val month = selectedStartDayCalendar.get(Calendar.MONTH)
        val day = selectedStartDayCalendar.get(Calendar.DAY_OF_MONTH)

        newCalendar.set(Calendar.YEAR, year)
        newCalendar.set(Calendar.MONTH, month)
        newCalendar.set(Calendar.DAY_OF_MONTH, day)

        _selectedEndDay.value = newCalendar.time
    }

    fun logOut():Boolean{
        try{
            userRepository.logOut()
            return true
        }catch (e:Exception){
            return false
        }
    }

}