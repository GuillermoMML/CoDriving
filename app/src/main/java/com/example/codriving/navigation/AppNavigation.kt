package com.example.codriving.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.codriving.Homepage.ui.home.HomePage
import com.example.codriving.Searchpage.ui.viewSearch.SearchPage

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = AppScreens.HomeScreen.route){
        composable(route= AppScreens.HomeScreen.route){
            HomePage(navController)
        }
        composable(route = AppScreens.SearchScreen.route){
            SearchPage(navController)
        }
    }
}