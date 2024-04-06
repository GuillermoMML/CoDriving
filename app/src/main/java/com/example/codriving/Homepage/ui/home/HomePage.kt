package com.example.codriving.Homepage.ui.home


import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.codriving.Homepage.ui.home.navigationBar.navigationBar
import com.example.codriving.MyCars.LoadScreen
import com.example.codriving.MyCars.contentModalSheet
import com.example.codriving.MyCars.getFormattedDateNoYear
import com.example.codriving.R
import com.example.codriving.UploadRentals.SegmentedControl
import com.example.codriving.data.Car
import com.example.codriving.navigation.AppScreens
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Finder(viewModel: HomeViewModel, onTakeRange: () -> Unit) {
    val selectstartDay = viewModel.selectedStartDay.collectAsState()
    val selectEnDay = viewModel.selectedEndDay.collectAsState()
    val selectedStartTime = viewModel.selectStartTime.collectAsState()

    val selectedEndTime = viewModel.endHour.collectAsState()
    val state = rememberTimePickerState()
    val formatter = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }
    var showTimePicker by remember { mutableStateOf(false) }
    val typesOfUse = listOf("Pick-Up", "Date", "Drop-off")
    val defaultSelectedItemIndex = 0
    val selectedIndex = remember { mutableStateOf(defaultSelectedItemIndex) }
    val currentIndex = remember { mutableStateOf(0) }


    Row(
        Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    )
    {
        SegmentedControl(
            items = typesOfUse,
            selectedIndex = selectedIndex,
            startTime = selectedStartTime.value,
            endTime = selectedEndTime.value,
            dateRange = "${getFormattedDateNoYear(selectstartDay.value)}-${
                getFormattedDateNoYear(
                    selectEnDay.value
                )
            }"
        ) {
            if (it == 0 || it == 2) {
                showTimePicker = true
            } else {
                onTakeRange()
            }
            currentIndex.value = it
        }
        IconButton(
            onClick = {},
        ) {
            Icon(Icons.Filled.Search, contentDescription = null)
        }


    }

    if (showTimePicker) {
        TimePickerDialog(
            onCancel = { showTimePicker = false },
            onConfirm = {
                val cal = Calendar.getInstance()
                cal.set(Calendar.HOUR_OF_DAY, state.hour)
                cal.set(Calendar.MINUTE, state.minute)
                cal.isLenient = false
                when (currentIndex.value) {
                    0 -> {
                        viewModel.setStartTime(formatter.format(cal.time))
                    }

                    2 -> {
                        viewModel.setEndTime(formatter.format(cal.time))
                    }
                }
                showTimePicker = false
            },
        ) {
            TimePicker(state = state)
        }
    }

}

@Composable
fun TimePickerDialog(
    title: String = "Select Time",
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
    toggle: @Composable () -> Unit = {},
    content: @Composable () -> Unit,
) {
    Dialog(
        onDismissRequest = onCancel,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        ),
    ) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = 6.dp,
            modifier = Modifier
                .width(IntrinsicSize.Min)
                .height(IntrinsicSize.Min)
                .background(
                    shape = MaterialTheme.shapes.extraLarge,
                    color = MaterialTheme.colorScheme.surface
                ),
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    text = title,
                    style = MaterialTheme.typography.labelMedium
                )
                content()
                Row(
                    modifier = Modifier
                        .height(40.dp)
                        .fillMaxWidth()
                ) {
                    toggle()
                    Spacer(modifier = Modifier.weight(1f))
                    TextButton(
                        onClick = onCancel
                    ) { Text("Cancel") }
                    TextButton(
                        onClick = onConfirm
                    ) { Text("OK") }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HomePage(

    navController: NavController,
) {
    val bottomSheetState =
        rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    val viewModel: HomeViewModel = hiltViewModel() // Injected in the composable
    val coroutineScope = rememberCoroutineScope()
    val topRatedCars = viewModel.mostRated.observeAsState()
    val TopAppBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    contentModalSheet(bottomSheetState = bottomSheetState, onConfirmation = { startDay, endDay ->
        viewModel.setStartDay(startDay)
        viewModel.setEndDay(endDay)
    }) {
        Scaffold(
            modifier = Modifier
                .fillMaxSize(),
            topBar = {
                HeaderHome(scrollBehavior = TopAppBarScrollBehavior)
            },
            bottomBar = { navigationBar(navController) },
            content = { paddingValues ->
                Column(modifier = Modifier.padding(paddingValues)) {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Finder(viewModel = viewModel) {
                            coroutineScope.launch {
                                bottomSheetState.show()
                            }
                        }

                    }
                    if (topRatedCars.value.isNullOrEmpty()) {
                        LoadScreen()
                    } else {
                        Text(text = "Featured Cars")
                        CarruselTopCars(topRatedCars = topRatedCars.value!!)
                    }

                }
            }

        )

    }
}

@Composable
fun CarruselTopCars(topRatedCars: List<Car>) {
    val aspectRatio = 16f / 9f
    val heightInDp = 150f
    val widthInDp = heightInDp * aspectRatio

    LazyRow(
        modifier = Modifier
            .fillMaxWidth(),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        topRatedCars.forEach() { Car ->

            item {

                Column {
                    Card(

                        shape = RoundedCornerShape(8.dp),
                        elevation = CardDefaults.cardElevation(hoveredElevation = 10.dp),
                        modifier = Modifier
                            .background(Color.Transparent)
                            .size(width = widthInDp.toInt().dp, height = 150.dp)

                    ) {
                        AsyncImage(
                            model = Car.image[0],
                            contentDescription = Car.model,
                            contentScale = ContentScale.Crop,
                        )
                    }
                    Text(
                        text = Car.model,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Kilometers " + Car.kilometers,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun SearchBar(viewModel: SearchViewModel, navController: NavController) {

    val searchText by viewModel.searchText.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxWidth()

    ) {
        TextField(
            value = searchText,
            readOnly = true,
            onValueChange = { viewModel.onSearchTextChanged(it) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp)
                .clip(RoundedCornerShape(10.dp)),
            label = { Text("Search") },
            interactionSource = remember {
                MutableInteractionSource()
            }.also { interactionSource ->
                LaunchedEffect(interactionSource) {
                    interactionSource.interactions.collect {
                        if (it is PressInteraction.Release) {
                            navController.navigate(route = AppScreens.SearchScreen.route)
                        }
                    }
                }
            }
        )

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeaderHome(scrollBehavior: TopAppBarScrollBehavior) {

    CenterAlignedTopAppBar(
        title = { Text(text = stringResource(id = R.string.app_name)) },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ),
        actions = {
            IconButton(onClick = { /*TODO*/ }) {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = "Profile"
                )
            }
        },
        scrollBehavior = scrollBehavior,
    )
}




