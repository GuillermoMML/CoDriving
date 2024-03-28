package com.example.codriving.navigation

sealed class AppScreens(val route:String){
    object HomeScreen: AppScreens("HomeScreen")
    object SearchScreen: AppScreens("SearchScreen")
    object RentCarScreen: AppScreens("RentCarScreen")
    object LoginScreen: AppScreens("LoginScreen")
    object SignInScreen: AppScreens("SignInScreen")
    object CarsFormScreen: AppScreens("CarsFromScreen/{cardId}")

    object ListMyCarsScreen: AppScreens("MyListCars")

    object MyRentalsScreen: AppScreens("MyRentalsScreen")


}
