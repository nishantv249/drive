package com.nishant.drivecopy

import android.app.Application
import android.content.Context
import androidx.work.Configuration
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.nishant.drivecopy.data.IDeviceImagesRepository
import com.nishant.drivecopy.db.DriveDatabase
import com.nishant.drivecopy.network.remote.IRemoteData
import com.nishant.drivecopy.network.storage.IRemoteStorage
import com.nishant.drivecopy.sync.DeleteImagesWorker
import com.nishant.drivecopy.sync.FetchImagesWorker
import com.nishant.drivecopy.sync.UploadRequestedImages
import com.nishant.drivecopy.sync.utils.UploadImages
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class App : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: DriveWorkerFactory
    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

}

class DriveWorkerFactory @Inject constructor (private val driveDatabase: DriveDatabase
        ,private val remoteData: IRemoteData, private val remoteStorage : IRemoteStorage,
        private val imagesFromDevice : IDeviceImagesRepository,private val uploadImages: UploadImages) : WorkerFactory(){
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker {
        return when(workerClassName){
            UploadRequestedImages::class.java.name -> {
                UploadRequestedImages(appContext, workerParameters, driveDatabase, uploadImages)
            }

            else -> {
                throw IllegalArgumentException("Unknown worker class name")
            }
        }
    }
}