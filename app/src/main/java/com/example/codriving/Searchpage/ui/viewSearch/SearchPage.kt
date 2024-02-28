package com.example.codriving.Searchpage.ui.viewSearch

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.codriving.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchPage(navController: NavHostController) {
    val state = rememberDatePickerState()
    val openDialog = remember { mutableStateOf(false) }
    val location = remember { mutableStateOf("") }
    val startRent = remember { mutableStateOf("") }
    val endRent = remember { mutableStateOf("") }
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    var _searchText = MutableStateFlow("")
    val serachText = _searchText.asStateFlow()

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
                        }
                )
            }
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {

            }
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier.padding(paddingValues)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.335f)
                        .paint(
                            painterResource(id = R.mipmap.backgroundsearch),
                            contentScale = ContentScale.Crop
                        )
                ) {
                    Text(
                        text = stringResource(id = R.string.logo_search),
                        fontSize = 30.sp,
                        color = Color.White,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                    )
                }
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .padding(5.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Text(text = stringResource(id = R.string.pick_up))
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Text(text = stringResource(id = R.string.reservation_start))
                        TextButton(onClick = { /*TODO*/ }) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()

                            )
                            Text(text = location.value)
                        }
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Text(text = stringResource(id = R.string.reservation_ends))
                    }

                }

            }


        }

    )
}

@Preview
@Composable
fun SearchPagePreview() {
    val navController =
        rememberNavController() // O utiliza un NavController falso para la vista previa
    SearchPage(navController = navController)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePicker(openDialog: MutableState<Boolean>, state: DatePickerState) {
    if (openDialog.value) {
        DatePickerDialog(
            onDismissRequest = {
                openDialog.value = false
            },
            confirmButton = {
                TextButton(
                    onClick = {

                        openDialog.value = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        openDialog.value = false
                    }
                ) {
                    Text("CANCEL")
                }
            }
        ) {
            androidx.compose.material3.DatePicker(
                state = state,
                modifier = Modifier
                    .size(400.dp)
                    .padding(15.dp)
            )
        }
    }
}