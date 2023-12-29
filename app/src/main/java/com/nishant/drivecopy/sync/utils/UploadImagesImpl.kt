package com.nishant.drivecopy.sync.utils

import android.net.Uri
import com.nishant.drivecopy.db.DriveDatabase
import com.nishant.drivecopy.db.entity.Images
import com.nishant.drivecopy.db.entity.UploadStatus
import com.nishant.drivecopy.network.remote.IRemoteData
import com.nishant.drivecopy.network.storage.IRemoteStorage
import dagger.hilt.android.internal.ThreadUtil
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.concurrent.CancellationException
import javax.inject.Inject

class UploadImagesImpl @Inject constructor(private val driveDatabase: DriveDatabase,
                                           private val remoteData : IRemoteData,
                                           private  val remoteStorage : IRemoteStorage) : UploadImages {

    @Throws(RuntimeException::class)
    override suspend fun upload(imagesList: List<Images>) {
        if(ThreadUtil.isMainThread()){
            throw RuntimeException(ERROR_ON_MAIN_THREAD)
        }
        imagesList.forEach { images ->
            upload(images)
        }
    }


    override fun upload(images: Images): Flow<UploadImageState> {
        return flow {
            try {
                images.uploadStatus = UploadStatus.UPLOADING.value
                driveDatabase.imagesDao().upsert(listOf(images))
                val uploadToStorageFlow = remoteStorage.uploadImage(Uri.parse(images.uri))
                uploadToStorageFlow.collect{ uploadingState ->
                    if(uploadingState.isUploaded){
                        emit(UploadImageState(95))
                        images.uploadStatus = UploadStatus.UPLOADED.value
                        images.uri = uploadingState.uri.toString()
                        remoteData.putImage(images)
                        emit(UploadImageState(97))
                        driveDatabase.imagesDao().upsert(listOf(images))
                        emit(UploadImageState(100))
                    }else{
                        emit(UploadImageState((uploadingState.progress*0.95).toInt()))
                    }
                }

            } catch (e: Exception) {
                if (e is CancellationException) {
                    throw e
                }
                try {
                    images.uploadStatus = UploadStatus.PENDING.value
                    driveDatabase.imagesDao().upsert(listOf(images))
                } catch (_: Exception) {
                    // this scenario  has to be handled
                }
            }
        }
    }

   private companion object{
        const val ERROR_ON_MAIN_THREAD = "this method should be called on background thread"
    }
}