package com.example.codriving.view.HomePage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.codriving.data.model.Car
import com.example.codriving.data.repository.UploadCarRepository
import com.example.codriving.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.time.OffsetDateTime
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val uploadCarRepository: UploadCarRepository,
    private val userRepository: UserRepository
) :
    ViewModel() {
    private val _selectedStartDay = MutableStateFlow(Date())
    val selectedStartDay: MutableStateFlow<Date> get() = _selectedStartDay

    private val _selectedEndDay =
        MutableStateFlow(Date(OffsetDateTime.now().plusDays(8).toInstant().toEpochMilli()))
    val selectedEndDay: MutableStateFlow<Date> get() = _selectedEndDay

    private val _pickUp = MutableStateFlow(String())
    val pickUp: MutableStateFlow<String> get() = _pickUp

    private val _dropOff = MutableStateFlow(String())
    val dropOff: MutableStateFlow<String> get() = _dropOff

    private val _mostRated = MutableLiveData<HashMap<String, Car>>()
    val mostRated: LiveData<HashMap<String, Car>> get() = _mostRated

    private val _isLoading = MutableLiveData(true)
    val isLoading: LiveData<Boolean> get() = _isLoading
    private val _isLoadingMoreData = MutableLiveData<Boolean>()
    val isLoadinMoreDate: LiveData<Boolean> get() = _isLoadingMoreData

    init {
        viewModelScope.launch {
            delay(1000)
            _mostRated.value = getTopRatedCars()
            _isLoading.value = false

        }
    }


    suspend fun getTopRatedCars(index: Long = 0): HashMap<String, Car> {
        return uploadCarRepository.getMostRatingCars(index)
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

    fun logOut(): Boolean {
        try {
            userRepository.logOut()
            return true
        } catch (e: Exception) {
            return false
        }
    }

    suspend fun addConversation(sorted: List<String>) {
        userRepository.addConversation(sorted)
    }

    suspend fun loadMoreData(index: Long) {
        _isLoadingMoreData.value = true
        _mostRated.value = uploadCarRepository.getMostRatingCars(index)
        delay(1000)
        _isLoadingMoreData.value = false
    }

    fun setPickUpandDropOff(pickUp: String, dropOff: String) {
        _pickUp.value = pickUp
        _dropOff.value = dropOff
    }

}