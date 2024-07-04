package com.example.codriving.view.AddressAutoComplete

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties

@Composable
fun AddressAutocompleteScreen(
    labelHolder: String,
    returnQuery: (String) -> Unit,
    showState: Boolean = true,
    viewModel: AddressAutocompleteViewModel,

    ) {

    var query by remember { mutableStateOf("") }
    val suggestions by viewModel.suggestions
    var menuExpanded by remember { mutableStateOf(false) }
    var loading by viewModel.loading
    val searchSuccess by viewModel.searchSuccess

    LaunchedEffect(query) {
        if (query.isNotBlank()) {
            viewModel.fetchAddressSuggestions(query)
        } else {
            // Clear suggestions if query is empty
            viewModel.clearSuggestions()
        }
        returnQuery(query)
    }

    Column {
        OutlinedTextField(
            value = query,
            onValueChange = {
                query = it
                menuExpanded = it.isNotEmpty() // Expandir el menú si hay algo en el TextField
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(labelHolder) },
            placeholder = { Text("Search address") },
            leadingIcon = {
                Icon(imageVector = Icons.Default.LocationOn, contentDescription = "Location")
            },
            trailingIcon = {
                if (showState) {
                    if (loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                        )
                    } else if (viewModel.suggestions.value.isEmpty()) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Search successful",
                            modifier = Modifier.size(24.dp)
                        )
                    } else if (searchSuccess) {
                        Icon(
                            imageVector = Icons.Default.Done,
                            contentDescription = "Search successful",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            },

            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = {
            })

        )
        DropdownMenu(
            properties = PopupProperties(focusable = false),
            expanded = viewModel.suggestions.value.isNotEmpty() && query.isNotEmpty(),
            onDismissRequest = { viewModel.clearSuggestions() },
            modifier = Modifier
                .height(200.dp)
        )
        {
            suggestions.forEach { streetName ->
                DropdownMenuItem(text = { Text(text = streetName) },
                    onClick = {
                        query = streetName // Set the selected street name to the query
                        viewModel.clearSuggestions()
                    })
            }
        }
    }
}

