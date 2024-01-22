package com.nishant.drivecopy.ui.screens.splash

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class SplashScreenState(val isTimedOut : Boolean,val isUserLoggedIn : Boolean)
@HiltViewModel
class SplashViewModel @Inject constructor() : ViewModel() {

    private val handler = Handler(Looper.getMainLooper())
    private val _state = MutableStateFlow(SplashScreenState(false, isUserLoggedIn = false))
    val state = _state.asStateFlow()

    init{
        handler.postDelayed({
            _state.value = SplashScreenState(true, isUserLoggedIn())
        },3000)
    }

    private fun isUserLoggedIn(): Boolean {
        return FirebaseAuth.getInstance().currentUser != null
    }


}