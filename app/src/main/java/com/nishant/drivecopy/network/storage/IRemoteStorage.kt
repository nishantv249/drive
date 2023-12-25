package com.nishant.drivecopy.network.storage

import android.net.Uri
import androidx.annotation.IntRange
import com.nishant.drivecopy.db.entity.Images
import kotlinx.coroutines.flow.Flow

interface IRemoteStorage {

    suspend fun uploadImage(images: Uri) : Flow<UploadingState>

    suspend fun deleteImage(link : String)

}

data class UploadingState(@IntRange(1,100) val progress : Int, val uri: Uri?,val isUploaded : Boolean)