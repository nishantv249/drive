package com.nishant.drivecopy.sync.utils

import com.nishant.drivecopy.db.entity.Images

interface UploadImages  {
    suspend fun upload(imagesList : List<Images>)
}