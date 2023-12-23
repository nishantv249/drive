package com.nishant.drivecopy.sync.utils

import com.nishant.drivecopy.db.entity.Images

interface DeleteImages {
    suspend fun deleteImages(images : List<Images>)
}