package com.example.codriving.screens.notificationPage

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.codriving.data.model.Notification
import com.example.codriving.data.model.RequestNotification
import com.example.codriving.data.repository.UploadCarRepository
import com.example.codriving.data.repository.UserRepository
import com.google.firebase.Timestamp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val uploadCarRepository: UploadCarRepository
) :
    ViewModel() {

    private val _notificationsThisWeek = MutableLiveData<MutableList<Notification>>(mutableListOf())
    private val _notificationsLastWeek = MutableLiveData<MutableList<Notification>>(mutableListOf())
    private val _usersNotifications = MutableLiveData<HashMap<String, RequestNotification>>()
    var resfresh by mutableStateOf(false)
    private val _isLoading = MutableLiveData(true)
    val notificationsThisWeek: LiveData<MutableList<Notification>> get() = _notificationsThisWeek
    val notificationsLastWeek: LiveData<MutableList<Notification>> get() = _notificationsLastWeek
    val userNotification: LiveData<HashMap<String, RequestNotification>> get() = _usersNotifications

    val currentTime = Timestamp.now()
    val isLoading: LiveData<Boolean> get() = _isLoading

    init {


        viewModelScope.launch {
            getNotifications()
        }


    }


    private fun addNotification(notification: Notification) {
        val timeDifference =
            (currentTime.seconds - notification.timestamp!!.seconds) / (60 * 60 * 24) // diferencia en días

        if (timeDifference <= 7) {
            _notificationsThisWeek.value?.add(notification)
            _notificationsThisWeek.value = _notificationsThisWeek.value
        } else {
            _notificationsLastWeek.value?.add(notification)
            _notificationsLastWeek.value = _notificationsLastWeek.value
        }
    }

    private suspend fun getUserByNotification(notification: Notification) {
        val user = userRepository.getUserById(notification.idSender)
        val car = uploadCarRepository.getCarById(notification.idProduct!!)

        if (user != null) {
            _usersNotifications.value = _usersNotifications.value ?: HashMap()
            _usersNotifications.value!![notification.idSender] = RequestNotification(car, user)
        }
    }

    private suspend fun getNotifications() {
        val auxThisWeek = mutableListOf<Notification>()
        val auxLastWeek = mutableListOf<Notification>()

        val fetchedNotifications = userRepository.getNotifications()
        fetchedNotifications.forEach { notification ->

            val timeDifference =
                (currentTime.seconds - notification.timestamp!!.seconds) / (60 * 60 * 24) // diferencia en días

            if (timeDifference <= 7) {
                auxThisWeek.add(notification)
            } else {
                auxLastWeek.add(notification)
            }

            if (notification.type == 1) {
                getUserByNotification(notification)

            }
        }
        _notificationsThisWeek.value!!.clear()
        _notificationsLastWeek.value!!.clear()

        _notificationsThisWeek.value = auxThisWeek
        _notificationsLastWeek.value = auxLastWeek

        _isLoading.value = false
    }

    suspend fun setRefresh() {
        resfresh = true
        getNotifications()
        resfresh = false
    }

    fun removeNotifyThisWeek(it: Notification) {
        userRepository.removeNotify(it)
        _notificationsThisWeek.value!!.remove(it)
    }

    fun removeNotifyLastWeek(it: Notification) {
        _notificationsLastWeek.value!!.remove(it)
    }

}