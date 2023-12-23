package com.nishant.drivecopy.network.storage

import android.net.Uri
import com.nishant.drivecopy.db.entity.Images

interface IRemoteStorage {

    suspend fun uploadImage(images: Uri) : String

    suspend fun deleteImage(link : String)

}