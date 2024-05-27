package com.example.codriving.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.material.Card
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Textsms
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.codriving.data.model.Car
import com.example.codriving.data.model.Conversations
import com.example.codriving.data.model.Message
import com.example.codriving.screens.RentCar.RatingBar
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Date


@Composable
fun itemCarView(car: Car, onClickItem: (Boolean) -> Unit, onMessage: (Car) -> Unit) {
    val scope = rememberCoroutineScope()
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
            .padding(vertical = 6.dp)
            .clickable {
                onClickItem(true)
            },
        elevation = 4.dp
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Fondo de la imagen de usuario
            Image(
                painter = rememberAsyncImagePainter(car.image[0]),
                contentDescription = "Profile Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            // Capa superior con los botones y el nombre del usuario
            Surface(
                color = Color(0x44000000), // Color con alfa reducido
                modifier = Modifier.fillMaxSize()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(5.dp),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.End
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        FloatingActionButton(
                            onClick = {
                                scope.launch {
                                    val array = listOf(Firebase.auth.uid.toString(), car.owner!!.id)
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
                                        Firebase.firestore.collection("conversations")
                                            .add(conversation)

                                    // Get the document reference for the newly created conversation
                                    val docRef = conversationRef.await()
                                    Firebase.firestore.collection("conversations")
                                        .document(docRef.id).collection("messages").add(message)
                                        .await()

                                }
                                onMessage(car)
                            },
                            modifier = Modifier
                                .padding(8.dp)
                                .size(40.dp)
                        ) {
                            Icon(
                                Icons.Default.Textsms,
                                contentDescription = "Add",
                                tint = Color.White
                            )
                        }
                    }
                }
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(5.dp),
                    verticalArrangement = Arrangement.Bottom,
                    horizontalAlignment = Alignment.Start
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = car.model,
                            color = Color.White,
                            fontSize = 18.sp,
                            modifier = Modifier
                        )
                        Text(
                            text = "Kilometers: ${car.kilometers}",
                            color = Color.White,
                            fontSize = 18.sp,
                            modifier = Modifier
                        )
                    }
                    Spacer(modifier = Modifier.height(5.dp))
                    RatingBar(rating = 0.3)
                }
            }
        }
    }
}