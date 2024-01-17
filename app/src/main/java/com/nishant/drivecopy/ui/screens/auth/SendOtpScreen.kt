package com.nishant.drivecopy.ui.screens.auth

import android.app.Activity
import android.content.Context
import android.os.Looper
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.nishant.drivecopy.R
import com.nishant.drivecopy.ui.components.OtpInput
import java.util.concurrent.TimeUnit


@Composable
fun AuthenticateUser(){
    val isOtpSent  = remember {
        mutableStateOf(false)
    }

    val verificationId = remember {
        mutableStateOf("")
    }

    val forceResendingToken : MutableState<PhoneAuthProvider.ForceResendingToken?> = remember {
        mutableStateOf(null)
    }

    val phoneAuthCredential : MutableState<PhoneAuthCredential?> = remember {
        mutableStateOf(null)
    }

    if(!isOtpSent.value){
        SendOtpScreen(isOtpSent,verificationId,forceResendingToken,phoneAuthCredential)
    }else{
        VerifyOtpScreen(verificationId,forceResendingToken,phoneAuthCredential)
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SendOtpScreen(
    isOtpSent: MutableState<Boolean>,
    verificationId: MutableState<String>,
    forceResendingToken: MutableState<PhoneAuthProvider.ForceResendingToken?>,
    phoneAuthCredential: MutableState<PhoneAuthCredential?>
) {

    var phoneNumber by remember {
        mutableStateOf("")
    }

    val context = LocalContext.current

    Column (modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = stringResource(R.string.otp_verification))
        Text(text = stringResource(R.string.otp_verification_sending_message), modifier = Modifier.padding(top =24.dp))

        TextField(value = phoneNumber, onValueChange = {
            phoneNumber = it
        }, modifier = Modifier.padding(top =24.dp))

        Button(onClick = {
            verifyPhoneNumber(phoneNumber,context,isOtpSent,verificationId,forceResendingToken,phoneAuthCredential)
        }, modifier = Modifier.padding(top =24.dp)) {
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
        .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks(){

            override fun onVerificationCompleted(phoneAuthCred: PhoneAuthCredential) {
                phoneAuthCredential.value = phoneAuthCred
            }

            override fun onVerificationFailed(firebaseException : FirebaseException) {

            }

            override fun onCodeSent(verificationId: String, forceResendingToken : PhoneAuthProvider.ForceResendingToken) {
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
    verificationId: MutableState<String>,
    forceResendingToken: MutableState<PhoneAuthProvider.ForceResendingToken?>,
    phoneAuthCredential: MutableState<PhoneAuthCredential?>
) {

    var isVerifyEnabled by remember {
        mutableStateOf(false)
    }

    var otpText by remember {
        mutableStateOf("")
    }

    var isResendEnabled by remember {
        mutableStateOf(false)
    }

    if(phoneAuthCredential.value != null){
        isVerifyEnabled = false
        println("User Authenticated")
    }

    var handler : android.os.Handler? =  remember {
        null
    }

/*
    var time = produceState(initialValue = 60) {
           handler = android.os.Handler(Looper.getMainLooper()){ message ->
            val time = message.obj as Int
            if(time == 0){
                isResendEnabled = true
            }else{
                value = time
                handler?.sendMessageDelayed(android.os.Message.obtain(message),1000)
            }
            value = time
            true
        }
        val message = android.os.Message.obtain(handler,0,59)
        handler?.sendMessageDelayed(message,1000)
    }
*/

    Column (modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = stringResource(R.string.otp_verification))
        Text(text = stringResource(R.string.otp_verification_sending_message), modifier = Modifier.padding(top =24.dp))

        OtpInput( modifier = Modifier.padding(top =24.dp)){ otp, isCompleted ->
            otpText = otp
            isVerifyEnabled = isCompleted
        }

        Button(onClick = {
            phoneAuthCredential.value = PhoneAuthProvider.getCredential(verificationId.value,otpText)
        }, modifier = Modifier.padding(top =24.dp),enabled = isVerifyEnabled) {
            Text(text = stringResource(R.string.verify))
        }
        val context = LocalContext.current
        if(isResendEnabled){
            Button(onClick = {
                reVerifyPhoneNumber(context, "",verificationId,forceResendingToken,phoneAuthCredential)
            }) {
                Text(text = stringResource(R.string.resend), modifier = Modifier.padding(top =  48.dp))
            }
        }else{
            //Text(text = "Resend OTP in ${time.value}", modifier = Modifier.padding(top = 48.dp))
        }
    }

}

fun reVerifyPhoneNumber(
    context: Context,
    phoneNumber: String,
    verificationIdState: MutableState<String>,
    forceResendingTokenState: MutableState<PhoneAuthProvider.ForceResendingToken?>,
    phoneAuthCredential: MutableState<PhoneAuthCredential?>
) {
    val options = PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())
        .setPhoneNumber(phoneNumber)
        .setTimeout(60L, TimeUnit.SECONDS)
        .setActivity(context as Activity)
        .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks(){

            override fun onVerificationCompleted(phoneAuthCred: PhoneAuthCredential) {
                phoneAuthCredential.value = phoneAuthCred
            }

            override fun onVerificationFailed(firebaseException : FirebaseException) {

            }

            override fun onCodeSent(verificationId: String, forceResendingToken : PhoneAuthProvider.ForceResendingToken) {
                verificationIdState.value = verificationId
                forceResendingTokenState.value = forceResendingToken
            }

        })
        .setForceResendingToken(forceResendingTokenState.value!!)
        .build()
    PhoneAuthProvider.verifyPhoneNumber(options)
}

