package com.nishant.drivecopy.sync.utils

import android.net.Uri
import com.nishant.drivecopy.db.DriveDatabase
import com.nishant.drivecopy.db.entity.Images
import com.nishant.drivecopy.db.entity.UploadStatus
import com.nishant.drivecopy.network.remote.IRemoteData
import com.nishant.drivecopy.network.storage.IRemoteStorage
import dagger.hilt.android.internal.ThreadUtil
import java.util.concurrent.CancellationException
import javax.inject.Inject

class UploadImagesImpl @Inject constructor(private val driveDatabase: DriveDatabase,
                                           private val remoteData : IRemoteData, private  val remoteStorage : IRemoteStorage) : UploadImages {

    @Throws(RuntimeException::class)
    override suspend fun upload(imagesList: List<Images>) {
        if(ThreadUtil.isMainThread()){
            throw RuntimeException(ERROR_ON_MAIN_THREAD)
        }
        imagesList.forEach { images ->
            upload(images)
        }
    }

    private suspend fun upload(image: Images){
        try {
            image.uploadStatus = UploadStatus.UPLOADING.value
            driveDatabase.imagesDao().upsert(listOf(image))
            image.link = remoteStorage.uploadImage(Uri.parse(image.uri))
            image.uploadStatus = UploadStatus.UPLOADED.value
            remoteData.putImage(image)
            driveDatabase.imagesDao().upsert(listOf(image))
        }catch (e : Exception){
            if( e is CancellationException){
                throw e
            }
            try {
                image.uploadStatus = UploadStatus.PENDING.value
                driveDatabase.imagesDao().upsert(listOf(image))
            }catch (_ : Exception){
                // this scenario  has to be handled
            }
        }
    }

   private companion object{
        const val ERROR_ON_MAIN_THREAD = "this method should be called on background thread"
    }
}