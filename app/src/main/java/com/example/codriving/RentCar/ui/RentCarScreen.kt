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
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun RentCarScreen(
    navController: NavHostController,
    idRentCar: Int?
) {

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

@Composable
fun CarouselCard() {
    //val pagerState = rememberPagerState(initialPage = 1)
    //val sliderList = listOf()
}
