package com.example.codriving.view.LoginPage.ui

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.codriving.data.model.LoggedInUser
import com.example.codriving.data.repository.FirebaseAuthRepository
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val repository: FirebaseAuthRepository) :
    ViewModel() {

    private val _loginState =
        MutableLiveData(LoggedInUser(isLoggedIn = false, userId = "", authToken = ""))
    val loginState: LiveData<LoggedInUser> get() = _loginState

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
    private val _isLoading = MutableLiveData(true)
    val isLoading: LiveData<Boolean> get() = _isLoading


    private fun isLoggedIn(): Boolean {
        if (repository.getCurrentUser() != null) {
            return true
        }
        return false
    }


    fun getLogged() {
        viewModelScope.launch(Dispatchers.IO) {
            val isUserLoggedIn = isLoggedIn()

            withContext(Dispatchers.Main) {
                val currentUser = repository.getCurrentUser()
                if (currentUser != null) {
                    val userId = currentUser.uid
                    val authToken = currentUser.getIdToken(true).toString()

                    if (isUserLoggedIn) {
                        // User is logged in, update login state
                        _loginState.value = LoggedInUser(true, userId, authToken)
                        _isLoading.value = false
                    }

                } else {
                    _isLoading.value = false
                }


            }
        }

    }

    init {

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
                            _loginState.value!!.authToken =
                                auth.currentUser?.getIdToken(true).toString()
                            _loginState.value!!.isLoggedIn = true
                            _loginState.value!!.userId = auth.currentUser!!.uid
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

    fun clearError() {
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