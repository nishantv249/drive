package com.nishant.drivecopy.data

import com.nishant.drivecopy.db.DriveDatabase
import javax.inject.Inject


class ImagesRepository @Inject constructor(val driveDatabase: DriveDatabase) {

    fun getImages() = driveDatabase.imagesDao().getImages()

}