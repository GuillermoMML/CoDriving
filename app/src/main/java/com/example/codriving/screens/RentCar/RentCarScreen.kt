package com.example.codriving.screens.RentCar

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.StarHalf
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Button
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.codriving.R
import com.example.codriving.common.HeaderPopBack
import com.example.codriving.data.model.Conversations
import com.example.codriving.data.model.Message
import com.example.codriving.data.model.RentCars
import com.example.codriving.data.model.User
import com.example.codriving.navigation.AppScreens
import com.example.codriving.screens.MyCars.LoadScreen
import com.example.codriving.ui.theme.md_theme_light_surfaceTint
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
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
                                    it1, car!!.rating, //car!!,
                                    ownerUser.value?.fullName!!,
                                    car!!.owner!!.id
                                ) {
                                    //rentCar es un car
                                    navController.navigate("${AppScreens.BookRentScreen.route}/${idRentCar}")
                                }
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
    rentCars: List<RentCars>,
    rating: Double?,
    ownerUser: String = "",
    car: String,
    onClickBook: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val dateFormat = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault())
    val ownerName = ownerUser
    val rentTimes = remember {
        mutableStateOf(false)
    }


    Column(Modifier.fillMaxWidth()) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            ExpandableItem(title = "Time Availables", expanded = rentTimes, content = {
                Column {
                    rentCars.forEach {
                        if (it.busy != true) {
                            val startDate: Date = it.startDate.toDate()
                            val endDate: Date = it.endDate.toDate()

                            val starformattedDate: String = dateFormat.format(startDate)
                            val endformattedDate: String = dateFormat.format(endDate)

                            Text(
                                text = it.pricePerDay.toString() + "€/day " + starformattedDate + " - " + endformattedDate,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        }


                    }
                }

            }, onClick = { rentTimes.value = !rentTimes.value })
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
                    Text(text = ownerName)
                }
            }
        }
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Button(onClick = {
                scope.launch(Dispatchers.IO) {
                    val array = listOf(Firebase.auth.uid.toString(), car)
                    val conversation = Conversations(
                        date = Timestamp(Date()),
                        lastMessage = null,
                        userIds = array
                    )
                    val message = Message(
                        message = "",
                        idSender = "",
                        type_message = 0,
                        date = Timestamp(Date())
                    )
                    val conversationRef =
                        Firebase.firestore.collection("conversations").add(conversation)

                    // Get the document reference for the newly created conversation
                    val docRef = conversationRef.await()
                    Firebase.firestore.collection("conversations").document(docRef.id)
                        .collection("messages").add(message).await()

                }

            }) {
                Text(text = stringResource(R.string.contact_owner))
            }
            FilledTonalButton(onClick = {
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