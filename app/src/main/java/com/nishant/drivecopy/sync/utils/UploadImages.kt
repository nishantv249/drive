package com.nishant.drivecopy.sync.utils

import androidx.annotation.IntRange
import com.nishant.drivecopy.db.entity.Images
import kotlinx.coroutines.flow.Flow

interface UploadImages  {
    suspend fun upload(imagesList : List<Images>)

    fun upload(images: Images) : Flow<UploadImageState>
}

@JvmInline
value class UploadImageState(@IntRange(0,100) val progress : Int)