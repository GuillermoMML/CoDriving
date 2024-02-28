package com.example.codriving.Homepage.ui.home

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SearchViewModel : ViewModel() {

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching

    private val _searchText = MutableStateFlow("")
    val searchText: StateFlow<String> = _searchText

    fun onSearchTextChanged(text: String) {
        _searchText.value = text
    }

    fun onSearchButtonClicked() {
        _isSearching.value = true

        // Implementar la lógica de la búsqueda

        _isSearching.value = false
    }
}

