package com.example.codriving.view.ProfilePage

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.codriving.data.model.Contracts
import com.example.codriving.data.model.RentCars
import com.example.codriving.data.model.RequestContracts
import com.example.codriving.data.model.User
import com.example.codriving.data.repository.UploadCarRepository
import com.example.codriving.data.repository.UserRepository
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.storage.storage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val uploadCarRepository: UploadCarRepository
) :
    ViewModel() {
    private val _listOfConstracts = MutableLiveData(HashMap<String, RequestContracts>())
    val contracts: LiveData<HashMap<String, RequestContracts>> get() = _listOfConstracts

    private val _fullname = MutableLiveData<String?>()
    val fullname: LiveData<String?> get() = _fullname

    private val _phone = MutableLiveData("")
    val phone: LiveData<String> get() = _phone

    private val _location = MutableLiveData<String?>()
    val location: MutableLiveData<String?> get() = _location

    private val _email = MutableLiveData<String?>()
    val email: LiveData<String?> get() = _email

    private val _errorMessage = MutableLiveData<String>()

    private val _profileImage = MutableLiveData<String>()
    val profileImage: LiveData<String> get() = _profileImage
    val errorMessage: LiveData<String> get() = _errorMessage

    private val _currentUser = MutableLiveData<User>(null)
    val currentUser: MutableLiveData<User> get() = _currentUser


    private val _loading = MutableLiveData<Boolean>()
    val loading: MutableLiveData<Boolean> get() = _loading
    private val _chagingPasswordResult = MutableLiveData<String>()
    val chagingPasswordResult: LiveData<String> get() = _chagingPasswordResult

    private val _profileUri = MutableLiveData<Uri?>()
    val profileUri: LiveData<Uri?> get() = _profileUri

    private val _loadingProgrees = MutableLiveData(false)

    init {
        viewModelScope.launch {
            val auth = Firebase.auth.uid
            _currentUser.value = userRepository.getUserById(auth!!)
            _fullname.value = currentUser.value?.fullName ?: ""
            _email.value = currentUser.value?.email ?: ""
            _phone.value = currentUser.value?.phone ?: ""
            _location.value = currentUser.value?.location ?: ""
            _profileImage.value =
                if (currentUser.value?.imageProfile!!.isEmpty()) "" else currentUser.value?.imageProfile!!

            var listOfContracts = emptyList<Contracts>()

            userRepository.getContractsByUser(
                auth,
                onSuccess = { listOfContracts = it },
                onFailure = { _errorMessage.value = it.message }
            )
            if (listOfContracts.isNotEmpty()) {

                listOfContracts.forEach { it ->
                    val rentCars = mutableListOf<RentCars>()  // Use a mutable list
                    it.rentCars.forEach {
                        rentCars.add(uploadCarRepository.getCarRentByReference(it!!)!!)
                        Log.d("Rentas: ", rentCars.toString())
                    }
                    val requestContracts = RequestContracts(
                        car = uploadCarRepository.getCarById(it.idProduct!!),
                        owner = userRepository.getUserById(it.idReceiver!!)!!,
                        client = userRepository.getUserById(it.idSender)!!,
                        rentCars = rentCars,
                        expiredDate = it.timestamp!!
                    )
                    _listOfConstracts.value?.set(it.idContracts!!, requestContracts)
                    Log.d("sizeeeeeeeeee", _listOfConstracts.value!!.size.toString())
                }
            }
            _loading.value = false

        }
    }

    fun setChanges(email: String, location: String, fullname: String, phone: String) {
        _email.value = email
        _fullname.value = fullname
        _phone.value = phone
        _location.value = location
    }

    suspend fun setCurrentChanges(password: String) {
        _loadingProgrees.value = true
        _profileUri.value.let {
            if (it != null) {
                uploadImage(uri = it, onSuccess = {
                    _profileImage.value = it
                }, onFailure = { it })
            }
        }
        userRepository.modifyUser(
            fullname.value!!,
            email.value!!,
            phone.value!!,
            location.value!!,
            profileImage.value!!,
            password,
            onSuccess = { _chagingPasswordResult.value = "Usuario actualizado existosamento" },
            onFailure = { _chagingPasswordResult.value = it.message }
        )
        _loadingProgrees.value = false
    }

    fun setImageUri(uri: Uri) {
        _profileUri.value = uri
    }

    suspend fun uploadImage(uri: Uri, onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit) {
        try {
            val storageRef =
                Firebase.storage.reference.child("profileImages/${uri.lastPathSegment}")
            val uploadTask = storageRef.putFile(uri).await()
            val downloadUrl = storageRef.downloadUrl.await()
            onSuccess(downloadUrl.toString())
        } catch (e: Exception) {
            onFailure(e)
        }
    }

    fun addReview(
        contractToReview: RequestContracts,
        review: String,
        rating: Double,
        onComplete: (String) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        userRepository.addReviewService(
            contractToReview,
            review,
            rating,
            onComplete = { onComplete(it) },
            onFailure = { onFailure(it) })
    }

}