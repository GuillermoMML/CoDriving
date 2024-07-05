package com.example.codriving.view.ServicePage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.codriving.data.model.Car
import com.example.codriving.data.model.RentCars
import com.example.codriving.data.model.User
import com.example.codriving.data.repository.UploadCarRepository
import com.example.codriving.data.repository.UserRepository
import com.google.firebase.firestore.DocumentReference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookRentViewModel @Inject constructor(
    private val uploadCarRepository: UploadCarRepository,
    private val userRepository: UserRepository,
) : ViewModel() {
    private var _car = Car()
    private var _carId = String()

    private val _phone = MutableLiveData("")
    val phone: LiveData<String> get() = _phone

    private var _listofRent = MutableStateFlow(hashMapOf<DocumentReference, RentCars>())
    val listofRent: StateFlow<HashMap<DocumentReference, RentCars>> = _listofRent

    private val _name = MutableLiveData("")
    val name: LiveData<String> get() = _name

    private val _location = MutableLiveData<String>()
    val location: MutableLiveData<String> get() = _location


    private val _email = MutableLiveData("")
    val email: LiveData<String> get() = _email

    private val _username = MutableLiveData("")
    val username: LiveData<String> get() = _username

    private val _currentUser = MutableLiveData(User())
    val currentUser: LiveData<User> get() = _currentUser

    private val _loading = MutableLiveData<Boolean>(false)
    val loading: LiveData<Boolean> get() = _loading

    private val _enable = MutableLiveData(false)
    val enable: LiveData<Boolean> get() = _enable

    suspend fun loadDetails(carId: String) {
         _car = uploadCarRepository.getCarById(carId)
        _carId = carId
        _listofRent.value =

            uploadCarRepository.getRentsByCar(_car.rentCars)
        _currentUser.value = userRepository.getUserById(userRepository.auth.currentUser!!.uid)
        _name.value = currentUser.value!!.fullName
        _phone.value = currentUser.value!!.phone
        _email.value = currentUser.value!!.email
        _location.value = currentUser.value!!.location
    }

    fun updateFields(name: String, username: String, phone: String, email: String) {
        _name.value = name
        _username.value = username
        _phone.value = phone
        _email.value = email


    }

    fun sendData(user: User,rentsCar: MutableSet<DocumentReference>, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        // Lanzar una corutina para llamar a sendNotifyRequest
        _loading.value = true
        viewModelScope.launch {
            try {
                sendNotifyRequest(user,rentsCar, onSuccess, onFailure)

            } finally {
                _loading.value = false

            }
        }

    }

    //Funcion que se llama al realizar una petición por un alquiler
     private  fun sendNotifyRequest(
        user: User,
        rentsCar: MutableSet<DocumentReference>,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {

        _loading.value = true
        val ownerId = _car.owner!!.id

        userRepository.productNotify("${user.fullName} esta interesado es tu coche ${_car.model}",_carId,rentsCar,1,ownerId,onSuccess, onFailure)
    }


}