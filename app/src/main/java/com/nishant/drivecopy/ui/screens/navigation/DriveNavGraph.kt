package com.nishant.drivecopy.ui.screens.navigation

import androidx.compose.runtime.Composable

import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.nishant.drivecopy.ui.screens.HomeScreen
import com.nishant.drivecopy.ui.screens.auth.AuthenticateUser
import com.nishant.drivecopy.ui.screens.splash.SplashScreen

@Composable
fun DriveNavGraph(){

    val navHostController = rememberNavController()
    NavHost(navController = navHostController, startDestination = "Splash"){
        composable("Splash"){
            SplashScreen(navigateToAuth = { navHostController.navigate("auth") },
                navigateToHome = { navHostController.navigate("home") })
        }
        composable("auth"){
            AuthenticateUser(){
                navHostController.navigate("home")
            }
        }

        composable("home"){
            HomeScreen()
        }
    }
}