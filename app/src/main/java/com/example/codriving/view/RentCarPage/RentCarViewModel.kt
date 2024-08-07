package com.example.codriving.view.RentCarPage

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.codriving.data.model.Car
import com.example.codriving.data.model.RentCars
import com.example.codriving.data.model.RentReview
import com.example.codriving.data.model.User
import com.example.codriving.data.repository.UploadCarRepository
import com.example.codriving.data.repository.UserRepository
import com.google.firebase.firestore.DocumentReference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RentCarViewModel @Inject constructor(private val uploadCarRepository: UploadCarRepository,private val userRepository: UserRepository
) : ViewModel() {
    private val _ownerUser = MutableLiveData(User())
    val ownerUser: LiveData<User> get() = _ownerUser


    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _rentCar = MutableLiveData<Car?>()
    val rentCar: LiveData<Car?> get() = _rentCar

    private var _listOfRents = MutableLiveData<Map<DocumentReference, RentCars>>(emptyMap())
    val listOfRents: LiveData<Map<DocumentReference, RentCars>> get() = _listOfRents

    private val _listOfReviews = MutableLiveData<Map<User, RentReview>>(emptyMap())
    val listOfReviews: LiveData<Map<User, RentReview>> get() = _listOfReviews

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error
    private var idCar = ""

    suspend fun loadData(id: String) {
        _rentCar.value = uploadCarRepository.getCarById(id)
        _listOfRents.value = uploadCarRepository.getRentsByCar(_rentCar.value!!.rentCars)
        _ownerUser.value = _rentCar.value!!.owner?.let { userRepository.documentSnapshotToUser(it) }

        //Obtenemos las reviews 3

        uploadCarRepository.getPreviewReviewByCar(id) { result ->
            if (result != null) {
                //Ahora obtenemos los usuarios
                _listOfReviews.value = result
            } else {
                _listOfReviews.value = emptyMap()
            }
        }





        _isLoading.value = true
        _error.value = null

    }

    suspend fun addConversation(conversation: List<String>) {
        userRepository.addConversation(conversation)
    }

    fun removeRentCar(referenceRentCar: DocumentReference, rentCar: RentCars) {
        viewModelScope.launch {
            try {
                uploadCarRepository.deleteRentCar(referenceRentCar, rentCar)

                // Si la eliminación es exitosa, actualiza la lista local
                _listOfRents.value =
                    _listOfRents.value?.filterNot { it.key == referenceRentCar }?.toMap()
            } catch (e: Exception) {
                // Manejar errores aquí
                Log.e("DeleteRentCar", "Error deleting rent car", e)
            }
        }

    }


}


