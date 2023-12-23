package com.nishant.drivecopy.data

import com.nishant.drivecopy.db.entity.Images

interface IDeviceImagesRepository {

   suspend fun getAllImages(): List<Images>
}