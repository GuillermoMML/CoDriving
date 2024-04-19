package com.example.codriving.screens.MyCars

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.codriving.common.PrecioTextField
import com.example.codriving.common.contentModalSheet
import com.example.codriving.common.getFormattedDateNoYear
import com.example.codriving.data.Car
import com.example.codriving.navigation.AppScreens
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ListMyCarsScreen(navController: NavHostController) {


    val viewModel: ListMyCarsViewModel = hiltViewModel()
    val listMyCarsState = viewModel.carListState.collectAsState()
    val isLoaded = viewModel.isLoaded.observeAsState()
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val bottomSheetState =
        rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    var selectStartDay by remember { mutableStateOf("") }
    var selectEndDay by remember { mutableStateOf("") }
    var finishedPublished by remember { mutableStateOf(false) }
    var showSnackbar by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }


    var currentCar by remember {
        mutableStateOf(HashMap<String, Car>())
    }
    var showModal by remember {
        mutableStateOf(
            false
        )
    }

    if (finishedPublished) {
        selectEndDay = ""
        selectStartDay = ""
        showModal = false
        finishedPublished = false
    }


    val expandedFab by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex == 0
        }
    }

    if (isLoaded.value!!) {
        contentModalSheet(
            bottomSheetState = bottomSheetState,
            onConfirmation = { startDay, endDay ->
                viewModel.setStartDay(startDay)
                viewModel.setEndDay(endDay)
            }) {

            if (showModal) {

                if (currentCar.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .zIndex(1F)
                            .background(Color.Black.copy(alpha = 0.6f)),
                    ) {
                        DialogWithImage(viewModel,
                            item = currentCar,
                            onDismissRequest = { showModal = false },
                            onConfirmation = {
                                coroutineScope.launch {
                                    if (viewModel.verifyPublishFields(idCar = it)) {
                                        finishedPublished = true
                                    } else {
                                        showSnackbar = true
                                    }
                                }
                            },
                            onShowDatePicker = {
                                coroutineScope.launch {
                                    bottomSheetState.show()
                                }

                            }
                        )

                    }
                }
            }

            Scaffold(
                snackbarHost = {
                    SnackbarHost(hostState = snackbarHostState)
                },
                floatingActionButton = {
                    ExtendedFloatingActionButton(
                        onClick = { navController.navigate(AppScreens.CarsFormScreen.route) },
                        expanded = expandedFab,
                        icon = { Icon(Icons.Filled.Add, "Localized Description") },
                        text = { Text(text = "Add more Cars") },
                    )
                },
                floatingActionButtonPosition = FabPosition.End,

                content = { paddingValues ->
                    if (showSnackbar) {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Rellene todos los campos")
                            showSnackbar = false
                        }
                    }
                    Box(
                        Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    ) {
                        if (listMyCarsState.value.isEmpty()) {
                            Box(
                                Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "No hay coches guardados")
                            }
                        } else {
                            LazyColumn(Modifier.fillMaxWidth()) {
                                listMyCarsState.value.forEach { cars ->
                                    item {
                                        previewCardsList(
                                            cars,
                                            viewModel,
                                            navController
                                        ) { bool, hasMap ->
                                            if (bool) {
                                                currentCar = hasMap
                                                showModal = true
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                })

        }
    } else {
        LoadScreen()
    }
}

/*Ventana que se abre al publicar un coche*/
@Composable
fun DialogWithImage(
    viewModel: ListMyCarsViewModel,
    item: HashMap<String, Car>,
    onDismissRequest: () -> Unit,
    onConfirmation: (String) -> Unit,
    onShowDatePicker: (Boolean) -> Unit,
) {
    var precio by remember { mutableStateOf("") }
    val startDay = viewModel.startDay.collectAsState()
    val endDay = viewModel.endDay.collectAsState()

    val actualCar = item.values.first()
    // Draw a rectangle shape with rounded corners inside the dialog
    Card(
        modifier = Modifier
            .height(500.dp)
            .zIndex(1f)
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
    ) {
        if (!viewModel.isLoadPublished.value!!) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {

                AsyncImage(
                    model = actualCar.image[0],
                    contentDescription = "Car Image",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .height(160.dp)
                )
                Text(
                    text = actualCar.model,
                    modifier = Modifier.padding(16.dp),
                )
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Row(
                        Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Price per Day: ")
                        PrecioTextField(
                            value = precio,
                            onValueChange = { nuevoPrecio ->
                                precio = nuevoPrecio
                                viewModel.updatePrice(nuevoPrecio)
                            }
                        )

                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {

                        TextButton(onClick = { onShowDatePicker(true) }) {
                            Text(text = "Select Dates")
                        }
                        if (endDay.value != null) {
                            Text(
                                "${getFormattedDateNoYear(startDay.value.time)}-${
                                    getFormattedDateNoYear(
                                        endDay.value.time
                                    )
                                }"
                            )
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        TextButton(
                            onClick = { onDismissRequest() },
                            modifier = Modifier.padding(8.dp),
                        ) {
                            Text("Cancel")
                        }

                        TextButton(
                            onClick = {
                                onConfirmation(item.keys.toString())
                            },
                            modifier = Modifier.padding(8.dp),
                        ) {
                            Text("Publish")
                        }
                    }

                }
            }

        } else {
            LoadScreen()
        }
    }
}


@Composable
fun previewCardsList(
    item: Map.Entry<String, Car>,
    viewModel: ListMyCarsViewModel,
    navController: NavHostController,
    onShowDialog: (Boolean, HashMap<String, Car>) -> Unit, // Función para actualizar el estado
) {
    val menuExpanded = remember { mutableStateOf(false) }

    ListItem(
        headlineContent = { Text(item.value.model) },
        supportingContent = {
            Text("Kilometers: ${item.value.kilometers}\nPlate: ${item.value.plate}\nModel year: ${item.value.year}")
        },
        leadingContent = {
            item.value.image.firstOrNull()?.let { imageUrl ->

                Box(
                ) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = "Car Image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(135.dp)
                    )
                }
            }

        },
        trailingContent =
        {
            Box(
                contentAlignment = Alignment.Center,
            ) {
            }
            IconButton(onClick = {
                menuExpanded.value = true
            })
            {
                Icon(imageVector = Icons.Filled.MoreVert, contentDescription = "More options")
            }
            DropdownMenu(
                expanded = menuExpanded.value,
                onDismissRequest = { menuExpanded.value = false }
            ) {
                if (item.value.rentCars.isNotEmpty()) {
                    DropdownMenuItem(
                        text = { Text("Ver Publicación") },
                        onClick = {
                            navController.navigate(AppScreens.RentCarScreen.route + "/${item.key}")
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Outlined.Search,
                                contentDescription = null
                            )
                        })

                }
                DropdownMenuItem(
                    text = { Text("Publicar") },
                    onClick = {
                        menuExpanded.value = false
                        val newHashMap = HashMap<String, Car>()
                        newHashMap[item.key] = item.value
                        onShowDialog(true, newHashMap)
                        //navController.navigate(AppScreens.MyRentalsScreen.route+"/${item.key}")

                    },
                    leadingIcon = {
                        Icon(
                            Icons.Outlined.Done,
                            contentDescription = null
                        )
                    })
                DropdownMenuItem(
                    text = { Text("Editar") },
                    onClick = { navController.navigate("${AppScreens.CarsFormScreen.route}/${item.key}") },
                    leadingIcon = {
                        Icon(
                            Icons.Outlined.Build,
                            contentDescription = null
                        )
                    })
                HorizontalDivider()
                DropdownMenuItem(
                    text = { Text("Eliminar") },
                    onClick = {
                        viewModel.viewModelScope.launch {
                            viewModel.removeCarFromList(item.key)
                        }
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Outlined.Delete,
                            contentDescription = null
                        )
                    },
                )

            }
        }
    )
    HorizontalDivider()
}



