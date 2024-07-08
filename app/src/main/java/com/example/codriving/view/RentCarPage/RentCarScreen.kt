package com.example.codriving.view.RentCarPage

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.StarHalf
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.codriving.R
import com.example.codriving.common.HeaderPopBack
import com.example.codriving.data.model.Car
import com.example.codriving.data.model.RentCars
import com.example.codriving.data.model.RentReview
import com.example.codriving.data.model.User
import com.example.codriving.navigation.AppScreens
import com.example.codriving.ui.theme.md_theme_light_surfaceTint
import com.example.codriving.view.MyCarsPage.LoadScreen
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.DateFormat
import java.util.Date
import java.util.Locale


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
    val listOfReview = rentCarViewModel.listOfReviews.observeAsState(emptyMap())

    LaunchedEffect(idRentCar) {
        rentCarViewModel.loadData(idRentCar!!)
    }
    if (isLoading == false) {
        LoadScreen()
    } else {
        Scaffold(modifier = Modifier
            .fillMaxSize(),
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            },
            topBar = {
                HeaderPopBack(navController = navController, "Rent a Car")
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
                    if (ownerUser.value != User()) {
                        CarouselCard(car!!.image)
                        listOfRents.let {
                            it.value?.let { it1 ->
                                bodyRest(
                                    rentCarViewModel,
                                    it1,
                                    car!!,
                                    ownerUser.value!!,
                                    listOfReview.value,
                                    onClickConversation = {
                                        navController.navigate(AppScreens.ConversationScreen.route)
                                    },
                                    onClickBook = {
                                        navController.navigate("${AppScreens.BookRentScreen.route}/${idRentCar}")
                                    })

                            }
                        }
                    } else {
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
) {
    Row {
        if (rating <= 0) {
            for (i in 1 until 6) {
                androidx.compose.material.Icon(
                    Icons.Outlined.StarOutline,
                    contentDescription = null,
                    tint = md_theme_light_surfaceTint,
                    modifier = Modifier.size(starSize)
                )
            }
        } else {
            for (i in 1 until 6) {
                val icon =
                    if (i <= rating) Icons.Filled.Star else if (i - 0.5 <= rating) Icons.AutoMirrored.Filled.StarHalf else Icons.Outlined.StarOutline
                icon.let {
                    androidx.compose.material.Icon(
                        it,
                        contentDescription = null,
                        tint = md_theme_light_surfaceTint,
                        modifier = Modifier.size(starSize)
                    )
                }
            }

        }
    }
}

@Composable
fun bodyRest(
    viewModel: RentCarViewModel,
    rentCars: List<RentCars>,
    car: Car,
    ownerUser: User,
    listofReview: Map<User, RentReview>,
    onClickBook: () -> Unit,
    onClickConversation: () -> Unit
) {
    val formattedRating = "%.2f".format(Locale.ENGLISH, car.rating)
    val scope = rememberCoroutineScope()
    val dateFormat = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault())
    val rentTimes = remember {
        mutableStateOf(false)
    }


    Column(
        Modifier
            .fillMaxWidth()
            .padding(5.dp)
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            if (rentCars.isEmpty()) {
                Text(
                    text = "Not Available Dates",
                    modifier = Modifier.fillMaxWidth(),
                    fontSize = 20.sp
                )
            } else {
                ExpandableItem(title = "Time Available", expanded = rentTimes, content = {
                    Column {
                        rentCars.forEach {
                            if (it.busy != true) {
                                val startDate: Date = it.startDate.toDate()
                                val endDate: Date = it.endDate.toDate()

                                val startformattedDate: String = dateFormat.format(startDate)
                                val endformattedDate: String = dateFormat.format(endDate)

                                Row(horizontalArrangement = Arrangement.SpaceBetween) {
                                    Column {
                                        Text(
                                            text = it.pricePerDay.toString() + "€/day " + startformattedDate + " - " + endformattedDate,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp
                                        )
                                        Log.d("RentCar: ", it.toString())
                                        Text(
                                            text = "\t\tPick Up: ${if (it.pickUpLocation.isNullOrEmpty()) "Anywhere" else it.pickUpLocation}",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp
                                        )
                                        Text(
                                            text = "\t\tDrop Off: ${if (it.dropOffLocation.isNullOrEmpty()) "Anywhere" else it.dropOffLocation}",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp
                                        )

                                    }

                                }
                            }


                        }
                    }

                }, onClick = { rentTimes.value = !rentTimes.value })

            }
        }

        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = rememberAsyncImagePainter(
                        ImageRequest.Builder(
                            LocalContext.current
                        ).data(data = ownerUser.imageProfile)
                            .apply(block = fun ImageRequest.Builder.() {
                                crossfade(true)
                            }).build()
                    ),
                    contentDescription = "Owner Profile Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .clip(CircleShape)
                        .fillMaxSize()
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
                ownerUser.fullName?.let { Text(text = it) }
            }
        }

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Button(
                enabled = !car.owner?.id.equals(Firebase.auth.uid),
                onClick = {
                    scope.launch(Dispatchers.IO) {
                        val array = listOf(Firebase.auth.uid.toString(), car.owner!!.id)
                        viewModel.addConversation(array)
                        withContext(Dispatchers.Main) {
                            onClickConversation()
                        }
                    }

                }) {
                Text(text = stringResource(R.string.contact_owner))
            }
            FilledTonalButton(
                enabled = !car.owner?.id.equals(Firebase.auth.uid) && rentCars.isNotEmpty(),
                onClick = {
                    onClickBook()
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
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = formattedRating,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = car.numberOfReviews.toString(),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Light
                    )
                }
                RatingBar(car.rating!!)
                if (listofReview.isNotEmpty()) {

                    LazyColumn(
                        contentPadding = PaddingValues(5.dp),
                        verticalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        items(listofReview.keys.toList()) { user ->
                            Card(
                                elevation = CardDefaults.cardElevation(
                                    defaultElevation = 5.dp
                                )
                            ) {
                                Column(
                                    modifier = Modifier
                                        .padding(5.dp)
                                        .fillMaxWidth()
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(50.dp)
                                                .clip(CircleShape)
                                                .background(Color.Transparent),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Image(
                                                painter = rememberAsyncImagePainter(
                                                    ImageRequest.Builder(
                                                        LocalContext.current
                                                    ).data(data = user.imageProfile)
                                                        .apply(block = fun ImageRequest.Builder.() {
                                                            crossfade(true)
                                                        }).build()
                                                ),
                                                contentDescription = "Profile image",
                                                contentScale = ContentScale.Fit,
                                                modifier = Modifier.fillMaxSize()
                                            )
                                        }
                                        Text(text = user.fullName.toString())
                                    }
                                    listofReview[user]?.let { RatingBar(rating = it.rating) }

                                    listofReview[user]?.let { Text(text = it.comment) }

                                }
                            }
                        }
                    }

                }

            }

        }
    }

}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun CarouselCard(bannerUrls: List<String>) {
    val pageState = rememberPagerState()
    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
        ) {
            HorizontalPager(
                count = bannerUrls.size,
                state = pageState,
                key = { it },
            ) { index ->

                Image(
                    painter = rememberAsyncImagePainter(bannerUrls[index]),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                        .clip(shape = RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )

            }
            HorizontalPagerIndicator(
                pagerState = pageState,
                activeColor = Color.White,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(top = 20.dp, bottom = 20.dp)
            )
        }


    }
}

@Composable
fun ExpandableItem(
    title: String,
    content: @Composable () -> Unit,
    expanded: MutableState<Boolean>,
    onClick: () -> Unit
) {
    Column(Modifier.padding(5.dp)) {
        Row(
            modifier = Modifier.clickable(onClick = onClick),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = title, style = typography.titleLarge, modifier = Modifier.weight(1f))
            Icon(
                imageVector = if (expanded.value) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = if (expanded.value) "Collapse" else "Expand"
            )
        }
        if (expanded.value) {
            Spacer(modifier = Modifier.height(8.dp))
            content()
        }
    }
}