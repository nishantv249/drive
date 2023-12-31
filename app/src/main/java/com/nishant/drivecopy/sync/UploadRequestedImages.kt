package com.nishant.drivecopy.sync

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.nishant.drivecopy.R
import com.nishant.drivecopy.db.DriveDatabase
import com.nishant.drivecopy.db.entity.Images
import com.nishant.drivecopy.sync.utils.UploadImages
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import java.util.UUID

@HiltWorker
class UploadRequestedImages @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted private val workerParameters: WorkerParameters,
    @Assisted private val driveDatabase: DriveDatabase,
    private val uploadImages: UploadImages
) : CoroutineWorker(context, workerParameters) {


    private val notificationManager by lazy {
        applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    override suspend fun doWork(): Result {
        return try {
            var imagesToUpload: Images? = null
            val id = workerParameters.inputData.getInt(COLLECTED_IMAGES_IDS, 0)
            if(id == 0){
                Result.success()
            }
            imagesToUpload = driveDatabase.imagesDao().getImagesById(id)
            createNotificationChannel()

            val uploadImagesFlow = uploadImages.upload(imagesToUpload)
            uploadImagesFlow.collect { uploadingImageState ->
                setForeground(createForeGroundInfo(uploadingImageState.progress, imagesToUpload))
            }
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }

    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createForeGroundInfo(progress: Int, images: Images): ForegroundInfo {
        val cancel = applicationContext.getString(R.string.cancel_download)
        val intent = WorkManager.getInstance(applicationContext)
            .createCancelPendingIntent(id)
        val notification = NotificationCompat.Builder(
            applicationContext,
            CHANNEL_ID
        )
            .setContentTitle(images.name)
            .setContentText("Uploading")
            .setProgress(100, progress, false)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setOngoing(true)
            .addAction(R.drawable.ic_launcher_foreground, cancel, intent)
            .build()
        return ForegroundInfo(images.id.toInt(), notification)
    }

    companion object {
        const val COLLECTED_IMAGES_IDS = "collected_image_ids"
        const val CHANNEL_NAME = "Uploading images channel"
        const val CHANNEL_ID = "fgdjf878bsgsn435dfyast3v2dsj"
    }

}