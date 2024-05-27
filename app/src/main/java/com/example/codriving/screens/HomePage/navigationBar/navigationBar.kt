package com.example.codriving.screens.HomePage.navigationBar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.navigation.NavController
import com.example.codriving.R
import com.example.codriving.navigation.AppScreens


@Composable
fun navigationBar(navController: NavController) {
    val IconCar: ImageVector = ImageVector.vectorResource(id = R.drawable.iconcar)
    val IconChat: ImageVector = ImageVector.vectorResource(id = R.drawable.iconchat)

    val iconsBar = listOf(Icons.Filled.Home, IconCar, IconChat, Icons.Filled.Info)
    val items = listOf("Home", "Rentals", "Chats", "Notifications")
    var selectedItem by remember { mutableIntStateOf(0) }

    NavigationBar {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = { Icon(iconsBar[index], contentDescription = item) },
                label = { Text(item) },
                selected = selectedItem == index,
                onClick = {
                    if (index == 0) {
                        navController.navigate(AppScreens.HomeScreen.route)
                    }

                    if (index == 1) { // Check if it's the "Rentals" item (index 1)
                        navController.navigate(AppScreens.ListMyCarsScreen.route)
                    }
                    if (index == 2) {
                        navController.navigate(AppScreens.ConversationScreen.route)
                    }
                    if (index == 3) {
                        navController.navigate(AppScreens.NotificationScreen.route)
                    } else {
                        selectedItem = index
                    }
                }
            )
        }
    }
}
