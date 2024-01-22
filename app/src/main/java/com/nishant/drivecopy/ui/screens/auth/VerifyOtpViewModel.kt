package com.nishant.drivecopy.ui.screens.auth

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

data class VerifyOtpUiState(val secondsLeftToResend : Int, val isResendEnabled : Boolean,
                            val isSigningIn: Boolean, val isSigningSucceeded : Boolean, val isSigningFailed : Boolean,
                            val errorMessage: String)

@HiltViewModel
class VerifyOtpViewModel @Inject constructor() : ViewModel() {

    private val handler = Handler(Looper.getMainLooper())
    private var timeToResend = RESEND_TIME_OUT
    private val runnable : Runnable by lazy {
        Runnable {
            if(timeToResend > 0){
                handler.postDelayed(runnable,1000)
            }
            _uiState.value = uiState.value.copy(
                secondsLeftToResend = timeToResend,
                isResendEnabled = timeToResend == 0
            )
            timeToResend --
        }
    }

    private val _uiState = MutableStateFlow(VerifyOtpUiState(RESEND_TIME_OUT, isResendEnabled = false, isSigningIn = false,
        isSigningSucceeded = false,false,""))
    val uiState = _uiState.asStateFlow()

    fun resetResend(){
        timeToResend = RESEND_TIME_OUT
        handler.removeCallbacks(runnable)
        handler.postDelayed(runnable,1000)
    }

    fun signInWithCredential(credential : PhoneAuthCredential){
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSigningIn = true)
            val task = FirebaseAuth.getInstance().signInWithCredential(credential).await()
            if(task.user != null){
                _uiState.value = _uiState.value.copy(isSigningSucceeded = true, isSigningIn = false)
            }else{
                _uiState.value = _uiState.value.copy(isSigningIn = false, isSigningFailed = true)
            }
        }
    }

    companion object{
        const val RESEND_TIME_OUT = 60
    }

}