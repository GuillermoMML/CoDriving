package com.example.codriving.login.ui

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(): ViewModel() {
    var username by mutableStateOf("")
    var password by mutableStateOf("")
    private val _clicked = MutableStateFlow(false)
    val clicked: MutableStateFlow<Boolean> get() = _clicked

    suspend fun isClicked (){
        viewModelScope.launch() {
            try{
                _clicked.value = true
                delay(2000)
                _clicked.value = false

            }catch (e: Exception){
                Log.e("Login", "Error fetching RentCar data", e)

            }
        }
    }
    fun performLogin(): Boolean {
        // Aquí puedes agregar la lógica de validación de inicio de sesión
        return username == "usuario" && password == "contraseña"
    }

    fun login(username: TextFieldValue, password: TextFieldValue) {
        TODO("Not yet implemented")
    }
}
