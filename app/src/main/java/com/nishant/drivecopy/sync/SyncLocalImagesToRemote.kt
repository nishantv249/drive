package com.nishant.drivecopy.sync

import com.nishant.drivecopy.data.ImagesObserver
import com.nishant.drivecopy.db.DriveDatabase
import com.nishant.drivecopy.db.entity.Images
import com.nishant.drivecopy.network.remote.IRemoteData
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.util.HashMap
import javax.inject.Inject

class SyncLocalImagesToRemote
@Inject constructor(private val imagesObserver: ImagesObserver,
                    private val driveDatabase: DriveDatabase,
                    private val remoteData : IRemoteData) {

    @OptIn(ExperimentalCoroutinesApi::class)
    private val scope = CoroutineScope(Dispatchers.IO.limitedParallelism(1))

    fun initialize(){
        imagesObserver.initialize()
        scope.launch {
            driveDatabase.imagesDao().getImages().collect { imagesList ->
                val imagesFromRemote = remoteData.getAllImages()
                val updatedImageList = updateUploadStatus(imagesList, imagesFromRemote)
                if(updatedImageList.isNotEmpty()) {
                    driveDatabase.imagesDao().upsert(updatedImageList)
                }
            }
        }
    }

    fun clear(){
        imagesObserver.clear()
        scope.cancel()
    }

    private fun updateUploadStatus(images: List<Images>, imagesFromRemote: HashMap<String, Images>)
        : List<Images>{
        val updatedList = mutableListOf<Images>()
        images.forEach {    localImage  ->
            if(imagesFromRemote.containsKey(localImage.id.toString())){
                val itemFromRemote = imagesFromRemote[localImage.id.toString()]
                localImage.uploadStatus = itemFromRemote?.uploadStatus!!
                localImage.link = itemFromRemote.link
                updatedList.add(itemFromRemote)
            }
        }
    return updatedList
    }

}