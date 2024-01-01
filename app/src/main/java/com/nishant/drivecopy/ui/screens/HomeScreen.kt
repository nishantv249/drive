package com.nishant.drivecopy.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.nishant.drivecopy.ui.components.AppBar
import com.nishant.drivecopy.ui.components.MenuAppBar

@Composable
fun HomeScreen(){
    var isSelection by remember {
        mutableStateOf(false)
    }
    var uploadImages = remember {
        mutableStateOf(false)
    }
    val onUploadClicked : () -> Unit
    Column {
        if (isSelection) {
            MenuAppBar{
                uploadImages.value = true
            }
        } else{
            AppBar()
        }
        DriveScreen(uploadImages){
            isSelected ->
            isSelection = isSelected
        }
    }
}