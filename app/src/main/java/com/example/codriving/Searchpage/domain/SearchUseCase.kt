package com.example.codriving.Searchpage.domain

interface SearchUseCase {
    suspend fun search(query: String): List<String>
}