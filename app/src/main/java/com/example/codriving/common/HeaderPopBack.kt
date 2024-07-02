package com.example.codriving.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.NavController
import com.example.codriving.navigation.AppScreens
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeaderPopBack(navController: NavController, TextHead: String?) {
    val expanded = remember {
        mutableStateOf(false)
    }

    CenterAlignedTopAppBar(
        // Assuming CenterAlignedTopAppBar is also a Composable function
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ),
        title = {
            Text(
                TextHead ?: "",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) { // Use navController.popBackStack() to go back
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = "Localized description"
                )
            }
        },
        actions = {
            IconButton(onClick = { expanded.value = true }) {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = "Profile"
                )
            }
            DropdownMenu(
                expanded = expanded.value,
                onDismissRequest = { expanded.value = false }
            ) {
                DropdownMenuItem(
                    trailingIcon = {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null
                        )
                    },
                    text = {
                        Text(
                            text = "Profile"
                        )
                    },
                    onClick = {
                        navController.navigate(
                            AppScreens.ProfileScreen.route,
                        )
                        expanded.value = false
                    }
                )

                DropdownMenuItem(
                    trailingIcon = {
                        Icon(
                            Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = null
                        )
                    },
                    text = {
                        Text(
                            text = "Log Out"
                        )
                    },
                    onClick = {
                        FirebaseAuth.getInstance().signOut()
                        navController.navigate(AppScreens.LoginScreen.route)
                        expanded.value = false
                    }
                )
            }
        },
    )
}
