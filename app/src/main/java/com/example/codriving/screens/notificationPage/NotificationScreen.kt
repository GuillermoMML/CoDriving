package com.example.codriving.screens.notificationPage

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissState
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material.rememberDismissState
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.codriving.common.HeaderPopBack
import com.example.codriving.data.model.Notification
import com.example.codriving.data.model.RequestNotification
import com.example.codriving.screens.MyCars.LoadScreen
import com.google.firebase.firestore.DocumentReference
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
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
    val snackbarHostState = remember { SnackbarHostState() }

    val errorMessage = viewModel.error.observeAsState()

    val refreshState = rememberPullRefreshState(
        refreshing = viewModel.resfresh,
        onRefresh = { scope.launch { viewModel.setRefresh() } })
    val isLoading = viewModel.isLoading.observeAsState()
    val showDialogCancel =
        remember { mutableStateOf(false) } // Variable para controlar la ventana modal
    val showDialogResult =
        remember { mutableStateOf(false) } // Variable para controlar la ventana modal
    val showInfoDialog = remember {
        mutableStateOf(false)
    }
    val currentNotify = viewModel.currentNotifies.observeAsState()
    val info = viewModel.infoMessage.observeAsState()
    val context = LocalContext.current


    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        topBar = {
            HeaderPopBack(navController = navController)
        },
        content = {
            if (showInfoDialog.value) {
                LaunchedEffect(key1 = currentNotify) {
                    viewModel.getRentsString()
                }
                BasicAlertDialog(
                    onDismissRequest = {
                        showInfoDialog.value = false
                        viewModel.setInfoMessage()
                    },
                ) {
                    if (info.value.equals("")) {
                    } else {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            shape = RoundedCornerShape(16.dp),
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    modifier = Modifier.fillMaxWidth(),
                                    text = "Dias de alquiler",
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    modifier = Modifier.fillMaxWidth(),
                                    text = info.value!!,
                                    textAlign = TextAlign.Center
                                )
                            }

                        }

                    }

                }

            }
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
                            ListNotifies(
                                notificationThisWeek.value,
                                userNotification,
                                onShowInfo = {
                                    showInfoDialog.value = true
                                    viewModel.setCurretNotify(it)
                                },
                                onConfirm = {
                                    scope.launch {

                                        try {
                                            // Llama al método en el ViewModel para generar el PDF
                                            val file = viewModel.generateModelPDF(context)
                                            // Muestra un mensaje de éxito después de generar el PDF
                                            if (!file.isNullOrEmpty()) {
                                                val result = snackbarHostState
                                                    .showSnackbar(
                                                        message = "Se generó el contrato",
                                                        actionLabel = "Ver",
                                                        // Defaults to SnackbarDuration.Short
                                                        duration = SnackbarDuration.Long
                                                    )
                                                when (result) {
                                                    SnackbarResult.ActionPerformed -> {
                                                        val uri = FileProvider.getUriForFile(
                                                            context,
                                                            context.applicationContext.packageName + ".provider",
                                                            File(file)
                                                        )
                                                        val intent =
                                                            Intent(Intent.ACTION_VIEW).apply {
                                                                flags =
                                                                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
                                                                setDataAndType(
                                                                    uri,
                                                                    "application/pdf"
                                                                )
                                                            }
                                                        context.applicationContext.startActivity(
                                                            intent
                                                        )
                                                    }

                                                    SnackbarResult.Dismissed -> {
                                                        /* Handle snackbar dismissed */
                                                    }
                                                }
                                            } else {
                                                snackbarHostState.showSnackbar("Error al generar el pdf")

                                            }

                                        } catch (e: Exception) {
                                            // En caso de error, muestra un mensaje de error
                                            snackbarHostState.showSnackbar("Error al generar el contrato ${e.message}")

                                        }
                                    }

                                },
                                onDelete = {
                                    if (it.type == 1) {
                                        showDialogCancel.value = true
                                        viewModel.removeNotify(it, 1)

                                    } else {
                                        viewModel.removeNotify(it, 2)

                                    }

                                })

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
                            ListNotifies(
                                notificationLastWeek.value, userNotification,
                                onShowInfo = {
                                    showInfoDialog.value = true
                                    viewModel.setCurretNotify(it)
                                },
                                onConfirm = {},
                                onDelete = {
                                    if (it.type == 1) {
                                        showDialogCancel.value = true
                                        viewModel.removeNotify(it, 1)

                                    } else {
                                        viewModel.removeNotify(it, 2)

                                    }

                                }

                            )
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

    if (showDialogCancel.value) {
        modalCancelNotify(
            viewModel = viewModel,
            onDismissRequest = { showDialogCancel.value = false }) {
            showDialogCancel.value = false
            showDialogResult.value = true

        }
    }
    if (showDialogResult.value) {
        var auxError = "Se ha eliminado la petición con exito"
        if (!errorMessage.value.isNullOrEmpty()) {
            auxError = errorMessage.value!!
        }
        BasicDialog(showDialogResult, auxError, viewModel)
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BasicDialog(
    showDialogResult: MutableState<Boolean>,
    resultMessage: String,
    viewModel: NotificationViewModel
) {
    BasicAlertDialog(onDismissRequest = {
        showDialogResult.value = false
    }) {

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(5.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)

            ) {
                Icon(
                    Icons.Default.Cancel,
                    contentDescription = null,
                    Modifier.padding(20.dp)
                )
                Text(text = resultMessage, textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.weight(1f))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.Center
                ) {
                    TextButton(
                        modifier = Modifier.padding(5.dp),
                        onClick = {
                            showDialogResult.value = false
                            viewModel.setError()
                        }) {
                        Text(text = "Ok")
                    }

                }
            }
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun modalCancelNotify(
    viewModel: NotificationViewModel,
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit
) {
    var cancelCauseText by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue("", TextRange(0, 150)))
    }
    BasicAlertDialog(onDismissRequest = {
        onDismissRequest()
    }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(5.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)

            ) {
                Icon(
                    Icons.Default.Cancel,
                    contentDescription = null,
                    Modifier.padding(20.dp)
                )
                OutlinedTextField(modifier = Modifier.fillMaxWidth(),
                    value = cancelCauseText,
                    onValueChange = { cancelCauseText = it },
                    label = { Text(text = "¿Por qué quieres rechazar la petición?") })
                Spacer(modifier = Modifier.weight(1f))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.Center
                ) {
                    TextButton(
                        modifier = Modifier.padding(5.dp),
                        onClick = {
                            viewModel.removeNotify(cancelCauseText.text)
                            viewModel.removeNotify(Notification(), 0)
                            onConfirm()
                        }) {
                        Text(text = "Aceptar")
                    }
                    TextButton(modifier = Modifier.padding(5.dp), onClick = {
                        viewModel.removeNotify(Notification(), 0)
                        onDismissRequest()

                    }) {
                        Text(text = "Cancelar")
                    }
                }
            }
        }
    }

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListNotifies(
    notificationlist: MutableList<Notification>?,
    userNotification: State<HashMap<String, RequestNotification>?>,
    onDelete: (Notification) -> Unit,
    onShowInfo: (List<DocumentReference>) -> Unit,
    onConfirm: (Notification) -> Unit
) {

    ElevatedCard(modifier = Modifier.padding(10.dp)) {
        if (!notificationlist.isNullOrEmpty()) {
            Column {
                notificationlist.forEach {
                    when (it.type) {
                        1 -> {
                            userNotifyItem(
                                notify = it,
                                userNotification = userNotification.value!![it.idSender]!!,
                                onCancel = { onDelete(it) },
                                onShowInfo = { onShowInfo(it.rentsCars) },
                                onConfirm = { onConfirm(it) }
                            )

                        }

                        2 -> {
                            SwipeToDeleteContainer(
                                item = it,
                                onDelete = {
                                    onDelete(it)
                                }
                            ) { notify ->
                                infoNotify(
                                    notify = notify,
                                    userNotification = userNotification.value!![it.idSender]!!,
                                    onCancel = { onDelete(notify) }
                                )
                            }
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

@Composable
fun infoNotify(
    notify: Notification,
    userNotification: RequestNotification,
    onCancel: (Notification) -> Unit
) {
    val user = userNotification.user
    val car = userNotification.car
    var imageProfile = ""
    var isRemoved by remember {
        mutableStateOf(false)
    }
    LaunchedEffect(key1 = isRemoved) {
        if (isRemoved) {
            onCancel(notify)
        }
    }

    ListItem(
        headlineContent = { Text(user.fullName.toString()) },
        supportingContent = {
            Text(notify.message.toString())
        },
        leadingContent = {
            Box(
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .size(60.dp)
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
                    contentDescription = "Profile image",
                    modifier = Modifier.size(100.dp),
                    contentScale = ContentScale.Crop
                )
            }
        },
        trailingContent = {
            Column(modifier = Modifier.width(100.dp)) {
                Text(
                    "${formatDateToDayMonthYear(notify.timestamp!!.toDate())}",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    )

}

fun formatDateToDayMonthYear(date: Date): String {
    val formatter = SimpleDateFormat("dd/MM/yyyy") // Replace with your desired format
    return formatter.format(date)
}


@Composable
fun userNotifyItem(
    notify: Notification,
    userNotification: RequestNotification,
    onCancel: (Notification) -> Unit,
    onShowInfo: () -> Unit,
    onConfirm: () -> Unit
) {
    val user = userNotification.user
    var imageProfile = ""
    var isRemoved by remember {
        mutableStateOf(false)
    }
    LaunchedEffect(key1 = isRemoved) {
        if (isRemoved) {
            onCancel(notify)
        }
    }

    ListItem(
        headlineContent = { Text(user.fullName.toString()) },
        supportingContent = {
            Text(notify.message.toString())
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
                    contentDescription = "Profile image",
                    modifier = Modifier.size(100.dp),
                    contentScale = ContentScale.Crop
                )
            }
        },
        trailingContent = {
            Column(modifier = Modifier.width(100.dp)) {
                Text(
                    "${formatDateToDayMonthYear(notify.timestamp!!.toDate())}",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(
                        onClick = {
                            onConfirm()
                        },
                    ) {
                        Icon(
                            Icons.Default.Check, contentDescription = null,
                        )
                    }
                    IconButton(
                        onClick = { onCancel(notify) },
                    ) {
                        Icon(
                            Icons.Default.Cancel,
                            contentDescription = null,

                            )
                    }
                }
                Box(modifier = Modifier.align(Alignment.End)) {
                    IconButton(
                        onClick = {
                            onShowInfo()
                        }) {
                        Icon(Icons.Default.Info, contentDescription = "Info")

                    }

                }
            }

        }
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