package com.example.codriving.screens.BookRent

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.codriving.common.HeaderPopBack
import java.text.SimpleDateFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookRentScreen(
    navController: NavController,
    viewModel: BookRentViewModel,
) {
    val listOfRent = viewModel.listofRent.collectAsState()
    val name = viewModel.name.observeAsState()
    val username = viewModel.username.observeAsState()
    val phone = viewModel.phone.observeAsState()

    val selectedDates = remember { mutableStateListOf<com.google.firebase.Timestamp>() }
    val dateFormat = SimpleDateFormat("dd/MM/yyyy")
    val expanded = remember {
        mutableStateOf(false)
    }
    var selectedDate by remember { mutableStateOf("") }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            HeaderPopBack(navController = navController)
        },
        content = { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(paddingValues),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),

                ) {
                item {
                    OutlinedTextField(value = name.value!!,
                        label = { Text(text = "Name") },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Person, contentDescription = null
                            )
                        },
                        onValueChange = {
                            viewModel.updateFields(
                                it,
                                username.value!!,
                                phone.value!!
                            )
                        })
                }
                item {
                    OutlinedTextField(value = username.value!!,
                        label = { Text(text = "UserName") },
                        onValueChange = { viewModel.updateFields(name.value!!, it, phone.value!!) })
                }
                item {
                    OutlinedTextField(
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                        value = phone.value!!, label = { Text(text = "Phone") }, leadingIcon = {
                            Icon(
                                Icons.Default.Call, contentDescription = null
                            )
                        },
                        onValueChange = {
                            viewModel.updateFields(
                                name.value!!,
                                username.value!!,
                                it
                            )
                        })

                }
                item {
                    OutlinedTextField(value = "",
                        label = { Text(text = "Location") },
                        leadingIcon = {
                            Icon(
                                Icons.Default.LocationOn, contentDescription = null
                            )
                        },
                        onValueChange = {})
                }

                item {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        OutlinedTextField(
                            enabled = false,
                            value = selectedDate,
                            label = { Text("Select Dates") },
                            onValueChange = { },
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                                disabledBorderColor = MaterialTheme.colorScheme.outline,
                                disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                //For Icons
                                disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),

                            modifier = Modifier
                                .clickable { expanded.value = true }
                                .padding(vertical = 8.dp)
                        )
                        DropdownMenu(
                            expanded = expanded.value,
                            onDismissRequest = { expanded.value = false }
                        ) {
                            listOfRent.value.forEach { date ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = "${dateFormat.format(date.startDate.toDate())} to ${
                                                dateFormat.format(date.endDate.toDate())
                                            }"
                                        )
                                    },
                                    onClick = {
                                        selectedDate =
                                            "${dateFormat.format(date.startDate.toDate())} to ${
                                                dateFormat.format(date.endDate.toDate())
                                            }"
                                        expanded.value = false
                                    }
                                )
                            }
                        }

                    }
                }

            }
        })
}