package com.example.codriving.screens.notificationPage

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.codriving.common.generatefilePathPDF
import com.example.codriving.data.model.Notification
import com.example.codriving.data.model.RequestNotification
import com.example.codriving.data.repository.UploadCarRepository
import com.example.codriving.data.repository.UserRepository
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.util.Locale
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
    private var currentNotify = Notification()
    private var typeWeek = 0
    private val _error = MutableLiveData("")
    private val _infoMessage = MutableLiveData("")

    private val _currentNotifies = MutableLiveData<MutableList<DocumentReference>>(mutableListOf())
    val error: LiveData<String> get() = _error
    val infoMessage: LiveData<String> get() = _infoMessage

    var resfresh by mutableStateOf(false)
    private val _isLoading = MutableLiveData(true)
    val notificationsThisWeek: LiveData<MutableList<Notification>> get() = _notificationsThisWeek
    val notificationsLastWeek: LiveData<MutableList<Notification>> get() = _notificationsLastWeek
    val userNotification: LiveData<HashMap<String, RequestNotification>> get() = _usersNotifications

    val currentTime = Timestamp.now()
    val isLoading: LiveData<Boolean> get() = _isLoading

    val currentNotifies: LiveData<MutableList<DocumentReference>> get() = _currentNotifies


    init {


        viewModelScope.launch {
            getNotifications()
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

    suspend fun getNotifications() {
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

            getUserByNotification(notification)


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


    fun removeNotify(cause: String?) {

        if (currentNotify != Notification()) {
            _isLoading.value = true
            if (typeWeek == 1) {
                userRepository.removeNotifyType1(
                    currentNotify,
                    cause,
                    onSuccess = { Log.d("Sucess", "Existo al borrar") },
                    onFailure = { _error.value = it })
                _notificationsThisWeek.value!!.remove(currentNotify)
            } else if (typeWeek == 2) {
                userRepository.removeNotifyType2(currentNotify)
                _notificationsLastWeek.value!!.remove(currentNotify)

            }

        } else {
            _error.value = "No se encontró la notificación"

        }
        _isLoading.value = false

    }


    fun removeNotify(it: Notification, i: Int) {
        typeWeek = i
        currentNotify = it
        if (typeWeek == 2) {
            removeNotify("")
        }
    }

    fun setError() {
        _error.value = String()
    }

    suspend fun getRentsString() {
        val formatter = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault())

        // formatter.format(date)

        var message = ""
        _currentNotifies.value!!.forEach {
            val rent = uploadCarRepository.getCarRentByReference(it)
            message += (rent?.startDate?.toDate()?.let { it1 -> formatter.format(it1) })
            message += " " + (rent?.endDate?.toDate()?.let { it1 -> formatter.format(it1) }) + "\n"
        }
        _infoMessage.value = message

    }

    fun setCurretNotify(list: List<DocumentReference>) {
        _currentNotifies.value = list.toMutableList()
    }

    fun setInfoMessage(input: String? = "") {
        _infoMessage.value = input
    }

    fun generateModelPDF(context: Context): String? {

        return generatefilePathPDF(context)
    }
}