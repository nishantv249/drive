package com.nishant.drivecopy.data

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import com.nishant.drivecopy.db.DriveDatabase
import javax.inject.Inject

class ImagesObserver @Inject constructor(context: Context,private val
        driveDatabase: DriveDatabase,private val imagesRepository : IDeviceImagesRepository) : ContentObserverWithLifeCycle(context) {

    override fun getUri(): Uri {
        return MediaStore.Images.Media.EXTERNAL_CONTENT_URI
    }

    override suspend fun loadContent(context: Context) {
        val newImages = imagesRepository.getAllImages()
        driveDatabase.imagesDao().upsert(newImages)
    }

}