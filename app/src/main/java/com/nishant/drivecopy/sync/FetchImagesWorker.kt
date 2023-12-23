package com.nishant.drivecopy.sync


import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.nishant.drivecopy.data.IDeviceImagesRepository
import com.nishant.drivecopy.db.DriveDatabase
import com.nishant.drivecopy.db.entity.Images
import com.nishant.drivecopy.network.remote.IRemoteData
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.HashMap

@HiltWorker
class FetchImagesWorker @AssistedInject constructor (
    @Assisted private val context: Context,
    @Assisted private val params: WorkerParameters,
    @Assisted private val database: DriveDatabase,
    @Assisted private val remoteData: IRemoteData,
    @Assisted private val imagesFromDevice: IDeviceImagesRepository
): CoroutineWorker(context,params) {

    override suspend fun doWork(): Result {
        return try {
            val images = imagesFromDevice.getAllImages()
            val imagesFromRemote = remoteData.getAllImages()
            updateUploadStatus(images,imagesFromRemote)
            database.imagesDao().upsert(images)
            Result.success()
        }catch (e : Exception){
            Result.failure()
        }
    }

    private fun updateUploadStatus(images: List<Images>, imagesFromRemote: HashMap<String, Images>){
        images.forEach {    localImage  ->
            if(imagesFromRemote.containsKey(localImage.id.toString())){
                val itemFromRemote = imagesFromRemote[localImage.id.toString()]
                localImage.uploadStatus = itemFromRemote?.uploadStatus!!
                localImage.link = itemFromRemote.link
            }
        }
    }
    
}