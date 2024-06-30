package com.example.codriving.view.ChatPage

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.codriving.data.model.Conversations
import com.example.codriving.data.model.Message
import com.example.codriving.data.model.User
import com.example.codriving.data.repository.MessagesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConversationsViewModel @Inject constructor(private val messagesRepository: MessagesRepository) :
    ViewModel() {
    private val _loading = MutableLiveData(true)
    val loading: LiveData<Boolean> = _loading

    private val _message = MutableStateFlow<List<Message>>(emptyList())
    val message: StateFlow<List<Message>> get() = _message

    private var _conversations = MutableLiveData<Map<String, Conversations>>(emptyMap())
    val conversations: LiveData<Map<String, Conversations>> = _conversations

    private val _profiles = MutableLiveData<Map<String, User>>(emptyMap())
    val profiles: LiveData<Map<String, User>> get() = _profiles

    private val _errorMessage = MutableLiveData("")

    val error: MutableLiveData<String> get() = _errorMessage


    override fun onCleared() {
        super.onCleared()
        // Unregister the listener when the ViewModel is destroyed
        messagesRepository.getMessagesListener()?.remove()
    }

    init {
        viewModelScope.launch {
            messagesRepository.getChatAvailable(onSuccess = {
                updateMessages(it)
                loadProfile(it)
                _loading.value = false
            }, onError = {
                Log.d("awdawdawdada", it.message!!)
                _errorMessage.value = it.message
            }
            )

        }
    }

    private fun loadProfile(it: Map<String, Conversations>) {

        messagesRepository.getProfilesMessages(it,
            onSuccess = {
                _profiles.value = it
            },
            onError = {
                it.message?.let { it1 -> Log.d("ERROR en cargar perfiles", it1) }
                _errorMessage.value = it.toString()
            }
        )
    }

    private fun updateMessages(list: Map<String, Conversations>) {
        _conversations.value = list
    }


}
