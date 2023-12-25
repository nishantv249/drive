package com.nishant.drivecopy.sync

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.nishant.drivecopy.db.DriveDatabase
import com.nishant.drivecopy.db.entity.UploadStatus
import com.nishant.drivecopy.sync.utils.UploadImages
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class UploadImagesWorker @AssistedInject constructor(@Assisted private val driveDatabase: DriveDatabase,
                                                     @Assisted private val uploadImages: UploadImages,
                                                     @Assisted context: Context,
                                                     @Assisted workerParameters: WorkerParameters) :
    CoroutineWorker(context,workerParameters) {

    override suspend fun doWork(): Result {
        return try {
            val imagesToUpload = driveDatabase.imagesDao().getImagesAwaitingUpload(UploadStatus.PENDING.value)
            uploadImages.upload(imagesToUpload)
            Result.success()
        }catch (e : Exception){
            Result.failure()
        }
    }

}