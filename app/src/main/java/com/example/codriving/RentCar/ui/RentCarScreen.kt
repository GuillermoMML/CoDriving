package com.example.codriving.RentCar.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController


class RentCarScreen {

}

@Composable
fun RentCarScreen(
    navController: NavHostController,
    rentCarViewModel: RentCarViewModel,
    idRentCar: Int?
) {
    val isLoading by rentCarViewModel.isLoading.observeAsState()
    val rentCar by rentCarViewModel.rentCar.observeAsState()

    LaunchedEffect(idRentCar) {
        rentCarViewModel.loadData(idRentCar)
    }

    if (isLoading == false) {
        // Show loading indicator here
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(100.dp)
                .size(200.dp)
        ) {
            CircularProgressIndicator(
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.CenterEnd)
            )
        }
    } else {
        // Display content based on "rentCar"
        // ... your content composables
        Scaffold(modifier = Modifier
            .padding(5.dp)
            .fillMaxSize(),
            topBar = {

                Box(

                    modifier = Modifier
                        .background(Color.Red)
                        .fillMaxWidth()
                        .height(50.dp)
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
            content = { paddingValues ->
                Box(
                    modifier = Modifier
                        .background(Color.Blue)
                        .padding(paddingValues)
                        .fillMaxSize()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(),
                    ) {
                        CarouselCard()
                    }

                }
            }
        )
    }
}

@Composable
fun CarouselCard() {
    //val pagerState = rememberPagerState(initialPage = 1)
    //val sliderList = listOf()
}
