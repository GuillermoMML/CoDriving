package com.example.codriving.view.SearchPage

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.codriving.common.itemCarView
import com.example.codriving.view.MyCarsPage.LoadScreen
import java.util.Date

@Composable
fun SearchPageScreen(
    startTime: Date,
    endTime: Date,
    goBack: () -> Unit,
    clickItem: (String) -> Unit,
    onMessage: (List<String>) -> Unit,
    pickUp: String,
    dropOff: String
) {
    val viewModel: SearchPageViewModel = hiltViewModel()
    val isLoading = viewModel.isLoading.observeAsState()
    val carListEnable = viewModel.carListEnable.collectAsState()
    val scope = rememberCoroutineScope()
    LaunchedEffect(key1 = startTime, key2 = endTime) {
        viewModel.getRentsFromRange(startTime, endTime,pickUp,dropOff)
    }
    if (isLoading.value!!) {
        LoadScreen()
    } else {
        if (viewModel.carListEnable.value.isEmpty()) {
            Box(
                Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "No se ha encontrado ninguna coincidencia")
                    ElevatedButton(onClick = { goBack() }) {
                        Text(text = "Atras")
                    }

                }
            }

        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp)
            ) {
                carListEnable.value.forEach { (carId, car) -> // Destructure key and value
                    item {
                        itemCarView(car = car, onClickItem = {
                            clickItem(carId)
                        },
                            onMessage = {
                                onMessage(it)
                            })
                    }
                }
            }
        }
    }

}


