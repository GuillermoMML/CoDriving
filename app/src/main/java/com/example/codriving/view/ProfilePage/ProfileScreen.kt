package com.example.codriving.view.ProfilePage

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.ImageSearch
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.codriving.common.launchPhotoPicker
import com.example.codriving.data.model.RequestContracts
import com.example.codriving.view.HomePage.navigationBar.navigationBar
import com.example.codriving.view.MyCarsPage.LoadScreen
import com.example.codriving.view.notificationPage.formatDateToDayMonthYear
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import java.util.Date

@Composable
fun ProfileScreen(navController: NavController, viewModel: ProfileViewModel) {
    val email = viewModel.email.observeAsState("")
    val phone = viewModel.phone.observeAsState("")
    val location = viewModel.location.observeAsState("")
    val profileimage = viewModel.profileImage.observeAsState("")
    val profileUri = viewModel.profileUri.observeAsState()
    val fullName = viewModel.fullname.observeAsState("")
    var password by remember { mutableStateOf("") }
    var errorPasswordMessage by remember { mutableStateOf("") }
    var errorSecondPasswordMessage by remember { mutableStateOf("") }
    val errorMessage = viewModel.errorMessage.observeAsState("")
    var rating by remember { mutableStateOf(3.0) }
    var review by remember { mutableStateOf("") }
    var resultReviewMessage by remember {
        mutableStateOf("")
    }
    val resultMessage = viewModel.chagingPasswordResult.observeAsState("")
    var passwordAgain by remember { mutableStateOf("") }
    val loading = viewModel.loading.observeAsState(true)
    val loadingProgress = viewModel.loading.observeAsState(false)
    val contracts = viewModel.contracts.observeAsState(HashMap())
    val snackbarHostState = remember { SnackbarHostState() }
    val today = Date()

    val scope = rememberCoroutineScope()
    var setContractReview by remember { mutableStateOf<RequestContracts?>(null) }

    val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> uri?.let { viewModel.setImageUri(it) } }
    )

    LaunchedEffect(key1 = resultMessage.value) {
        if (resultMessage.value.isNotEmpty()) {
            snackbarHostState.showSnackbar(resultMessage.value)
        }
    }

    LaunchedEffect(key1 = errorMessage.value) {
        if (errorMessage.value.isNotEmpty()) {
            snackbarHostState.showSnackbar(errorMessage.value)
        }
    }

    if (setContractReview != null) {
        Dialog(onDismissRequest = {
            setContractReview = null
            resultReviewMessage = ""
        }) {

            if (resultReviewMessage.isNotEmpty()) {
                Card {
                    Text(text = resultReviewMessage)
                }
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    RatingCard(
                        onRatingChange = { rating = it.toDouble() }, // Pass rating state here
                        onReviewChange = { review = it },
                        rating = rating, // Use the current rating state
                        review = review,
                        onSendReview = {
                            viewModel.addReview(
                                setContractReview!!,
                                review,
                                rating + 1.0,
                                onComplete = { resultReviewMessage = it },
                                onFailure = { resultReviewMessage = it.message.toString() })
                        }
                    )
                }

            }
        }
    }
    Scaffold(

        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        bottomBar = {
            navigationBar(navController = navController)
        }
    ) { paddingValues ->

        if (loading.value) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            )
            {
                LoadScreen()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                LazyColumn(
                    modifier = Modifier.padding(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    val data = profileUri.value ?: profileimage.value


                    item {
                        Box(
                            modifier = Modifier
                                .padding(5.dp)
                                .size(180.dp),

                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .fillMaxSize()
                                    .background(Color.Transparent),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = rememberAsyncImagePainter(
                                        ImageRequest.Builder(
                                            LocalContext.current
                                        )
                                            .data(data = data)
                                            .apply(block = fun ImageRequest.Builder.() {
                                                crossfade(true)
                                            }).build()
                                    ),
                                    contentDescription = "Profile image",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            Surface(
                                modifier = Modifier.fillMaxSize(),
                                color = Color.Transparent // Color transparente
                            ) {
                                Box(contentAlignment = Alignment.BottomEnd) {
                                    IconButton(
                                        onClick = {
                                            launchPhotoPicker(singlePhotoPickerLauncher)
                                        },
                                        modifier = Modifier
                                            .size(48.dp)
                                            .background(Color.White, shape = CircleShape)
                                            .padding(8.dp)
                                    ) {
                                        Icon(Icons.Default.ImageSearch, contentDescription = null)
                                    }
                                }
                            }
                        }
                    }
                    item {
                        OutlinedTextField(
                            value = fullName.value!!,
                            onValueChange = {
                                viewModel.setChanges(
                                    email.value!!,
                                    location.value!!,
                                    it,
                                    phone.value
                                )
                            },
                            label = { Text("Fullname") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = email.value!!,
                            onValueChange = {
                                viewModel.setChanges(
                                    it,
                                    location.value!!,
                                    fullName.value!!,
                                    phone.value
                                )
                            },
                            label = { Text("Email") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = location.value!!,
                            onValueChange = {
                                viewModel.setChanges(
                                    email.value!!,
                                    it,
                                    fullName.value!!,
                                    phone.value
                                )
                            },
                            label = { Text("Location") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = phone.value,
                            onValueChange = {
                                viewModel.setChanges(
                                    email.value!!,
                                    location.value!!,
                                    fullName.value!!,
                                    it
                                )
                            },
                            label = { Text("Phone Number") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = password,
                            onValueChange = {
                                password = it
                                errorPasswordMessage =
                                    if (password.length < 5) "Password must be at least 6 characters" else ""
                            },
                            label = { Text("Password") },
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            modifier = Modifier.fillMaxWidth()
                        )

                        if (errorPasswordMessage.isNotEmpty()) {
                            Text(
                                text = errorPasswordMessage,
                                color = Color.Red,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                    item {
                        OutlinedTextField(
                            value = passwordAgain,
                            onValueChange = {
                                passwordAgain = it
                                errorSecondPasswordMessage =
                                    if (passwordAgain != password) "Password is not the same" else ""
                            },
                            label = { Text("Repeat Password") },
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            modifier = Modifier.fillMaxWidth()
                        )
                        if (errorSecondPasswordMessage.isNotEmpty()) {
                            Text(
                                text = errorSecondPasswordMessage,
                                color = Color.Red,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }

                    if (contracts.value.isNotEmpty()) {
                        item {
                            Text(text = "Renting")
                        }
                        contracts.value.forEach { (_, contract) ->
                            var expired: String
                            contract.rentCars.forEach {
                                item {
                                    Column {
                                        Row(
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            if (today.after(it.endDate.toDate())) {
                                                expired = "Expired"
                                            } else {
                                                expired = "Active"
                                            }
                                            Column(
                                                horizontalAlignment = Alignment.Start,
                                                modifier = Modifier.weight(0.9f)
                                            ) {
                                                // Iterate through contracts and display each row
                                                Text(
                                                    text = "Owner: ${contract.owner.fullName}",
                                                    style = MaterialTheme.typography.labelMedium
                                                )
                                                Text(
                                                    text = "Client: ${contract.client.fullName}",
                                                    style = MaterialTheme.typography.labelMedium
                                                )
                                                Text(
                                                    text = "Car: ${contract.car.model}",
                                                    style = MaterialTheme.typography.labelMedium
                                                )
                                                Text(text = "Pick Up: ${it.pickUpLocation?: ""}")
                                                Text(text = "Drop Up: ${it.dropOffLocation?: ""}")

                                                Text(
                                                    text = "Rating Range: ${
                                                        formatDateToDayMonthYear(
                                                            it.startDate.toDate()
                                                        )
                                                    } - ${
                                                        formatDateToDayMonthYear(
                                                            it.endDate.toDate()
                                                        )
                                                    }  ${expired}",
                                                    style = MaterialTheme.typography.labelMedium
                                                )

                                            }
                                            if (expired == "Expired" && contract.car.owner!!.id != FirebaseAuth.getInstance().uid) {
                                                IconButton(onClick = {
                                                    setContractReview = contract
                                                }) {
                                                    Icon(
                                                        Icons.AutoMirrored.Filled.Comment,
                                                        contentDescription = null
                                                    )
                                                }

                                            }
                                        }
                                        HorizontalDivider()

                                    }

                                }
                            }
                        }


                    }
                    item {
                        Button(
                            enabled =
                            errorPasswordMessage.isEmpty() && fullName.value!!.isNotEmpty() &&
                                    email.value!!.isNotEmpty() && phone.value.isNotEmpty() && location.value!!.isNotEmpty(),

                            onClick = {
                                scope.launch {
                                    viewModel.setCurrentChanges(password)
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            if (loadingProgress.value) {
                                LoadScreen()
                            }
                            Text("Guardar")
                        }

                    }
                }

            }


        }
    }
}

@Composable
fun RatingCard(
    onRatingChange: (Int) -> Unit,
    onReviewChange: (String) -> Unit,
    rating: Double,
    review: String = "",
    onSendReview: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = review,
                onValueChange = onReviewChange,
                label = { Text("Leave a review") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {

                for (starIndex in 0..4) {
                    val isFilled = starIndex <= rating
                    val starIcon = if (isFilled) {

                        Icons.Filled.Star
                    } else {
                        Icons.Outlined.StarOutline
                    }
                    IconButton(onClick = {
                        onRatingChange(starIndex)
                    }) {
                        Icon(starIcon, contentDescription = "Star rating")
                    }
                }
            }
            Button(
                onClick = { onSendReview() },
                enabled = review.isNotEmpty()
            ) {
                Row {
                    Text(text = "Send Review")
                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null)
                }
            }
        }
    }
}
