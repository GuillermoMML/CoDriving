package com.example.codriving.SignUp.ui

import androidx.lifecycle.ViewModel
import com.example.codriving.login.domain.SignInResult
import com.example.codriving.login.domain.SignInState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel

class SignInViewModel @Inject constructor(): ViewModel() {
    private val _state = MutableStateFlow(SignInState())
    val state = _state.asStateFlow()
    fun onSignInResult(result: SignInResult){
        _state.update { it.copy(
            isSignSucessful = result.data != null,
            signInError = result.errorMessage
        ) }
    }

    fun resetState(){
        _state.update { SignInState() }
    }
}