package com.example.codriving.view.notificationPage

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.FileProvider
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.codriving.common.generatefilePathPDF
import com.example.codriving.data.model.Notification
import com.example.codriving.data.model.RentCars
import com.example.codriving.data.model.RequestNotification
import com.example.codriving.data.repository.FirebaseStorageRepository
import com.example.codriving.data.repository.UploadCarRepository
import com.example.codriving.data.repository.UserRepository
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.File
import java.text.DateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val uploadCarRepository: UploadCarRepository,
    private val firebaseStorageRepository: FirebaseStorageRepository
) :
    ViewModel() {

    private val _notificationsThisWeek = MutableLiveData<MutableList<Notification>>(mutableListOf())
    private val _notificationsLastWeek = MutableLiveData<MutableList<Notification>>(mutableListOf())
    private val _startDates = MutableLiveData<MutableList<String>>(mutableListOf())
    private val _endDates = MutableLiveData<MutableList<String>>(mutableListOf())
    private val _usersNotifications = MutableLiveData<HashMap<String, RequestNotification>>()
    private val _isLoading = MutableLiveData(true)
    private val _error = MutableLiveData("")
    private val _pdfUri = MutableLiveData<Uri?>()
    val pdfUri: LiveData<Uri?> get() = _pdfUri


    private var currentNotify = Notification()
    private val _currentNotifies = MutableLiveData<MutableList<DocumentReference>>(mutableListOf())
    val error: LiveData<String> get() = _error
    var resfresh by mutableStateOf(false)
    val notificationsThisWeek: LiveData<MutableList<Notification>> get() = _notificationsThisWeek
    val notificationsLastWeek: LiveData<MutableList<Notification>> get() = _notificationsLastWeek
    val startDates: LiveData<MutableList<String>> get() = _startDates
    val endDates: LiveData<MutableList<String>> get() = _endDates

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
            _usersNotifications.value!![notification.idNotification.toString()] =
                RequestNotification(car, user)
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


    suspend fun removeNotify(cause: String?, notSendMessage: Boolean? = false) {
        // Iterar usando un iterador explícito
        val iteratorThisWeek = _notificationsThisWeek.value!!.iterator()
        while (iteratorThisWeek.hasNext()) {
            val notification = iteratorThisWeek.next()
            if (notification.idNotification == currentNotify.idNotification) {
                if (currentNotify.type == 2 || currentNotify.type == 0) {
                    userRepository.removeNotify(currentNotify)
                } else {
                    if (notSendMessage == true) {
                        userRepository.removeNotify(currentNotify)
                    } else {
                        userRepository.removeNotifyType1(
                            currentNotify,
                            cause,
                            onSuccess = { Log.d("Success", "Existo al borrar") },
                            onFailure = { _error.value = it }
                        )

                    }
                }

                iteratorThisWeek.remove()
            }
        }

        // Iterar usando un iterador explícito para la otra lista
        val iteratorLastWeek = _notificationsLastWeek.value!!.iterator()
        while (iteratorLastWeek.hasNext()) {
            val notification = iteratorLastWeek.next()
            if (notification.idNotification == currentNotify.idNotification) {
                if (currentNotify.type == 2) {
                    userRepository.removeNotify(currentNotify)
                } else {
                    userRepository.removeNotifyType1(
                        currentNotify,
                        cause,
                        onSuccess = { Log.d("Success", "Existo al borrar") },
                        onFailure = { _error.value = it }
                    )

                }

                iteratorLastWeek.remove()
            }
        }
    }

    suspend fun acceptNotify(cause: String?, notify: Notification) {
        try {
            userRepository.acceptNotify(
                removeNotify = notify,
                cause!!.toString(),
                notify.idProduct!!,
                notify.rentsCars.toMutableSet(),
                notify.idSender,
            )
            notify.rentsCars.forEach {
                uploadCarRepository.busyRentCar(it)
            }

            //Eliminamos la notificación
            setCurrentNotify(notify)
            removeNotify("", true)

        } catch (e: Exception) {
            _error.value = e.message
        }
    }


    fun setCurrentNotify(it: Notification) {
        currentNotify = it
    }

    fun setError() {
        _error.value = String()
    }

    suspend fun getRentsString() {
        val formatter = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault())

        val startDates = mutableListOf<String>()
        val endDates = mutableListOf<String>()

        _currentNotifies.value!!.forEach {
            val rent = uploadCarRepository.getCarRentByReference(it)
            rent?.startDate?.toDate()?.let { it1 -> startDates.add(formatter.format(it1)) }
            rent?.endDate?.toDate()?.let { it1 -> endDates.add(formatter.format(it1)) }
        }
        _startDates.value = startDates
        _endDates.value = endDates
    }


    fun setCurrentRentsNotify(list: List<DocumentReference>) {
        _currentNotifies.value = list.toMutableList()
    }


    suspend fun generateModelPDF(context: Context, notify: Notification): String? {
        val owner = userRepository.getUserById(notify.idReceiver.toString())!!
        val client = userNotification.value!![notify.idNotification]!!.user
        val car = userNotification.value!![notify.idNotification]!!.car
        val listOfRents = mutableListOf<RentCars>()  // Inicializa la lista mutable
        notify.rentsCars.forEach {
            val rentCar = uploadCarRepository.getCarRentByReference(it)
            if (rentCar != null) {
                listOfRents.add(rentCar)  // Agrega el elemento a la lista
            }
        }

        val auxPDF = generatefilePathPDF(context, owner, client, car, listOfRents)

        if (auxPDF != null) {
            uploadPDFStorage(auxPDF, notify)
        }
        return auxPDF
    }

    private fun uploadPDFStorage(auxPDF: String, notify: Notification) {
        firebaseStorageRepository.uploadPDF(auxPDF, notify)
    }

    fun generateContract(notification: Notification) {
        var lastEndDate = Timestamp(Date())
        notification.rentsCars.forEach {
            it.get().addOnSuccessListener {
                if (it["endDate"] as Timestamp >= lastEndDate) {
                    lastEndDate = it["endDate"] as Timestamp
                }
            }
        }

        userRepository.generateContract(lastEndDate, notification)
    }

    fun downloadPDF(pdfName: String, onSuccess: (File) -> Unit, onFailure: (Exception) -> Unit) {
        viewModelScope.launch {
            try {
                firebaseStorageRepository.downloadPDF(pdfName, onSuccess = {
                    onSuccess(it)
                }, onFailure)
            } catch (e: Exception) {
                onFailure(e)
            }
        }
    }

    fun openPDF(context: Context, pdfFile: File) {
        val pdfUri = FileProvider.getUriForFile(
            context,
            context.applicationContext.packageName + ".provider",
            pdfFile
        )

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(pdfUri, "application/pdf")
            flags = Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_GRANT_READ_URI_PERMISSION
        }
        context.startActivity(intent)
    }

}
