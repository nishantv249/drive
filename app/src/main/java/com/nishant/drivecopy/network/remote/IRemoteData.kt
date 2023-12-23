package com.nishant.drivecopy.network.remote

import com.nishant.drivecopy.db.entity.Images

interface IRemoteData {

    suspend fun getAllImages() : HashMap<String,Images>

    suspend fun deleteImage(id : Long)

    suspend fun putImage(image : Images)

}