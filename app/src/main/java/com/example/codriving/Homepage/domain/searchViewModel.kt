package com.example.codriving.Homepage.domain

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SearchViewModel : ViewModel() {

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching

    private val _searchText = MutableStateFlow("")
    val searchText: StateFlow<String> = _searchText

    private val _searchResults = MutableStateFlow(listOf<String>())
    val searchResults: StateFlow<List<String>> = _searchResults

    fun onSearchTextChanged(text: String) {
        _searchText.value = text
    }

    fun onSearchButtonClicked() {
        _isSearching.value = true

        // Implementar la lógica de la búsqueda

        _isSearching.value = false
    }
}

