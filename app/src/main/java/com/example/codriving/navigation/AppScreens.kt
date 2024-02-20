package com.example.codriving.navigation

sealed class AppScreens(val route:String){
    object HomeScreen: AppScreens("HomeScreen")
    object SearchScreen: AppScreens("SearchScreen")
    object RentCarScreen: AppScreens("RentCarScreen")
}
