package com.example.codriving.view.AddressAutoComplete

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class AddressAutocompleteViewModel : ViewModel() {

    private val baseUrl = "https://geocode.xyz/?region=ES&geoit=json&streetname="

    val suggestions = mutableStateOf<List<String>>(emptyList())
    val loading = mutableStateOf(false)
    val searchSuccess = mutableStateOf(false)

    fun fetchAddressSuggestions(query: String) {

        if (query.isBlank()) {
            suggestions.value = emptyList()
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            loading.value = true
            try {
                val url = URL(baseUrl + query)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"

                val inputStream = connection.inputStream
                val bufferedReader = BufferedReader(InputStreamReader(inputStream))
                val response = StringBuilder()
                var inputLine: String?

                while (bufferedReader.readLine().also { inputLine = it } != null) {
                    response.append(inputLine)
                }

                bufferedReader.close()

                // Extraer los nombres de calles de la respuesta JSON
                val streetNames = extractStreetNames(response.toString())

                // Actualizar el estado de suggestions en el hilo principal
                suggestions.value = streetNames
                searchSuccess.value = true // Indicar que la búsqueda fue exitosa

            } catch (e: Exception) {
                suggestions.value = emptyList() // Manejo de errores, por ejemplo, lista vacía
                searchSuccess.value = false // Indicar que la búsqueda falló
                e.printStackTrace()
            } finally {
                loading.value = false
            }
        }
    }

    private fun extractStreetNames(response: String): List<String> {
        val streetNames = mutableListOf<String>()

        try {
            val jsonResponse = JSONObject(response)

            // Obtener el objeto "standard" que contiene las sugerencias de direcciones
            val standardObject = jsonResponse.optJSONObject("standard")

            // Verificar que el objeto "standard" no sea nulo
            standardObject?.let {
                // Obtener el objeto "street" dentro de "standard"
                val streetObject = it.optJSONObject("street")
                streetObject?.let { streets ->
                    // Iterar sobre las claves del objeto "street"
                    streets.keys().forEach { key ->
                        streetNames.add(key) // Agregar el nombre de la calle a la lista
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return streetNames
    }

    fun defaultSuggest() {
        suggestions.value = emptyList()
    }

    fun clearSuggestions() {
        suggestions.value = emptyList()
    }
}
