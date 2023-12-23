package com.nishant.drivecopy.sync.utils

import com.nishant.drivecopy.db.entity.Images
import com.nishant.drivecopy.network.remote.IRemoteData
import com.nishant.drivecopy.network.storage.IRemoteStorage
import java.util.concurrent.CancellationException
import javax.inject.Inject

class DeleteImagesImpl @Inject constructor(private val remoteData : IRemoteData,
    private val remoteStorage : IRemoteStorage) : DeleteImages {

    override suspend fun deleteImages(images: List<Images>) {
        images.forEach {image ->
            deleteImage(image)
        }
    }

    private suspend fun deleteImage(image: Images) {
        try {
            remoteStorage.deleteImage(image.link)
            remoteData.deleteImage(image.id)
        }catch (e : Exception){
            if( e is CancellationException){
                throw e
            }
            // If delete from storage fails, it will be re-attempted in next cycle
            // If delete from remote fails, means a re-attempt to deleteFromStorage
            // will throw an exception, means we can consider removing from remote
            // again in a certain type of exception
        }
    }
}