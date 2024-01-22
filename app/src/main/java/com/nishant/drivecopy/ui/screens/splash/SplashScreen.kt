package com.nishant.drivecopy.ui.screens.splash

import android.window.SplashScreen
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun SplashScreen(navigateToAuth: () -> Unit,navigateToHome : () -> Unit, splashViewModel : SplashViewModel
    = hiltViewModel()){

    val splashState by splashViewModel.state.collectAsState()
    if(splashState.isTimedOut){
        if(splashState.isUserLoggedIn){
            navigateToHome()
        }else{
            navigateToAuth()
        }
    }

}