package com.example.codriving.screens.notificationPage

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissState
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material.rememberDismissState
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.codriving.common.HeaderPopBack
import com.example.codriving.data.model.Notification
import com.example.codriving.data.model.RequestNotification
import com.example.codriving.screens.MyCars.LoadScreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun notificationView(
    navController: NavController,
    viewModel: NotificationViewModel = hiltViewModel()
) {
    val notificationThisWeek = viewModel.notificationsThisWeek.observeAsState()
    val notificationLastWeek = viewModel.notificationsLastWeek.observeAsState()
    val userNotification = viewModel.userNotification.observeAsState()
    val scope = rememberCoroutineScope()

    val refreshState = rememberPullRefreshState(
        refreshing = viewModel.resfresh,
        onRefresh = { scope.launch { viewModel.setRefresh() } })

    val isLoading = viewModel.isLoading.observeAsState()
    Scaffold(
        topBar = {
            HeaderPopBack(navController = navController)
        },
        content = {
            Box(modifier = Modifier.fillMaxSize()) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(it)
                        .pullRefresh(refreshState)
                ) {
                    if (isLoading.value == false) {
                        item {
                            Text(
                                text = "This week",
                                modifier = Modifier.padding(10.dp),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,

                                )
                        }
                        item {
                            ListNotifies(notificationThisWeek.value, userNotification) {
                                viewModel.removeNotifyThisWeek(it)
                            }
                        }
                        item {
                            Text(
                                text = "Last Week",
                                modifier = Modifier.padding(10.dp),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,

                                )
                        }
                        item {
                            ListNotifies(notificationLastWeek.value, userNotification) {
                                viewModel.removeNotifyLastWeek(it)
                            }
                        }

                    } else {
                        item { LoadScreen() }
                    }
                }
                PullRefreshIndicator(
                    refreshing = viewModel.resfresh,
                    state = refreshState,
                    modifier = Modifier.align(
                        Alignment.TopCenter
                    )
                )

            }
        })
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListNotifies(
    notificationlist: MutableList<Notification>?,
    userNotification: State<HashMap<String, RequestNotification>?>,
    onDelete: (Notification) -> Unit
) {

    ElevatedCard(modifier = Modifier.padding(10.dp)) {
        if (!notificationlist.isNullOrEmpty()) {
            Column {
                notificationlist.forEach {
                    when (it.type) {
                        1 -> {
                            SwipeToDeleteContainer(
                                item = it,
                                onDelete = {
                                    onDelete(it)
                                }
                            ) { notify ->
                                userNotifyItem(
                                    notify = notify,
                                    userNotification = userNotification.value!![it.idSender]!!
                                )
                            }
                        }

                        2 -> {
                            Text(text = "Tipo 2 en desarrollo")
                        }
                    }
                    HorizontalDivider()
                }

            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No se encontro ninguna notificación",
                    textAlign = TextAlign.Center
                )
            }
        }
    }

}

fun formatDateToDayMonthYear(date: Date): String {
    val formatter = SimpleDateFormat("dd/MM/yyyy") // Replace with your desired format
    return formatter.format(date)
}


@Composable
fun userNotifyItem(notify: Notification, userNotification: RequestNotification) {
    val user = userNotification.user
    val car = userNotification.car
    var imageProfile = ""

    ListItem(
        headlineContent = { Text(user.fullName.toString()) },
        supportingContent = {
            Text("Esta interesado en tu coche ${car.model}")
        },
        leadingContent = {
            Box(
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .size(90.dp)
                    .clip(CircleShape)
                    .background(Color.White)
            ) {
                imageProfile =
                    "https://static-00.iconduck.com/assets.00/profile-default-icon-2048x2045-u3j7s5nj.png"


                Image(
                    painter = rememberAsyncImagePainter(
                        ImageRequest.Builder(
                            LocalContext.current
                        ).data(data = imageProfile)
                            .apply(block = fun ImageRequest.Builder.() {
                                crossfade(true)
                            }).build()
                    ),
                    contentDescription = "Prfole image",
                    modifier = Modifier.size(100.dp),
                    contentScale = ContentScale.Crop
                )
            }
        },
        trailingContent = { Text("${formatDateToDayMonthYear(notify.timestamp!!.toDate())}") }
    )

}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun <T> SwipeToDeleteContainer(
    item: T,
    onDelete: (T) -> Unit,
    animationDuration: Int = 500,
    content: @Composable (T) -> Unit
) {
    var isRemoved by remember {
        mutableStateOf(false)
    }
    val state = rememberDismissState(
        confirmStateChange = { value ->
            if (value == DismissValue.DismissedToStart) {
                isRemoved = true
                true
            } else {
                false
            }
        }
    )

    LaunchedEffect(key1 = isRemoved) {
        if (isRemoved) {
            delay(animationDuration.toLong())
            onDelete(item)
        }
    }

    AnimatedVisibility(
        visible = !isRemoved,
        exit = shrinkVertically(
            animationSpec = tween(durationMillis = animationDuration),
            shrinkTowards = Alignment.Top
        ) + fadeOut()
    ) {
        SwipeToDismiss(
            state = state,
            background = {
                DeleteBackground(swipeDismissState = state)
            },
            dismissContent = { content(item) },
            directions = setOf(DismissDirection.EndToStart)
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DeleteBackground(
    swipeDismissState: DismissState
) {
    val color = if (swipeDismissState.dismissDirection == DismissDirection.EndToStart) {
        Color.Red
    } else Color.Transparent

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color)
            .padding(16.dp),
        contentAlignment = Alignment.CenterEnd
    ) {
        Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = null,
            tint = Color.White
        )
    }
}