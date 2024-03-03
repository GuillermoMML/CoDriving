package com.example.codriving.LoginPage.ui

import android.util.Log
import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.codriving.LoginPage.domain.LoginViewState
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(): ViewModel() {

   // private val _loginViewState = MutableLiveData<LoginViewState>()
//    val loginViewState: LiveData<LoginViewState> get() = _loginViewState

    private val _email = MutableLiveData<String>()
    val email: LiveData<String> get() = _email
    private val _password = MutableLiveData<String>()
    val password: LiveData<String> get() = _password

    private val _loginEnable = MutableLiveData<Boolean>()
    val loginEnable: LiveData<Boolean> get() = _loginEnable

    private val _error = MutableLiveData<String>("")
    val error: LiveData<String> get() = _error

    private val auth: FirebaseAuth = Firebase.auth
    private val _clicked = MutableStateFlow(false)
    val clicked: MutableStateFlow<Boolean> get() = _clicked

    suspend fun isClicked() {
        viewModelScope.launch() {
            try {
                _clicked.value = true
                _clicked.value = false

            } catch (e: Exception) {
                Log.e("Login", "Error fetching RentCar data", e)

            }
        }
    }

    fun signInWithEmailAndPassword(
        email: String,
        password: String,
        HomePage: () -> Unit,
        showErrorMessage: (String) -> Unit, // Función para mostrar mensaje de error
    ) {
        viewModelScope.launch {
            try {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d("Existo","Inicio de sesion")
                            HomePage()
                        } else {
                            showErrorMessage("Usuario o contraseña incorrectos")
                        }
                    }
            } catch (e: Exception) {
                showErrorMessage("Error al iniciar sesión: ${e.message}")
            }
        }
    }

    fun clearError(){
        _error.value = ""
    }
    fun onLoginChange(email: String, password: String) {
        _email.value = email
        _password.value = password
        _loginEnable.value = isValidEmail(email) && isValidPassword(password)

    }

    private fun isValidEmail(email: String): Boolean =
        Patterns.EMAIL_ADDRESS.matcher(email).matches()


    private fun isValidPassword(password: String): Boolean = password.length >= 6
    fun onLoginSelected() {

    }


}