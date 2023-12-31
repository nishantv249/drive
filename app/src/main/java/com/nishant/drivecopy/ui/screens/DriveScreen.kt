package com.nishant.drivecopy.ui.screens


import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.nishant.drivecopy.usecases.models.Images

@Composable
fun DriveScreen(uploadImages : Boolean, getImagesViewModel: ImagesViewModel = hiltViewModel(),changeSelectionState : (isSelection : Boolean) -> Unit){

    if(isReadPermissionGranted() && isNotificationPermissionGranted()){
        val context = LocalContext.current
        LaunchedEffect(Unit) {
            getImagesViewModel.fetchImages(context)
        }
    }

    val selectionList : MutableList<Long> = remember {
        mutableStateListOf()
    }

    if(uploadImages && selectionList.size > 0){
        getImagesViewModel.uploadImage(selectionList, LocalContext.current)
        selectionList.clear()
    }

    changeSelectionState(selectionList.size > 0)

    val imagesList by getImagesViewModel.getImages().collectAsState(initial = emptyList())
    LazyVerticalGrid(columns = GridCells.Fixed(2), verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.padding(16.dp)) {
        items(imagesList, key = {
            it.id
        }){ images ->
            ImagesItem(images,getSelectionStatus(selectionList,images)) { id ->
                if (selectionList.contains(id)) {
                    selectionList.remove(id)
                } else {
                    selectionList.add(id)
                }
            }
        }
    }
}

/*
    selection states for images

    1. normal -- no selection at all .. represented as empty list
    2. selecting -- non-empty list but the image item is not in the list
    3. selected -- non-empty list and the image item is in the list

 */

fun getSelectionStatus(selectionList: MutableList<Long>, images: Images): Int {
    if(selectionList.isEmpty()){
        return 1
    }
    if(selectionList.contains(images.id)) {
        return 3
    }
    return 2
}

@Composable
fun ImagesItem(images: Images, selectionStatus: Int, onItemClicked : (id : Long) -> Unit) {
    Column(modifier = Modifier.pointerInput(Unit) {
        detectTapGestures(onLongPress = {
            onItemClicked(images.id)
        })
    }) {
        val painter = rememberAsyncImagePainter(model = images.uri)
        Box {
            Image(
                painter = painter, contentDescription = "",
                Modifier
                    .fillMaxWidth()
                    .height(180.dp), contentScale = ContentScale.Crop
            )
            Text(text = images.uploadStatus, modifier = Modifier.padding(16.dp))
            if(selectionStatus == 3){
                Icon(imageVector = Icons.Filled.CheckCircle, contentDescription = "", tint = Color.White,
                    modifier = Modifier.align(Alignment.BottomEnd))
            }else if(selectionStatus == 2){
                Icon(imageVector = Icons.Filled.AddCircle, contentDescription = "", tint = Color.White,
                    modifier = Modifier.align(Alignment.BottomEnd))
            }
        }

        Text(text = images.name, modifier = Modifier.padding(16.dp), maxLines = 1)
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
