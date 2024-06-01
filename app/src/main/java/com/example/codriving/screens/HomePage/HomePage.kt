package com.example.codriving.screens.HomePage


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.codriving.R
import com.example.codriving.common.contentModalSheet
import com.example.codriving.common.getFormattedDateNoYear
import com.example.codriving.common.itemCarView
import com.example.codriving.navigation.AppScreens
import com.example.codriving.screens.HomePage.navigationBar.navigationBar
import com.example.codriving.screens.MyCars.LoadScreen
import com.example.codriving.screens.SearchPage.SearchPageScreen
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Finder(
    viewModel: HomeViewModel,
    selectStartDay: State<Date>, // Cambia el tipo según corresponda
    selectEndDay: State<Date>, // Cambia el tipo según corresponda
    onTakeRange: () -> Unit,
) {

    val state = rememberTimePickerState()
    var showTimePicker by remember { mutableStateOf(false) }
    val typesOfUse = listOf("Pick-Up", "Date", "Drop-off")
    val defaultSelectedItemIndex = 0
    val selectedIndex = remember { mutableStateOf(defaultSelectedItemIndex) }
    val currentIndex = remember { mutableStateOf(0) }

    val startTime: String = buildString {
        val calendar = Calendar.getInstance()
        calendar.time = selectStartDay.value

        append(
            "%02d:%02d".format(
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE)
            )
        )
    }
    val endTime: String = buildString {
        val calendar = Calendar.getInstance()
        calendar.time = selectEndDay.value

        append(
            "%02d:%02d".format(
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE)
            )
        )
    }
    SegmentedControl(
        items = typesOfUse,
        selectedIndex = selectedIndex,
        startTime = startTime,
        endTime = endTime,
        dateRange = "${getFormattedDateNoYear(selectStartDay.value.time)}-${
            getFormattedDateNoYear(
                selectEndDay.value.time
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




    if (showTimePicker) {
        TimePickerDialog(
            onCancel = { showTimePicker = false },
            onConfirm = {
                val hour = state.hour
                val minute = state.minute
                when (currentIndex.value) {
                    0 -> {
                        viewModel.setStartTime(hour, minute)
                    }

                    2 -> {
                        viewModel.setEndTime(hour, minute)
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
    val topRatedCars = viewModel.mostRated.observeAsState()
    val TopAppBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    var isSearching by rememberSaveable {
        mutableStateOf(false)
    }

    contentModalSheet(bottomSheetState = bottomSheetState, onConfirmation = { startDay, endDay ->
        viewModel.setStartDay(startDay)
        viewModel.setEndDay(endDay)
    }) {
        Scaffold(
            modifier = Modifier
                .fillMaxSize(),
            topBar = {
                HeaderHome(
                    scrollBehavior = TopAppBarScrollBehavior,
                    viewModel,
                    bottomSheetState,
                    onSearch = {
                        isSearching = false

                        isSearching = true
                    },
                    onLogOut = {
                        if (viewModel.logOut()) {
                            navController.navigate(
                                AppScreens.LoginScreen.route,
                            )
                        }
                    },
                    onProfile = {
                        navController.navigate(AppScreens.ProfileScreen.route)
                    },

                    )
            },
            bottomBar = { navigationBar(navController = navController) },
            content = { paddingValues ->
                Column(modifier = Modifier.padding(paddingValues)) {
                    if (!isSearching) {
                        LazyColumn(modifier = Modifier.padding(10.dp)) {
                            topRatedCars.value?.let {
                                it.forEach { (key, car) ->
                                    item {
                                        itemCarView(
                                            car = car,
                                            onMessage = {},
                                            onClickItem = { click ->
                                                if (click) navController.navigate(AppScreens.RentCarScreen.route + "/${key}")

                                            })
                                    }
                                }
                            } ?: item {
                                LoadScreen()
                            }
                        }
                    } else {
                        SearchPageScreen(
                            startTime = viewModel.selectedStartDay.value,
                            endTime = viewModel.selectedEndDay.value,
                            goBack = { isSearching = false },
                            clickItem = { navController.navigate(AppScreens.RentCarScreen.route + "/${it}") },
                        )
                    }
                }
            }

        )

    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun HeaderHome(
    scrollBehavior: TopAppBarScrollBehavior,
    viewModel: HomeViewModel,
    bottomSheetState: ModalBottomSheetState,
    onSearch: () -> Unit,
    onLogOut: () -> Unit,
    onProfile: () -> Unit,
) {
    val selectstartDay = viewModel.selectedStartDay.collectAsState()
    val selectEndDay = viewModel.selectedEndDay.collectAsState()
    val expanded = remember {
        mutableStateOf(false)
    }

    val coroutineScope = rememberCoroutineScope()
    Column {
        CenterAlignedTopAppBar(
            title = { Text(text = stringResource(id = R.string.app_name)) },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.primary,
            ),
            actions = {
                IconButton(onClick = { expanded.value = true }) {
                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = "Profile"
                    )
                }
                DropdownMenu(
                    expanded = expanded.value,
                    onDismissRequest = { expanded.value = false }
                ) {
                    DropdownMenuItem(
                        trailingIcon = {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null
                            )
                        },
                        text = {
                            Text(
                                text = "Profile"
                            )
                        },
                        onClick = {
                            onProfile()
                            expanded.value = false
                        }
                    )

                    DropdownMenuItem(
                        trailingIcon = {
                            Icon(
                                Icons.AutoMirrored.Filled.ExitToApp,
                                contentDescription = null
                            )
                        },
                        text = {
                            Text(
                                text = "Log Out"
                            )
                        },
                        onClick = {
                            if (viewModel.logOut()) {
                                onLogOut()
                            }
                            expanded.value = false
                        }
                    )

                }


            },
            scrollBehavior = scrollBehavior,
        )
        Row(
            Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Finder(
                viewModel = viewModel,
                selectStartDay = selectstartDay,
                selectEndDay = selectEndDay,
            ) {
                coroutineScope.launch {
                    bottomSheetState.show()
                }
            }
            IconButton(
                onClick = { onSearch() },
            ) {
                Icon(Icons.Filled.Search, contentDescription = null)
            }


        }
    }

}







