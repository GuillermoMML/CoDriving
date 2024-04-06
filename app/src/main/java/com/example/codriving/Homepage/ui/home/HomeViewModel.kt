package com.example.codriving.Homepage.ui.home

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
import java.text.SimpleDateFormat
import java.time.OffsetDateTime
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val carRepository: UploadCarRepository): ViewModel(){
    private val _selectedStartDay  = MutableStateFlow(Date().time)
    val selectedStartDay: MutableStateFlow<Long> get() = _selectedStartDay

    private val _selectedEndDay = MutableStateFlow(OffsetDateTime.now().plusDays(8).toInstant().toEpochMilli())
    val selectedEndDay: MutableStateFlow<Long> get() = _selectedEndDay

    private val _selectedStartTime = MutableStateFlow("00:00")
    val selectStartTime: StateFlow<String> get() = _selectedStartTime
    private val _endHour = MutableStateFlow("00:00")
    val endHour: StateFlow<String> get() = _endHour

    private val _mostRated = MutableLiveData<List<Car>>()
    val mostRated: LiveData<List<Car>> get() = _mostRated
    //Encargarse  de la logica de cargar datos (poner maximo y al alcanzar pedir a la base de datos más)

    init{
        viewModelScope.launch {
            _mostRated.value = getTopRatedCars()
        }
    }
    suspend fun getTopRatedCars(): List<Car> {
      return carRepository.getMostRatingCars()
    }
    fun setStartDay(day: Long){
        _selectedStartDay.value = day

    }
    fun setEndDay(day: Long){
        _selectedEndDay.value = day
    }
    fun setStartTime(hour:String){
        _selectedStartTime.value = hour
    }
    fun setEndTime(hour:String){
        _endHour.value = hour
    }


    fun LongToStringDate(DateLong:Long):String{
        val sdf = SimpleDateFormat("dd/MM/yyyy")
        val formattedDate = sdf.format(DateLong)
        return formattedDate
    }
    fun calculateEndDate(startDate: Long): Long {
        return startDate + (5 * 24 * 60 * 60 * 1000) // 5 days in milliseconds
    }

}