package com.example.codriving.screens.RentCar

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.codriving.R
import com.example.codriving.common.HeaderPopBack
import com.example.codriving.data.Car
import com.example.codriving.data.RentCars
import com.example.codriving.data.User
import com.example.codriving.navigation.AppScreens
import com.example.codriving.screens.MyCars.LoadScreen
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date



@Composable
fun RentCarScreen(
    navController: NavHostController,
    rentCarViewModel: RentCarViewModel = hiltViewModel(),
    idRentCar: String?
) {

    val isLoading by rentCarViewModel.isLoading.observeAsState()
    val car by rentCarViewModel.rentCar.observeAsState()
    val error by rentCarViewModel.error.observeAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val listOfRents = rentCarViewModel.listOfRents.observeAsState()
    val ownerUser = rentCarViewModel.ownerUser.observeAsState()

    LaunchedEffect(idRentCar) {
        rentCarViewModel.loadData(idRentCar!!)
    }


    if (isLoading == false) {

        // Show loading indicator here
        LoadScreen()
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
                     HeaderPopBack(navController = navController)
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
                    if(ownerUser.value != User()){
                        car?.let { CarouselCard(it) } //POner algo en caso de que falle
                        listOfRents.let { it.value?.let { it1 ->
                            bodyRest(it1, car!!.rating, car!!, ownerUser.value?.fullName!!) {
                                //rentCar es un car
                                navController.navigate("${AppScreens.BookRentScreen.route}/${idRentCar}")
                            }
                        } }
                    }  else{
                        LoadScreen()
                    }
                    }
            }
        )
    }
}

@Composable
fun RatingBar(
    rating: Double,
    starSize: Dp = 24.dp,
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
fun bodyRest(rentCars: List<RentCars>, rating: Double?, rentCar: Car,ownerUser:String = "", onClickBook: () -> Unit) {
    val ownerName = ownerUser
    Column(Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Column {
                rentCars.forEach {
                    val startDate: Date = it.startDate.toDate()
                    val endDate : Date = it.endDate.toDate()

                    val dateFormat = SimpleDateFormat("dd/MM/yyyy")
                    val starformattedDate: String = dateFormat.format(startDate)
                    val endformattedDate: String = dateFormat.format(endDate)

                    Text(
                        text = it.pricePerDay.toString() + "€/day "+starformattedDate+"-"+endformattedDate,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )

                }
            }
        }
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
                    Text(text =ownerName)
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
            FilledTonalButton(onClick = { onClickBook()
            }) {
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
                    text = rating.toString(),
                    fontSize = 30.sp,
                    fontWeight = FontWeight.ExtraBold
                )

                RatingBar(rating!!)
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

@OptIn(ExperimentalPagerApi::class)
@Composable
fun CarouselCard(rentCar: Car) {
    val pageState = rememberPagerState()
    Column(
        modifier = Modifier
            .fillMaxWidth()

    ) {
        HorizontalPager(
            count = rentCar.image.size,
            state = pageState,
            key = { it },
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
                    model = rentCar.image[index],
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
}

