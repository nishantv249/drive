package com.nishant.drivecopy.sync

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.nishant.drivecopy.db.DriveDatabase
import com.nishant.drivecopy.db.entity.Images
import com.nishant.drivecopy.sync.utils.UploadImages
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class UploadRequestedImages @AssistedInject constructor(@Assisted context: Context,
    @Assisted private val workerParameters: WorkerParameters,@Assisted private val driveDatabase: DriveDatabase,
    private val uploadImages: UploadImages) : CoroutineWorker(context,workerParameters) {

    override suspend fun doWork(): Result {
        return try {
            var imagesToUpload : List<Images>?  = null
            if(workerParameters.inputData.getIntArray(
                COLLECTED_IMAGES_IDS) != null){
                val ids = workerParameters.inputData.getIntArray(COLLECTED_IMAGES_IDS)!!.toList()
                imagesToUpload = driveDatabase.imagesDao().getImagesByIds(ids)
            }
            if(imagesToUpload == null){
                Result.success()
            }
            uploadImages.upload(imagesToUpload!!)
            Result.success()
        }catch (e : Exception){
            Result.failure()
        }
    }

    companion object{
        const val COLLECTED_IMAGES_IDS = "collected_image_ids"
    }

}