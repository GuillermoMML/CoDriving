package com.example.codriving.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.codriving.Homepage.ui.home.HomePage
import com.example.codriving.Homepage.ui.home.HomeViewModel
import com.example.codriving.RentCar.ui.RentCarScreen
import com.example.codriving.RentCar.ui.RentCarViewModel
import com.example.codriving.Searchpage.ui.viewSearch.SearchPage

@Composable
fun AppNavigation() {

    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = AppScreens.HomeScreen.route) {
        composable(route = AppScreens.HomeScreen.route) {
            val viewHome: HomeViewModel = viewModel()
            HomePage(navController, viewHome)
        }
        composable(route = AppScreens.SearchScreen.route) {
            SearchPage(navController)
        }
        composable(
            route = AppScreens.RentCarScreen.route + "/{idRentCar}",
            arguments = listOf(navArgument(name = "idRentCar") {
                type = NavType.IntType
            })
        ) { backStackEntry ->
            val rentCarViewModel: RentCarViewModel = viewModel() // Access ViewModel

            RentCarScreen(
                navController,
                rentCarViewModel,
                backStackEntry.arguments?.getInt("idRentCar"),
            )
        }


    }
}