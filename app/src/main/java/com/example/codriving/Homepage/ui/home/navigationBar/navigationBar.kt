package com.example.codriving.Homepage.ui.home.navigationBar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import com.example.codriving.R


@Composable
fun navigationBar() {
    val IconCar: ImageVector = ImageVector.vectorResource(id = R.drawable.iconcar)
    val IconChat: ImageVector = ImageVector.vectorResource(id = R.drawable.iconchat)

    var iconsBar = listOf(Icons.Filled.Home, IconCar, IconChat, Icons.Filled.Info)
    val items = listOf("Home", "Rentals", "Chats", "Help")
    var selectedItem by remember { mutableIntStateOf(0) }

    NavigationBar {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = { Icon(iconsBar[index], contentDescription = item) },
                label = { Text(item) },
                selected = selectedItem == index,
                onClick = { selectedItem = index }
            )
        }
    }
}
