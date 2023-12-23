package com.nishant.drivecopy.sync

import android.content.Context
import android.net.Uri
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.nishant.drivecopy.db.DriveDatabase
import com.nishant.drivecopy.db.entity.Images
import com.nishant.drivecopy.db.entity.UploadStatus
import com.nishant.drivecopy.network.remote.IRemoteData
import com.nishant.drivecopy.network.storage.IRemoteStorage
import com.nishant.drivecopy.sync.utils.DeleteImages
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first

@HiltWorker
class DeleteImagesWorker @AssistedInject constructor(@Assisted context: Context, @Assisted
    workerParameters: WorkerParameters, @Assisted private val driveDatabase: DriveDatabase,
                                                     @Assisted private val remoteData: IRemoteData,
                                                     @Assisted private val deleteImages: DeleteImages) :
    CoroutineWorker(context, workerParameters)  {

    override suspend fun doWork(): Result {
        return try {
            removeDeletedImagesFromStorage()
            Result.success()
        }catch (e : Exception){
            Result.failure()
        }
    }

    private suspend fun removeDeletedImagesFromStorage() {
        val remoteImagesList = remoteData.getAllImages()
        val localImagesList = driveDatabase.imagesDao().getImages().first()
        val itemsToBeDeleted = mutableListOf<Images>()
        remoteImagesList.forEach { remoteImage ->
            if(localImagesList.find { it.id == remoteImage.value.id } == null){
                itemsToBeDeleted.add(remoteImage.value)
            }
        }
        deleteImages.deleteImages(itemsToBeDeleted)
    }

}