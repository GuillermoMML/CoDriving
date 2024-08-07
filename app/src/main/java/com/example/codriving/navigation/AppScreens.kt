package com.example.codriving.navigation

sealed class AppScreens(val route:String){
    object HomeScreen : AppScreens("HomeScreen")
    object RentCarScreen : AppScreens("RentCarScreen")
    object LoginScreen : AppScreens("LoginScreen")
    object SignInScreen : AppScreens("SignInScreen")
    object CarsFormScreen : AppScreens("CarsFromScreen/{cardId}")
    object ListMyCarsScreen : AppScreens("MyListCars")
    object BookRentScreen : AppScreens("BookRentScreen")
    object NotificationScreen : AppScreens("NotificationScreen")

    object ConversationScreen : AppScreens("ConversationScreen")
    object ChatScreen : AppScreens("ChatScreen")
    object ProfileScreen : AppScreens("ProfileScreen")
}
