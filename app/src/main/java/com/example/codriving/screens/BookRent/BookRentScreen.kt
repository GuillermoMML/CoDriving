package com.example.codriving.screens.BookRent

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AddAlert
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.codriving.common.AlertDialogExample
import com.example.codriving.common.HeaderPopBack
import com.example.codriving.data.model.User
import com.example.codriving.screens.MyCars.ImageUploadScreen
import com.example.codriving.screens.RentCar.ExpandableItem
import com.google.firebase.firestore.DocumentReference
import java.text.DateFormat
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.N)
@Composable
fun BookRentScreen(
    navController: NavController,
    viewModel: BookRentViewModel,
) {
    val showModal = remember {
        mutableStateOf(false)
    }
    val failureMessage = remember {
        mutableStateOf("")
    }
    val listOfRent = viewModel.listofRent.collectAsState()
    val name = viewModel.name.observeAsState()
    val username = viewModel.username.observeAsState()
    val email = viewModel.email.observeAsState()
    var selectedImageUris by remember { mutableStateOf(emptyList<Uri>()) }
    val enableSend = mutableStateOf(false)
    val phone = viewModel.phone.observeAsState()
    val rentTimes = remember {
        mutableStateOf(false)
    }
    var selectedRentCars by remember { mutableStateOf<MutableSet<DocumentReference>>(mutableSetOf()) }
    val checked = remember {
        mutableStateOf(false)
    }
    val dateFormat = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault())

    if (showModal.value) {
        if (failureMessage.value.isEmpty()) {
            Dialog(onDismissRequest = { showModal.value = false }) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(16.dp),
                    shape = RoundedCornerShape(16.dp),
                ) {
                    Text(
                        text = "Petición enviada con exito",
                        modifier = Modifier
                            .fillMaxSize()
                            .wrapContentSize(Alignment.Center),
                        textAlign = TextAlign.Center,
                    )
                }
            }

        } else {
            AlertDialogExample(
                onDismissRequest = { showModal.value = false },
                onConfirmation = {
                    showModal.value = false
                },
                dialogTitle = "Fallo al enviar la petición",
                dialogText = failureMessage.value,
                icon = Icons.Default.AddAlert
            )

        }

    }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            HeaderPopBack(navController = navController)
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    enabled = selectedRentCars.isNotEmpty(),
                    onClick = {
                        enableSend.value = false
                        val user = User(
                            fullName = viewModel.name.value, email = viewModel.email.value,
                            phone = viewModel.phone.value,
                            location = viewModel.location.value
                        )

                        viewModel.sendData(
                            user, selectedRentCars,
                            onSuccess = {
                                showModal.value = true
                            },
                            onFailure = {
                                failureMessage.value = it
                                showModal.value = true
                            })
                    }) {
                    Row(horizontalArrangement = Arrangement.Center) {
                        Text(text = "Send Request", Modifier.padding(end = 5.dp))
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null)

                    }
                }

            }

        },
        content = { paddingValues ->
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                LazyColumn(
                    modifier = Modifier
                        .width(300.dp)
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
                                    phone.value!!,
                                    email.value!!
                                )
                            })
                    }
                    item {
                        OutlinedTextField(value = username.value!!,
                            label = { Text(text = "surname") },
                            onValueChange = {
                                viewModel.updateFields(
                                    name.value!!,
                                    it,
                                    phone.value!!,
                                    email.value!!
                                )
                            })
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
                                    it,
                                    email.value!!
                                )
                            })

                    }
                    item {
                        OutlinedTextField(
                            value = email.value!!, label = { Text(text = "Email") }, leadingIcon = {
                                Icon(
                                    Icons.Default.Email, contentDescription = null
                                )
                            },
                            onValueChange = {
                                viewModel.updateFields(
                                    name.value!!,
                                    username.value!!,
                                    phone.value!!,
                                    it
                                )
                            })

                    }
                    item {
                        Box {
                            ExpandableItem(
                                title = "Select availables days",
                                expanded = rentTimes,
                                onClick = { rentTimes.value = !rentTimes.value },
                                content = {
                                    listOfRent.value.forEach { (key, rentCar) ->
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Start
                                        ) {
                                            checked.value = selectedRentCars.contains(key)

                                            Checkbox(
                                                checked = checked.value,
                                                onCheckedChange = {
                                                    selectedRentCars =
                                                        if (it) selectedRentCars.plus(key) as MutableSet<DocumentReference> else selectedRentCars.minus(
                                                            key
                                                        ) as MutableSet<DocumentReference>
                                                },
                                                // null recommended for accessibility with screenreaders
                                            )
                                            Text(
                                                text = "${rentCar.pricePerDay}$: ${
                                                    dateFormat.format(
                                                        rentCar.startDate.toDate()
                                                    )
                                                }-${
                                                    dateFormat.format(
                                                        rentCar.endDate.toDate()
                                                    )
                                                }"
                                            )

                                        }

                                    }
                                })
                        }
                    }
                    item {
                        Column(
                            Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = "Identification Image")

                            ImageUploadScreen(selectedImageUris) { uri ->

                                if (uri != null) {
                                    // Si hay resultados de la selección de imágenes
                                    if (selectedImageUris.size < 2) {
                                        // Si hay menos de dos imágenes seleccionadas, simplemente agregamos los nuevos resultados
                                        selectedImageUris =
                                            selectedImageUris.toMutableList().apply { add(uri) }

                                    } else {
                                        // Si ya hay dos imágenes seleccionadas, las reemplazamos con los nuevos resultados
                                        selectedImageUris = emptyList()
                                        selectedImageUris =
                                            selectedImageUris.toMutableList().apply { add(uri) }

                                    }
                                }
                            }

                        }


                    }
                }

            }

        })
}