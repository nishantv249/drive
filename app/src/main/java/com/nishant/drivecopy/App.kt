package com.nishant.drivecopy

import android.app.Application
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.work.Configuration
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.ListenableWorker
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.nishant.drivecopy.data.IDeviceImagesRepository
import com.nishant.drivecopy.db.DriveDatabase
import com.nishant.drivecopy.network.remote.IRemoteData
import com.nishant.drivecopy.network.storage.IRemoteStorage
import com.nishant.drivecopy.sync.DeleteImagesWorker
import com.nishant.drivecopy.sync.UploadImagesWorker
import com.nishant.drivecopy.sync.UploadRequestedImagesWorker
import com.nishant.drivecopy.sync.utils.DeleteImages
import com.nishant.drivecopy.sync.utils.UploadImages
import dagger.hilt.android.HiltAndroidApp
import java.time.Duration
import javax.inject.Inject

@HiltAndroidApp
class App : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: DriveWorkerFactory
    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()
        val uploadWorker = PeriodicWorkRequestBuilder<UploadImagesWorker>(Duration.ofHours(6)).build()
        val deleteImagesWorker = PeriodicWorkRequestBuilder<DeleteImagesWorker>(Duration.ofHours(6)).build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork("uploadDel",
            ExistingPeriodicWorkPolicy.KEEP,uploadWorker)
        WorkManager.getInstance(this).enqueueUniquePeriodicWork("uploadDel",
            ExistingPeriodicWorkPolicy.KEEP,deleteImagesWorker)
    }

}

class DriveWorkerFactory @Inject constructor (private val driveDatabase: DriveDatabase
        ,private val remoteData: IRemoteData,private val uploadImages: UploadImages,
        private val deleteImages: DeleteImages) : WorkerFactory(){
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker {
        return when(workerClassName){
            UploadRequestedImagesWorker::class.java.name -> {
                UploadRequestedImagesWorker(appContext, workerParameters, driveDatabase, uploadImages)
            }

            UploadImagesWorker::class.java.name -> {
                UploadImagesWorker(driveDatabase, uploadImages,appContext,workerParameters)
            }

            DeleteImagesWorker::class.java.name -> {
                DeleteImagesWorker(appContext, workerParameters,driveDatabase,remoteData,deleteImages)
            }

            else -> {
                throw IllegalArgumentException("Unknown worker class name")
            }
        }
    }
}