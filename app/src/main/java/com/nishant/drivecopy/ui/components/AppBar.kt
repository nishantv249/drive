package com.nishant.drivecopy.ui.components

import android.content.res.Resources.Theme
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun AppBar(){
   Box (modifier = Modifier
       .fillMaxWidth()
       .height(64.dp)
       .background(Color.Magenta)
       .wrapContentHeight()){
       Text(text = "Home", modifier = Modifier
           .padding(start = 24.dp), color = Color.White,
           style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold)
       )
   }
}

@Composable
fun MenuAppBar( onUploadClicked : () -> Unit){
    Box (modifier = Modifier
        .fillMaxWidth()
        .height(64.dp)
        .background(Color.Magenta)
        .wrapContentHeight().clickable {
            onUploadClicked()
        }, contentAlignment = Alignment.CenterEnd){
        Image(Icons.Default.KeyboardArrowUp, contentDescription = "")
    }
}


@Preview
@Composable
fun AppBarPreview(){
    AppBar()
}