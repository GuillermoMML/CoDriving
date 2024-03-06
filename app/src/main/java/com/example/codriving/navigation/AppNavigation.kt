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
import com.example.codriving.LoginPage.ui.LoginScreen
import com.example.codriving.LoginPage.ui.LoginViewModel
import com.example.codriving.LoginPage.ui.SignScreen
import com.example.codriving.RentCar.ui.RentCarScreen
import com.example.codriving.RentCar.ui.RentCarViewModel
import com.example.codriving.Searchpage.ui.viewSearch.SearchPage

@Composable
fun AppNavigation(
    viewModel: LoginViewModel,
    navController: NavHostController = rememberNavController()
) {

    NavHost(navController = navController, startDestination = AppScreens.LoginScreen.route) {
        composable(route = AppScreens.HomeScreen.route) {
            HomePage(navController)
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
        composable(route = AppScreens.LoginScreen.route) {

            LoginScreen(navController,viewModel)
        }
        composable(AppScreens.SignInScreen.route) {

            SignScreen(navController = navController )
        }


    }
}