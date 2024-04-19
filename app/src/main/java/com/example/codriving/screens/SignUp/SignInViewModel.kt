package com.example.codriving.screens.SignUp

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.codriving.data.User
import com.example.codriving.data.repository.FirebaseAuthRepository
import com.example.codriving.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(private val repository: FirebaseAuthRepository,private val userRepository: UserRepository) : ViewModel() {

   private val _fullname = MutableLiveData<String>()
    val fullname: LiveData<String> get() = _fullname

    private val _phone = MutableLiveData("")
    val phone: LiveData<String> get() = _phone

    private val _location = MutableLiveData(null)
    val location: MutableLiveData<Nothing?> get() = _location

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage


    private val _email = MutableLiveData<String>()
    val email: LiveData<String> get() = _email

    private val _password = MutableLiveData<String>()
    val password: LiveData<String> get() = _password

    private val _loginEnable = MutableLiveData<Boolean>()
    val loginEnable: LiveData<Boolean> get() = _loginEnable


    private val _isLoading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _isLoading

    suspend fun signIn(email: String, password: String, onComplete: (Boolean) -> Unit) {
        _isLoading.value = true
        try{
            repository.signUp(email, password)
            val currentUser = repository.getCurrentUser()
            if (currentUser != null) {
                val newUser = User(
                    fullName = _fullname.value,
                    email = email,
                    phone = _phone.value,
                    location = _location.value,
                    rentalHistory = emptyList(),
                    ratings = emptyList()
                )
                userRepository.createUser(newUser, currentUser.uid)
                _errorMessage.value = "Se inició sesión exitosamente"
                onComplete(true)
            }else{
                _errorMessage.value = "No se pudo crear el usuario intentar de nuevo"
                onComplete(false)
            }
        }catch (e:Exception){
            _errorMessage.value = e.message
            onComplete(false)
        }finally {
            _isLoading.value = false
        }

    }



    fun onSignChange(email: String, password: String,fullname:String,phone:String) {
        _email.value = email
        _password.value = password
        _loginEnable.value = isValidEmail(email) && isValidPassword(password)
        _fullname.value = fullname
        _phone.value = phone

    }

    private fun isValidEmail(email: String): Boolean =
        Patterns.EMAIL_ADDRESS.matcher(email).matches()


    private fun isValidPassword(password: String): Boolean = password.length >= 6


}

