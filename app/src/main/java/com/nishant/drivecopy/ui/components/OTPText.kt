package com.nishant.drivecopy.ui.components


import android.view.KeyEvent
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.nativeKeyCode
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun OtpInput( otpLength : Int = 6,modifier : Modifier = Modifier, onOtpChanged : (otp : String, isValid : Boolean) -> Unit) {

    val otpValue = remember { MutableList(otpLength) { "" } }
    val focusRequesters =  remember {
        List(otpLength) { FocusRequester() }
    }

    val keyBoard = LocalSoftwareKeyboardController.current

    Row( horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = modifier) {
        repeat(otpLength) { index ->
            CharView(focusRequester = focusRequesters[index], {
                jumpToPreviousBox(focusRequesters,index)
            }) {
                if(it.isEmpty() && otpValue[index].isEmpty()){
                    jumpToPreviousBox(focusRequesters,index)
                    return@CharView
                }
                otpValue[index] = it
                val otp = getStringFrom(otpValue)
                onOtpChanged(otp, otpValue.size == otpLength)
                requestFocusForNextEmptyBox(index,focusRequesters,otpValue)
                if(otp.length == otpLength){
                    keyBoard?.hide()
                }
            }
        }
    }
}

fun jumpToPreviousBox(focusRequesters: List<FocusRequester>, index: Int) {
    if (index == 0) {
        return
    }
    focusRequesters[index - 1].requestFocus()
}

fun requestFocusForNextEmptyBox(
    currentIndex: Int,
    focusRequesters: List<FocusRequester>,
    otpValue: MutableList<String>
) {
    // determine the index of next empty Box in a circular way and forward first

    for (i in currentIndex until otpValue.size){
        if(otpValue[i].isEmpty()){
            focusRequesters[i].requestFocus()
            return
        }
    }

    for (i in 0 until currentIndex) {
        if (otpValue[i].isEmpty()) {
            focusRequesters[i].requestFocus()
            return
        }
    }
}

fun getStringFrom(otpValue: List<String>): String {
    var otp : String = ""
    otpValue.forEach {
        otp += it
    }
    return otp
}

@Composable
fun CharView(focusRequester: FocusRequester, goBack: ()-> Unit,  onText: (text: String) -> Unit){

    var text by remember { mutableStateOf("") }
    var isTextEmpty by remember {
        mutableStateOf(false)
    }

    var isFocused by remember { mutableStateOf(false) }
    val strokeWidth  = if(isFocused) 3.dp else 1.5.dp
    val color = if(isFocused) Color.Blue else Color.LightGray

    Box(modifier = Modifier
        .height(OTP_BOX_HEIGHT.dp)
        .width(OTP_BOX_WIDTH.dp)
        .border(strokeWidth, color, RoundedCornerShape(4.dp)), contentAlignment = Alignment.Center
    ) {
        BasicTextField(
            value = text, onValueChange = {
                if (it.length > 1) {
                    return@BasicTextField
                }
                text = it
                onText(text)
                if(text.isNotEmpty()){
                    isTextEmpty = false
                }
            }, modifier = Modifier
                .focusRequester(focusRequester)
                .onFocusChanged { focusState ->
                    isFocused = focusState.hasFocus
                }
                .onKeyEvent { event ->
                    if (event.key.nativeKeyCode == android.view.KeyEvent.KEYCODE_DEL) {
                        if (text.isEmpty() && !isTextEmpty) {
                            isTextEmpty = true
                        }else{
                            goBack()
                        }
                        true
                    } else {
                        false

                    }
                },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
            maxLines = 1
        )
    }
}

private const val OTP_BOX_HEIGHT = 60
private const val OTP_BOX_WIDTH = 40