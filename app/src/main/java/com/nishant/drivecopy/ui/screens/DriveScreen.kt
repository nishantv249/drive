package com.nishant.drivecopy.ui.screens


import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.flowWithLifecycle
import coil.compose.rememberAsyncImagePainter

@Composable
fun DriveScreen(getImagesViewModel: ImagesViewModel = hiltViewModel()){

    if(isReadPermissionGranted() && isNotificationPermissionGranted()){
        val context = LocalContext.current
        LaunchedEffect(Unit) {
            getImagesViewModel.fetchImages(context)
        }
    }

    val images by getImagesViewModel.getImages().collectAsState(initial = emptyList())
    LazyVerticalGrid(columns = GridCells.Fixed(2)) {
        items(images, key = {
            it.id
        }){
            val context = LocalContext.current
            Column(modifier = Modifier.clickable {
                getImagesViewModel.uploadImage(it.id, context)
            }) {
                val painter  = rememberAsyncImagePainter(model = it.uri)
                Box {
                    Image(painter = painter, contentDescription = "",
                        Modifier
                            .fillMaxWidth()
                            .height(180.dp), contentScale = ContentScale.Crop)
                        Text(text = it.uploadStatus, modifier = Modifier.padding(16.dp))
                }

                Text(text = it.name, modifier = Modifier.padding(16.dp))
            }
        }
    }

}

@Composable
fun isNotificationPermissionGranted(): Boolean {

    var isPermissionGranted by remember {
        mutableStateOf(false)
    }

    val permissionLauncher = rememberLauncherForActivityResult(contract =
    ActivityResultContracts.RequestPermission()) { permissionGranted ->
        isPermissionGranted = permissionGranted
    }

    LaunchedEffect(key1 = Unit){
        if(!isPermissionGranted){
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
    return  isPermissionGranted
}


@Composable
fun isReadPermissionGranted(): Boolean {

    var isPermissionGranted by remember {
        mutableStateOf(false)
    }

    val permissionLauncher = rememberLauncherForActivityResult(contract =
    ActivityResultContracts.RequestPermission()) { permissionGranted ->
        isPermissionGranted = permissionGranted
    }

    LaunchedEffect(key1 = Unit){
        if(!isPermissionGranted){
            permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
        }
    }

    return  isPermissionGranted
}
