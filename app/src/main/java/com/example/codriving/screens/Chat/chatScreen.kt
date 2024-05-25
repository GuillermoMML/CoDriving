package com.example.codriving.screens.Chat

import android.annotation.SuppressLint
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import com.example.codriving.common.HeaderPopBack


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun chatScreen(navController: NavController) {
    Scaffold(
        topBar = {
            HeaderPopBack(navController = navController)
        },
        content = {

        }
    )
}

@Preview
@Composable
fun previewBody() {

}