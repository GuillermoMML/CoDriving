package com.example.codriving.screens.Chat

import android.annotation.SuppressLint
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.codriving.data.model.Message
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import java.text.SimpleDateFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun chatScreen(navController: NavController, viewModel: ChatViewModel) {
    val messages by viewModel.messages.collectAsState()


    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text(
                        "Chats",
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* do something */ }) {
                        Icon(
                            imageVector = Icons.Filled.Person,
                            contentDescription = "Profile"
                        )
                    }
                },
            )
        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)

                ) {
                    items(messages) { message ->
                        if (message.idSender.isNotEmpty()) {
                            MessageItem(
                                message = message,
                                isOwnMessage = message.idSender == Firebase.auth.uid.toString()
                            )
                        }


                    }

                }
                MessageInput(viewModel) { message ->
                    viewModel.sendMessage(message)
                }

            }

        }
    )
}


@Composable
fun MessageItem(message: Message, isOwnMessage: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isOwnMessage) Arrangement.End else Arrangement.Start
    ) {
        Card(
            modifier = Modifier
                .padding(4.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isOwnMessage) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary,
                contentColor = if (isOwnMessage) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onTertiary
            )
        ) {
            Column(
                modifier = Modifier.padding(8.dp)
            ) {
                if (message.type_message == 1) {
                    // Display image
                    Image(
                        painter = rememberAsyncImagePainter(
                            ImageRequest.Builder(
                                LocalContext.current
                            ).data(data = message.message)
                                .apply(block = fun ImageRequest.Builder.() {
                                    crossfade(true)
                                }).build()
                        ),
                        contentDescription = "Profile image",
                        modifier = Modifier.size(200.dp),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    // Display text
                    Text(text = message.message)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = formatTimestampToTime(message.date),
                    textAlign = TextAlign.End,
                    fontSize = 10.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun MessageInput(viewModel: ChatViewModel, onSend: (String) -> Unit) {
    var selectedImages by remember {
        mutableStateOf<List<Uri?>>(emptyList())
    }
    val context = LocalContext.current
    var message by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current
    val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> selectedImages = listOf(uri) }
    )
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        value = message,
        onValueChange = { message = it },
        trailingIcon = {
            IconButton(onClick = {
                keyboardController?.hide()
                if (message.isNotEmpty()) {
                    onSend(message)
                    message = ""
                }
            }) {
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null)
            }
        },
        label = { Text(text = "Type your message") },
        leadingIcon = {
            IconButton(onClick = {
                launchPhotoPicker(singlePhotoPickerLauncher)
            }) {
                Icon(Icons.Default.Image, contentDescription = null)
            }
        }
    )
    selectedImages.firstOrNull()?.let { uri ->
        LaunchedEffect(uri) {
            viewModel.uploadImage(
                uri = uri,
                onSuccess = { imageUrl ->
                    viewModel.sendMessage(imageUrl, 1)
                    selectedImages = emptyList() // Clear selected images after sending
                },
                onFailure = { e ->
                    Toast.makeText(
                        context,
                        "Failed to upload image: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            )
        }
    }

}


@SuppressLint("SimpleDateFormat")
fun formatTimestampToTime(timestamp: com.google.firebase.Timestamp): String {
    val formatter = SimpleDateFormat("HH:mm")
    return formatter.format(timestamp.toDate())
}

fun launchPhotoPicker(singlePhotoPickerLauncher: ManagedActivityResultLauncher<PickVisualMediaRequest, Uri?>) {
    singlePhotoPickerLauncher.launch(
        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
    )
}