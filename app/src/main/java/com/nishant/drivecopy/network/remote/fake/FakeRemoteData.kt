package com.nishant.drivecopy.network.remote.fake

import com.nishant.drivecopy.db.entity.Images
import com.nishant.drivecopy.network.remote.IRemoteData

class FakeRemoteData : IRemoteData {

    private val hashMap by lazy{
        HashMap<String, Images>()
    }

    override suspend fun getAllImages(): HashMap<String, Images> {
        return hashMap
    }

    override suspend fun deleteImage(id: Long) {
        val stringId = id.toString()
        hashMap.remove(stringId)
    }

    override suspend fun putImage(image: Images) {
        hashMap[image.id.toString()] = image
    }

}