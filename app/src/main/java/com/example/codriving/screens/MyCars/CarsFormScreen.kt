package com.example.codriving.screens.MyCars


import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.example.codriving.R
import com.example.codriving.data.model.Year


@Composable
fun CarsFormScreen(navController: NavHostController, viewModel: CarsFormViewModel = hiltViewModel(), car: String? = null) {

    val marcaList by viewModel.marcaList.collectAsState() // Use collectAsState for StateFlow
    val modelsList by viewModel.modelList.collectAsState()
    val selectedModel by viewModel.selectModel.observeAsState()
    val selectedMarca by viewModel.selectMarca.observeAsState()
    val mileageState by viewModel.mileageState.observeAsState()
    val plate by viewModel.plate.observeAsState()
    val validateField by viewModel.validateField.observeAsState()
    val uploadStatus by viewModel.uploadStatus.observeAsState()
    var selectedImageUris by remember { mutableStateOf(emptyList<Uri>()) }
    val isLoading by viewModel.isLoading.observeAsState()
    val Car by viewModel.car.observeAsState()

    LaunchedEffect(car) {
        if (car != null) {
            viewModel.loadCarDetails(car)
        }

    }

    if (uploadStatus!!) {
        navController.popBackStack()
    }
    if (marcaList.isNotEmpty() && modelsList.isNotEmpty()) {
        Scaffold(
            modifier = Modifier
                .fillMaxSize(),
            topBar = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "back",
                        modifier = Modifier
                            .size(40.dp)
                            .padding(5.dp)
                            .clickable {
                                navController.popBackStack()
                            },
                    )
                }
            },

            content = { paddingValues ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(10.dp)
                ) {
                    LazyColumn(
                        modifier = Modifier.padding(paddingValues),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight(0.310f)
                                    .paint(
                                        painterResource(id = R.drawable.banneruploadcar),
                                        contentScale = ContentScale.FillBounds
                                    ),
                                contentAlignment = Alignment.TopCenter
                            ) {
                                Text(
                                    text = stringResource(id = R.string.carsFromBanner),
                                    fontSize = 30.sp,
                                    color = Color.White,
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                )
                            }
                        }

                        if(Car !=null){
                            item {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Start,
                                    verticalAlignment = Alignment.CenterVertically
                                )
                                {
                                    Markes_DropMenu(viewModel, Car?.brand)
                                }
                            }
                            item {
                                Models_DropMenu(viewModel, Car?.model)
                            }
                            item {
                                KilometerField(viewModel, Car?.kilometers)
                            }
                            item {
                                PlateField(viewModel, Car?.plate!!)
                            }
                            item {
                                val years = (1980..2024).map { Year(it) }

                                YearDropDown(viewModel, years)
                            }
                            item {
                                ImageUploadScreen(selectedImageUris) { uri ->
                                    selectedImageUris =
                                        selectedImageUris.toMutableList().apply { add(uri) }
                                }
                            }
                        }else {
                            item {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Start,
                                    verticalAlignment = Alignment.CenterVertically
                                )
                                {
                                    Markes_DropMenu(viewModel, selectedMarca)
                                }
                            }
                            item {
                                Models_DropMenu(viewModel, selectedModel)
                            }
                            item {
                                KilometerField(viewModel, mileageState)
                            }
                            item {
                                PlateField(viewModel, plate!!)
                            }
                            item {
                                val years = (1980..2024).map { Year(it) }

                                YearDropDown(viewModel, years)
                            }
                            item {
                                ImageUploadScreen(selectedImageUris) { uri ->
                                    selectedImageUris =
                                        selectedImageUris.toMutableList().apply { add(uri) }
                                }
                            }
                        }
                        item {
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                Button(
                                    onClick = {

                                        viewModel.uploadCar(selectedImageUris)

                                    }, // Llamar al método en el ViewModel para subir las imágenes
                                    enabled = validateField!! && !isLoading!!, // Deshabilitar el botón si no hay imágenes seleccionadas
                                    content = {
                                        if (isLoading!!) {
                                            CircularProgressIndicator(
                                                modifier = Modifier
                                                    .height(16.dp)
                                                    .width(16.dp),
                                                strokeWidth = 2.dp,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        } else {
                                            Text("Upload Car")
                                        }

                                    }

                                )
                            }

                        }

                    }
                }
            }

        )

    } else {
        LoadScreen()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YearDropDown(
    viewModel: CarsFormViewModel,
    years: List<Year>
) {
    val selectedYear by viewModel.selectedYear.observeAsState()
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    )
    {
        Text(
            text = "Model year:",
            Modifier.weight(0.3f)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .weight(1f)
        ) {
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = {
                    expanded = !expanded
                }
            ) {
                TextField(
                    value = selectedYear!!.value.toString(),
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.menuAnchor()
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    years.forEach { item ->
                        DropdownMenuItem(
                            text = { Text(text = item.value.toString()) },
                            onClick = {
                                viewModel.setSelectedYear(item)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun LoadScreen() {
    Surface(
        modifier = Modifier.fillMaxSize(),
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator() // Indicador de carga circular
        }
    }

}

@Composable
fun PlateField(viewModel: CarsFormViewModel, plate: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    )
    {
        Text(
            text = "Plate Number",
            modifier = Modifier.weight(0.3f)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .weight(1f)
        ) {
            TextField(
                value = plate,
                singleLine = true,
                onValueChange = { newText ->

                    val filteredText = newText.replace(" ", "")
                    if (filteredText != newText) {
                        // Se ha introducido un espacio, no actualizar el valor
                    } else {
                        // Actualizar el valor con el texto filtrado
                        viewModel.setPlate(filteredText)
                    }
                    viewModel.esMatriculaValida(viewModel.getPlate()!!)

                },
                isError = viewModel.getBooleanMatricula()!!,
                supportingText = {
                    if (viewModel.getBooleanMatricula()!!) {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = "No es una matricula",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                trailingIcon = {
                    if (viewModel.getBooleanMatricula()!!)
                        Icon(Icons.Filled.Clear, "error", tint = MaterialTheme.colorScheme.error)
                },
                keyboardActions = KeyboardActions { viewModel.esMatriculaValida(viewModel.getPlate()!!) },
            )
        }
    }
}


@Composable
fun KilometerField(viewModel: CarsFormViewModel, mileageState: Int?) {
    val mileageErrorState = remember { mutableStateOf("") }

    val onMileageChange: (String) -> Unit = { newMileage ->
        viewModel.setMilesState(newMileage.toIntOrNull() ?: 0)
        mileageErrorState.value =
            if (mileageState!! <= 0) "Introduzca un kilometraje válido" else ""
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    )
    {
        Text(
            text = "Kilometers:",
            Modifier.weight(0.3f)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .weight(1f)
        ) {
            TextField(
                value = mileageState.toString(),
                onValueChange = onMileageChange,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Models_DropMenu(
    viewModel: CarsFormViewModel,
    selectedModel: String?
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedText by remember { mutableStateOf(viewModel.getListModel()[0]) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    )
    {
        Text(
            text = "Modelo:",
            Modifier.weight(0.3f)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .weight(1f)
        ) {
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = {
                    expanded = !expanded
                }
            ) {
                TextField(
                    value = selectedModel!!,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.menuAnchor()
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    viewModel.getListModel().forEach { item ->
                        DropdownMenuItem(
                            text = { Text(text = item) },
                            onClick = {
                                selectedText = item
                                viewModel.setModel(selectedText)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ImageUploadScreen(
    selectedImageUris: List<Uri>,
    onImageSelected: (Uri) -> Unit
) {
    val getContent =
        rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) { results: List<Uri>? ->
            results?.forEach { uri ->
                onImageSelected(uri)
            }
        }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = { getContent.launch("image/*") },
            content = { Text("Select Images") }
        )

        // Mostrar las imágenes en filas de tres columnas
        val chunkedUris =
            selectedImageUris.chunked(3) // Agrupar las URIs en grupos de tres para mostrar en filas
        chunkedUris.forEachIndexed { index, uris ->
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {

                uris.forEach { uri ->
                    Image(
                        painter = rememberAsyncImagePainter(uri),
                        contentDescription = null,
                        modifier = Modifier
                            .size(120.dp) // Tamaño de las imágenes
                            .padding(2.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Markes_DropMenu(viewModel: CarsFormViewModel, selectedMarca: String?) {
    var expanded by remember { mutableStateOf(false) }
    var selectedText by remember { mutableStateOf(viewModel.getListMarks()[0]) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    )
    {
        Text(
            text = "Marca:",
            Modifier.weight(0.3f)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .weight(1f)
        ) {
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = {
                    expanded = !expanded
                }
            ) {
                TextField(
                    value = selectedMarca!!,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.menuAnchor()
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    viewModel.getListMarks().forEach { item ->
                        DropdownMenuItem(
                            text = { Text(text = item) },
                            onClick = {
                                selectedText = item
                                viewModel.setMarca(selectedText)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }

}

@Preview
@Composable
fun CarsFormScreenPreviewDark() {
    CarsFormScreenPreview()
}

@Preview
@Composable
fun CarsFormScreenPreview() {
    // Provide a mock NavHostController for the preview (since it's not needed for previewing)
    val navController = rememberNavController()

    CarsFormScreen(navController)
}
