@file:JvmName("ConversationsViewModelKt")

package com.example.codriving.screens.Chat

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.codriving.navigation.AppScreens
import com.example.codriving.screens.MyCars.LoadScreen
import java.text.DateFormat
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConversationScreen(navController: NavController, viewModel: ConversationsViewModel) {
    val conversations = viewModel.conversations.observeAsState()
    val loading = viewModel.loading.observeAsState()
    val errorMessage = viewModel.error.observeAsState()
    val profiles = viewModel.profiles.observeAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val formatter = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault())



    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        topBar = {
            CenterAlignedTopAppBar(
                // Assuming CenterAlignedTopAppBar is also a Composable function
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text(
                        "Chats",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    androidx.compose.material3.IconButton(onClick = {
                        navController.popBackStack()
                    }) { // Use navController.popBackStack() to go back
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = "Localized description"
                        )
                    }
                },
                actions = {
                    androidx.compose.material3.IconButton(onClick = { /* do something */ }) {
                        Icon(
                            imageVector = Icons.Filled.Person,
                            contentDescription = "Localized description"
                        )
                    }
                },
            )
        }
    ) {
        Box(
            modifier = Modifier
                .padding(it)
                .fillMaxSize(),
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(5.dp),
                color = MaterialTheme.colorScheme.background,
                shape = RoundedCornerShape(8.dp),
                elevation = 10.dp
            ) {
                if (loading.value == true) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        LoadScreen()
                    }
                } else {
                    if (!profiles.value.isNullOrEmpty()) {
                        LazyColumn(
                            content = {

                                conversations.value!!.forEach { (key, value) ->
                                    item {
                                        ListItem(
                                            modifier = Modifier
                                                .height(105.dp)
                                                .background(Color.Transparent)
                                                .clickable {
                                                    navController.navigate(AppScreens.ChatScreen.route + "/${key}")
                                                },
                                            headlineContent = {
                                                Text(
                                                    text = profiles.value!![key]!!.fullName.toString()
                                                        .replaceFirstChar {
                                                            if (it.isLowerCase()) it.titlecase(
                                                                Locale.getDefault()
                                                            ) else it.toString()
                                                        }, fontSize = 20.sp,
                                                    modifier = Modifier.padding(bottom = 5.dp)
                                                )
                                            },
                                            trailingContent = {
                                                Box(
                                                    modifier = Modifier.fillMaxHeight(),
                                                    contentAlignment = Alignment.TopCenter
                                                ) {
                                                    Text(text = formatter.format(value.date!!.toDate()))
                                                }
                                            },
                                            supportingContent = {
                                                Text(
                                                    text = value.lastMessage ?: ""
                                                )
                                            },
                                            leadingContent = {
                                                Box(
                                                    modifier = Modifier
                                                        .padding(horizontal = 4.dp)
                                                        .size(90.dp)
                                                        .clip(CircleShape)
                                                        .background(Color.White)
                                                ) {

                                                    val imageProfile =
                                                        profiles.value!![key]!!.imageProfile

                                                    Image(
                                                        painter = rememberAsyncImagePainter(
                                                            ImageRequest.Builder(
                                                                LocalContext.current
                                                            ).data(data = imageProfile)
                                                                .apply(block = fun ImageRequest.Builder.() {
                                                                    crossfade(true)
                                                                }).build()
                                                        ),
                                                        contentDescription = "Profile image",
                                                        modifier = Modifier.size(100.dp),
                                                        contentScale = ContentScale.Crop
                                                    )
                                                }
                                            }
                                        )
                                    }
                                    item {
                                        HorizontalDivider()
                                    }
                                }
                            }
                        )


                    }
                }
            }

        }

    }
}


