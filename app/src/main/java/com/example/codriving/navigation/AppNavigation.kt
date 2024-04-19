package com.example.codriving.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.codriving.screens.BookRent.BookRentScreen
import com.example.codriving.screens.BookRent.BookRentViewModel
import com.example.codriving.screens.HomePage.HomePage
import com.example.codriving.screens.LoginPage.ui.LoginScreen
import com.example.codriving.screens.LoginPage.ui.LoginViewModel
import com.example.codriving.screens.MyCars.CarsFormScreen
import com.example.codriving.screens.MyCars.CarsFormViewModel
import com.example.codriving.screens.MyCars.ListMyCarsScreen
import com.example.codriving.screens.RentCar.RentCarScreen
import com.example.codriving.screens.RentCar.RentCarViewModel
import com.example.codriving.screens.SignUp.SignScreen
import com.google.firebase.auth.FirebaseAuth

@Composable
fun AppNavigation (
     navController: NavHostController = rememberNavController(), auth: FirebaseAuth
) {
    val auth = FirebaseAuth.getInstance()


    NavHost(navController = navController, startDestination = AppScreens.LoginScreen.route) {
        composable(route = AppScreens.HomeScreen.route) {
            HomePage(navController)
        }

        composable(
            route = AppScreens.RentCarScreen.route + "/{idRentCar}",
            arguments = listOf(navArgument(name = "idRentCar") {
                type = NavType.StringType
            })
        ) { backStackEntry ->
            val rentCarViewModel: RentCarViewModel = hiltViewModel() // Access ViewModel
            RentCarScreen(
                navController,
                rentCarViewModel,
                backStackEntry.arguments?.getString("idRentCar"),
            )
        }
        composable(route = AppScreens.LoginScreen.route) {


            val loginViewModel: LoginViewModel = hiltViewModel()

            LoginScreen(navController, loginViewModel) {

                if (auth.currentUser != null) {
                    navController.navigate(AppScreens.HomeScreen.route)
                }

                navController.navigate(AppScreens.HomeScreen.route) {
                    popUpTo(navController.graph.startDestinationId) {
                        inclusive =
                            true
                    }

                }
            }
        }
        composable(AppScreens.SignInScreen.route) {

            SignScreen(navController = navController)
        }

        composable(
            AppScreens.CarsFormScreen.route + "/{carId}",
            arguments = listOf(navArgument("carId") { type = NavType.StringType; nullable = true })
        ) { navBackStackEntry ->
            val CarsFormViewModel: CarsFormViewModel = hiltViewModel()
            val carId = navBackStackEntry.arguments?.getString("carId")
            LaunchedEffect(carId) {
                carId?.let {
                    CarsFormViewModel.loadCarDetails(carId)
                }
            }
            CarsFormScreen(navController = navController, CarsFormViewModel)

        }



        composable(AppScreens.CarsFormScreen.route) {
            CarsFormScreen(navController = navController)
        }

        composable(AppScreens.ListMyCarsScreen.route) {
            ListMyCarsScreen(navController = navController)
        }

        composable(AppScreens.ListMyCarsScreen.route) {
            ListMyCarsScreen(navController = navController)
        }

        composable(
            AppScreens.BookRentScreen.route + "/{rentCar}",
            arguments = listOf(navArgument("rentCar") {
                type = NavType.StringType; nullable = true
            })
        ) { navBackStackEntry ->
            val BookRentViewModel: BookRentViewModel = hiltViewModel()
            val carId = navBackStackEntry.arguments?.getString("rentCar")
            LaunchedEffect(carId) {
                carId?.let {
                    BookRentViewModel.loadDetails(carId)
                }
            }
            BookRentScreen(navController = navController, BookRentViewModel)

        }

    }
}