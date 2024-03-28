package com.example.codriving.RentCar.ui

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.codriving.R
import com.example.codriving.data.RentCars
import kotlinx.coroutines.launch


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
    val error by rentCarViewModel.error.observeAsState()
    var snackbarHostState = remember { SnackbarHostState() }
    var scope = rememberCoroutineScope()

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
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            },
            topBar = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(70.dp)
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
                //COntroling error data
                scope.launch {
                    if (error != null) {
                        val snackbarResult = snackbarHostState.showSnackbar(
                            "$error",
                            actionLabel = "Back"
                        )
                        if (snackbarResult == SnackbarResult.ActionPerformed) {
                            navController.popBackStack()
                        }
                    }
                }
            },
            content = { paddingValues ->
                Column(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize()
                ) {
                   // rentCar?.let { CarouselCard(it) } //POner algo en caso de que falle
                    rentCar?.let { bodyRest(it) }
                }


            }
        )
    }
}

@Composable
fun RatingBar(
    rating: Double,
    starSize: Dp = 24.dp,
    starSpacing: Dp = 4.dp,
    fullStarIconResId: Int = R.drawable.star_solid,
    halfStarIconResId: Int = R.drawable.star_half_solid,
    emptyStarIconResId: Int = R.drawable.star_regular
) {
    Row {

        for (i in 1 until 6) {
            val iconResId = when {
                i <= rating -> fullStarIconResId
                i <= rating + 0.5 -> halfStarIconResId
                else -> emptyStarIconResId
            }
            Image(
                painter = painterResource(id = iconResId),
                contentDescription = null,
                modifier = Modifier
                    .size(starSize),
                )
        }
    }
}

@Composable
fun bodyRest(rentCar: RentCars) {
    Column(Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                text = rentCar?.pricePerDay.toString() + "€/day",
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            )
        }
       /* Box(modifier = Modifier.fillMaxWidth()) {
            Text(text = rentCar.startDate.format(DateTimeFormatter.ofPattern("LLL dd"))
                .replaceFirstChar { it.uppercase() } + " - " + rentCar.endDate.format(
                DateTimeFormatter.ofPattern("LLL dd")
            ).replaceFirstChar { it.uppercase() })
        }*/
        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_launcher_background),
                        contentDescription = "Owner profile Image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .clip(CircleShape)

                    )
                }
                Column(
                    modifier = Modifier
                        .padding(start = 5.dp)
                        .weight(2f)
                ) {
                    Text(
                        text = "Owner",
                        fontWeight = FontWeight.Bold,
                    )
                    Text(text = rentCar.ownerName)
                }
            }
        }
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Button(onClick = { /*TODO*/ }) {
                Text(text = stringResource(R.string.contact_owner))
            }
            FilledTonalButton(onClick = { /*TODO*/ }) {
                Text(text = stringResource(R.string.book))
            }
        }
        Row(
            Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(top = 10.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
            ) {
                Text(
                    text = rentCar.rating.toString(),
                    fontSize = 30.sp,
                    fontWeight = FontWeight.ExtraBold
                )

                RatingBar(rentCar.rating)
                Text(text = "Total Reviews HERE")
            }
            Column(
                modifier = Modifier
                    .weight(2f)
            ) {

            }
        }
    }

}
/*
@OptIn(ExperimentalPagerApi::class)
@Composable
fun CarouselCard(rentCar: RentCars) {
    val pageState = rememberPagerState()
    val scope = rememberCoroutineScope()
    Column(
        modifier = Modifier
            .fillMaxWidth()

    ) {
        HorizontalPager(
            count = rentCar.car.image.size,
            state = pageState,
            key = { rentCar.car.image[it] },
            modifier = Modifier
                .height(200.dp)
        ) { index ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = rentCar.car.image[index],
                    contentDescription = null,
                    modifier = Modifier.clip(RoundedCornerShape(10.dp)),
                    contentScale = ContentScale.Fit
                )
            }

        }
        HorizontalPagerIndicator(
            pagerState = pageState,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 20.dp, bottom = 20.dp)
        )
    }
    //val pagerState = rememberPagerState(initialPage = 1)
    //val sliderList = listOf()
}*/

