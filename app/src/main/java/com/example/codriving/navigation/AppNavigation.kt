package com.example.codriving.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.codriving.R
import com.example.codriving.ui.theme.ThemeViewModel
import com.example.codriving.view.ChatPage.ChatViewModel
import com.example.codriving.view.ChatPage.ConversationScreen
import com.example.codriving.view.ChatPage.ConversationsViewModel
import com.example.codriving.view.ChatPage.chatScreen
import com.example.codriving.view.HomePage.HomePage
import com.example.codriving.view.HomePage.HomeViewModel
import com.example.codriving.view.LoginPage.ui.LoginScreen
import com.example.codriving.view.LoginPage.ui.LoginViewModel
import com.example.codriving.view.MyCarsPage.CarsFormScreen
import com.example.codriving.view.MyCarsPage.CarsFormViewModel
import com.example.codriving.view.MyCarsPage.ListMyCarsScreen
import com.example.codriving.view.ProfilePage.ProfileScreen
import com.example.codriving.view.ProfilePage.ProfileViewModel
import com.example.codriving.view.RentCarPage.RentCarScreen
import com.example.codriving.view.RentCarPage.RentCarViewModel
import com.example.codriving.view.ServicePage.BookRentScreen
import com.example.codriving.view.ServicePage.BookRentViewModel
import com.example.codriving.view.SignUpPage.SignScreen
import com.example.codriving.view.notificationPage.notificationView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    themeViewModel: ThemeViewModel
) {
    val isLoading = mutableStateOf(true)
    val scope = rememberCoroutineScope()
    NavHost(navController = navController, startDestination = AppScreens.LoginScreen.route) {
        composable(route = AppScreens.HomeScreen.route) {
            val homePageViewModifier: HomeViewModel = hiltViewModel()

            HomePage(navController, homePageViewModifier, themeViewModel)
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

            LaunchedEffect(Unit) {

                scope.launch(Dispatchers.IO) {
                    loginViewModel.getLogged()
                    delay(1000)
                    withContext(Dispatchers.Main) {
                        isLoading.value = false
                    }
                }
            }
            if (!isLoading.value) {
                if (loginViewModel.loginState.value!!.isLoggedIn) {
                    navController.navigate(AppScreens.HomeScreen.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            inclusive =
                                true
                        }
                    }
                } else {
                    LoginScreen(navController = navController, viewModel = loginViewModel) {
                        navController.navigate(AppScreens.HomeScreen.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                inclusive =
                                    true
                            }
                        }
                    }
                }

            }
            /*  LoginScreen(navController, loginViewModel) {
                  if (auth.currentUser != null) {
                      navController.navigate(AppScreens.HomeScreen.route)
                  } else {
                      isLogged()
                  }

              }*/
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

        composable(AppScreens.NotificationScreen.route) {
            notificationView(navController = navController)
        }

        composable(AppScreens.ConversationScreen.route) {
            val conversationsViewModel: ConversationsViewModel = hiltViewModel()
            ConversationScreen(navController = navController, conversationsViewModel)
        }

        composable(
            AppScreens.ChatScreen.route + "/{conversationId}",
            arguments = listOf(navArgument("conversationId") {
                type = NavType.StringType; nullable = false
            })
        ) { navBackStackEntry ->
            val chatViewModel: ChatViewModel = hiltViewModel()
            val conversationId = navBackStackEntry.arguments?.getString("conversationId")
            LaunchedEffect(conversationId) {
                conversationId?.let {
                    chatViewModel.fetchMessages(it)
                }
            }
            chatScreen(navController = navController, chatViewModel)

        }
        composable(AppScreens.ProfileScreen.route) {
            val profileViewModel: ProfileViewModel = hiltViewModel()
            ProfileScreen(navController = navController, profileViewModel)
        }

    }
}

@Composable
fun initialLoadingScreen() {
    val backgroundColor = Color(0xFF0748AB)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.ecofamily), // Cambia a tu recurso de ícono
                contentDescription = null,
                modifier = Modifier.size(400.dp) // Ajusta el tamaño del ícono según sea necesario
            )
            Spacer(modifier = Modifier.height(16.dp))
            CircularProgressIndicator()
        }
    }

}