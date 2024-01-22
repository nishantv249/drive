package com.nishant.drivecopy.ui.screens.auth

import android.app.Activity
import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.nishant.drivecopy.R
import com.nishant.drivecopy.ui.components.OtpInput
import java.util.concurrent.TimeUnit


@Composable
fun AuthenticateUser(onUserAuthenticated : () -> Unit) {
    val phoneNumber = remember {
        mutableStateOf("")
    }
    val isOtpSent = remember {
        mutableStateOf(false)
    }

    val verificationId = remember {
        mutableStateOf("")
    }

    val forceResendingToken: MutableState<PhoneAuthProvider.ForceResendingToken?> = remember {
        mutableStateOf(null)
    }

    val phoneAuthCredential: MutableState<PhoneAuthCredential?> = remember {
        mutableStateOf(null)
    }

    if (!isOtpSent.value) {
        SendOtpScreen(
            phoneNumber,
            isOtpSent,
            verificationId,
            forceResendingToken,
            phoneAuthCredential
        )
    } else {
        VerifyOtpScreen(phoneNumber, verificationId, forceResendingToken, phoneAuthCredential,onUserAuthenticated)
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SendOtpScreen(
    phoneNumber: MutableState<String>,
    isOtpSent: MutableState<Boolean>,
    verificationId: MutableState<String>,
    forceResendingToken: MutableState<PhoneAuthProvider.ForceResendingToken?>,
    phoneAuthCredential: MutableState<PhoneAuthCredential?>
) {


    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = stringResource(R.string.otp_verification))
        Text(
            text = stringResource(R.string.otp_verification_sending_message),
            modifier = Modifier.padding(top = 24.dp)
        )

        TextField(value = phoneNumber.value, onValueChange = { number ->
            phoneNumber.value = number
        }, modifier = Modifier.padding(top = 24.dp))

        Button(onClick = {
            verifyPhoneNumber(
                phoneNumber.value,
                context,
                isOtpSent,
                verificationId,
                forceResendingToken,
                phoneAuthCredential
            )
        }, modifier = Modifier.padding(top = 24.dp)) {
            Text(text = stringResource(R.string.send_otp))
        }
    }

}

fun verifyPhoneNumber(
    phoneNumber: String,
    context: Context,
    isOtpSent: MutableState<Boolean>,
    verificationIdState: MutableState<String>,
    forceResendingTokenState: MutableState<PhoneAuthProvider.ForceResendingToken?>,
    phoneAuthCredential: MutableState<PhoneAuthCredential?>
) {
    val options = PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())
        .setPhoneNumber(phoneNumber)
        .setTimeout(60L, TimeUnit.SECONDS)
        .setActivity(context as Activity)
        .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(phoneAuthCred: PhoneAuthCredential) {
                phoneAuthCredential.value = phoneAuthCred
            }

            override fun onVerificationFailed(firebaseException: FirebaseException) {

            }

            override fun onCodeSent(
                verificationId: String,
                forceResendingToken: PhoneAuthProvider.ForceResendingToken
            ) {
                isOtpSent.value = true
                verificationIdState.value = verificationId
                forceResendingTokenState.value = forceResendingToken
            }

        })
        .build()
    PhoneAuthProvider.verifyPhoneNumber(options)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerifyOtpScreen(
    phoneNumber: MutableState<String>,
    verificationId: MutableState<String>,
    forceResendingToken: MutableState<PhoneAuthProvider.ForceResendingToken?>,
    phoneAuthCredential: MutableState<PhoneAuthCredential?>,
    onUserAutheticated: () -> Unit,
    verifyOtpViewModel: VerifyOtpViewModel = hiltViewModel()
) {

    var isVerifyEnabled by remember {
        mutableStateOf(false)
    }

    var otpText by remember {
        mutableStateOf("")
    }

    var otpSent = remember {
        mutableStateOf(false)
    }

    if (phoneAuthCredential.value != null) {
        isVerifyEnabled = false
        LaunchedEffect(key1 = phoneAuthCredential.value) {
            phoneAuthCredential.value?.let {
                verifyOtpViewModel.signInWithCredential(it)
            }
        }
    }

    LaunchedEffect(Unit) {
        verifyOtpViewModel.resetResend()
    }

    if (otpSent.value) {
        LaunchedEffect(key1 = otpSent.value) {
            verifyOtpViewModel.resetResend()
        }
    }

    val verifyOtpUiState by verifyOtpViewModel.uiState.collectAsState()

    if(verifyOtpUiState.isSigningSucceeded){
        onUserAutheticated()
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = stringResource(R.string.otp_verification))
        Text(
            text = stringResource(R.string.otp_verification_sending_message),
            modifier = Modifier.padding(top = 24.dp)
        )

        OtpInput(modifier = Modifier.padding(top = 24.dp)) { otp, isCompleted ->
            otpText = otp
            isVerifyEnabled = isCompleted
        }

        Button(onClick = {
            phoneAuthCredential.value =
                PhoneAuthProvider.getCredential(verificationId.value, otpText)
        }, modifier = Modifier.padding(top = 24.dp), enabled = isVerifyEnabled) {
            Text(text = stringResource(R.string.verify))
        }
        val context = LocalContext.current
        if (verifyOtpUiState.isResendEnabled) {
            Text(
                text = stringResource(R.string.resend),
                modifier = Modifier
                    .padding(top = 48.dp)
                    .clickable {
                        reVerifyPhoneNumber(
                            context,
                            otpSent,
                            phoneNumber.value,
                            verificationId,
                            forceResendingToken,
                            phoneAuthCredential
                        )
                    })

        } else {
            Text(
                text = "Resend OTP in ${verifyOtpUiState.secondsLeftToResend}",
                modifier = Modifier.padding(top = 48.dp)
            )
            otpSent.value = false
        }
    }

}

fun reVerifyPhoneNumber(
    context: Context,
    otpSent: MutableState<Boolean>,
    phoneNumber: String,
    verificationIdState: MutableState<String>,
    forceResendingTokenState: MutableState<PhoneAuthProvider.ForceResendingToken?>,
    phoneAuthCredential: MutableState<PhoneAuthCredential?>
) {
    val options = PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())
        .setPhoneNumber(phoneNumber)
        .setTimeout(60L, TimeUnit.SECONDS)
        .setActivity(context as Activity)
        .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(phoneAuthCred: PhoneAuthCredential) {
                phoneAuthCredential.value = phoneAuthCred
            }

            override fun onVerificationFailed(firebaseException: FirebaseException) {
                println(firebaseException.message)
            }

            override fun onCodeSent(
                verificationId: String,
                forceResendingToken: PhoneAuthProvider.ForceResendingToken
            ) {
                verificationIdState.value = verificationId
                forceResendingTokenState.value = forceResendingToken
                otpSent.value = true
            }

        })
        .setForceResendingToken(forceResendingTokenState.value!!)
        .build()
    PhoneAuthProvider.verifyPhoneNumber(options)
}

