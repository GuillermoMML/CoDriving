package com.example.codriving.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
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
import com.example.codriving.MyCars.CarsFormScreen
import com.example.codriving.MyCars.CarsFormViewModel
import com.example.codriving.MyCars.ListMyCarsScreen
import com.example.codriving.RentCar.ui.RentCarScreen
import com.example.codriving.RentCar.ui.RentCarViewModel
import com.example.codriving.Searchpage.ui.viewSearch.SearchPage
import com.example.codriving.SignUp.SignScreen

@Composable
fun AppNavigation(
    viewModel: LoginViewModel, navController: NavHostController = rememberNavController()
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

            LoginScreen(navController, viewModel)
        }
        composable(AppScreens.SignInScreen.route) {

            SignScreen(navController = navController)
        }

        composable(
            AppScreens.CarsFormScreen.route + "/{carId}",
            arguments = listOf(navArgument("carId") { type = NavType.StringType; nullable = true })
        ) {navBackStackEntry ->
            val viewModel : CarsFormViewModel = hiltViewModel()
            val carId = navBackStackEntry.arguments?.getString("carId")
            LaunchedEffect(carId) {
                carId?.let {
                    viewModel.loadCarDetails(carId)
                }
            }
            CarsFormScreen(navController = navController,viewModel)

        }



        composable(AppScreens.CarsFormScreen.route,) {
            CarsFormScreen(navController = navController)
        }

        composable(AppScreens.ListMyCarsScreen.route) {
            ListMyCarsScreen(navController = navController)
        }


    }
}