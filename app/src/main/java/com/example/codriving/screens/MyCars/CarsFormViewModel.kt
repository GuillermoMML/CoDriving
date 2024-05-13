package com.example.codriving.screens.MyCars

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.codriving.data.model.Car
import com.example.codriving.data.model.Year
import com.example.codriving.data.repository.FirebaseStorageRepository
import com.example.codriving.data.repository.UploadCarRepository
import com.example.codriving.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class CarsFormViewModel @Inject constructor(private val uploadCarRepository: UploadCarRepository,private val firebaseStorageRepository: FirebaseStorageRepository,
private val userRepository: UserRepository) : ViewModel() {
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _validateField = MutableLiveData(false)
    val validateField: LiveData<Boolean> get() = _validateField

    private val _plate = MutableLiveData("")
    val plate: LiveData<String> get() = _plate


    private val _mileageState = MutableLiveData(0)
    val mileageState: LiveData<Int> get() = _mileageState

    private val _marcaList = MutableStateFlow<List<String>>(emptyList())
    val marcaList: StateFlow<List<String>> = _marcaList

    private val _modelList = MutableStateFlow<List<String>>(emptyList())
    val modelList: StateFlow<List<String>> = _modelList

    private val _selectMarca = MutableLiveData("Volkswagen")
    val selectMarca: LiveData<String> get() = _selectMarca

    private val _selectedYear = MutableLiveData<Year>()
    val selectedYear: LiveData<Year> = _selectedYear

    private val _selectModel = MutableLiveData("")
    val selectModel: LiveData<String> get() = _selectModel

    private val _errorMessage = MutableLiveData<String>()

    private val _uploadStatus = MutableLiveData(false)
    val uploadStatus: LiveData<Boolean> get() = _uploadStatus

    private val _isError = MutableLiveData(false)

    private val _car = MutableLiveData<Car?>()
    val car: LiveData<Car?> get() = _car


    init {
        _selectedYear.value = Year(Calendar.getInstance().get(Calendar.YEAR))
        getMarks()
        setMarca(_selectMarca.value!!)
    }
    suspend fun loadCarDetails(id: String?) {
        _car.value = uploadCarRepository.getCarById(id!!)
        _plate.value = _car.value!!.plate
        _selectMarca.value = car.value!!.brand
        _mileageState.value = car.value!!.kilometers
        _selectModel.value = car.value!!.model

    }

    fun setSelectedYear(year: Year) {
        _selectedYear.value = year
    }



    fun setMarca(modelo: String) {
        _selectMarca.value = modelo
        getModels()
    }

    private fun getMarks() = viewModelScope.launch {
        try {
            _marcaList.value = uploadCarRepository.getMarks()

        } catch (E: Exception) {
            _errorMessage.value = "No se encontraron marcas de coches"
        }
    }

    private fun getModels() = viewModelScope.launch {
        try {
            _modelList.value = uploadCarRepository.getModelsByMark(_selectMarca.value.toString())
            _selectModel.value = _modelList.value[0]
        } catch (e: Exception) {
            _errorMessage.value = "No se econtraron modelos para esa marca"
        }
    }

    fun uploadCar(imageUris: List<Uri>) {
        _isLoading.value = true
        viewModelScope.launch {
            val uploadedImageUrls = mutableListOf<String>() // Contenedor para las URLs de las imágenes subidas

            try {
                for (imageUri in imageUris) {
                    val downloadUri = firebaseStorageRepository.uploadImage(imageUri).await()
                    // Procesa la URL de descarga de la imagen si es necesario
                    uploadedImageUrls.add(downloadUri.toString())
                }
                val newCar = Car(
                    plate = _plate.value!!,
                    brand = _selectMarca.value!!,
                    image = uploadedImageUrls,
                    kilometers = _mileageState.value!!,
                    owner = userRepository.getUserReferenceById(userRepository.auth.uid!!),
                    model = _selectModel.value!!,
                    year = _selectedYear.value!!.value.toString(),
                    rentCars = emptyList()
                )

                uploadCarRepository.createCar(newCar)
                _isLoading.value = false
                _uploadStatus.postValue(true) // Todas las imágenes se subieron correctamente
            } catch (e: Exception) {
                _uploadStatus.postValue(false) // Error al subir al menos una imagen
            }
        }
    }


    fun getListModel(): List<String> {
        return _modelList.value
    }


    fun setMilesState(newMiles: Int){
        _mileageState.value = newMiles
    }

    fun getListMarks(): List<String> {
        return _marcaList.value
    }

    fun setModel(model: String) {
        _selectModel.value = model
    }

    fun setPlate(newPlate: String){
        validareFields()
        _plate.value = newPlate
    }
    fun getPlate(): String? {
        return _plate.value
    }
    fun getBooleanMatricula(): Boolean? {
        return _isError.value
    }

    fun esMatriculaValida(matricula: String) {
        // Patrón para matrículas españolas
        val regex = Regex("[0-9]{4}[A-Za-z]{3}")

        // Validar si la matrícula coincide con el patrón
        _isError.value = !regex.matches(matricula)

    }

    fun validareFields() {
        val isError = _isError.value!!
        val selectMarcaValue = !selectMarca.value.isNullOrEmpty()
        val selectedYearValue = selectedYear.value != null
        val selectModelValue = !selectModel.value.isNullOrEmpty()

        _validateField.value = isError && selectMarcaValue && selectedYearValue && selectModelValue
    }


}

