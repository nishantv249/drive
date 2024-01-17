package com.nishant.drivecopy.ui.screens.auth

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class SendOtpUIState(val isSendEnabled : Boolean,val isOtpRequested : Boolean)
@HiltViewModel
class SendOtpViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(SendOtpUIState(
        isSendEnabled = false,
        isOtpRequested = false
    ))

    val uiState = _uiState.asStateFlow()

    fun onSendOtpClicked() {

    }
}