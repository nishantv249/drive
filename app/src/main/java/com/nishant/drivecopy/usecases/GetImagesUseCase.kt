package com.nishant.drivecopy.usecases

import com.nishant.drivecopy.data.ImagesRepository
import com.nishant.drivecopy.usecases.models.Images
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetImagesUseCase @Inject constructor(private val imagesRepository: ImagesRepository) {

    operator fun  invoke() : Flow<List<Images>>{
        return imagesRepository.getImages().map{
            it.map { image ->
                Images(image.id, image.name
                    ,getDate(image.date),image.uri,
                    getUploadStatus(image.uploadStatus),image.link)
            }
        }
    }

    private fun getUploadStatus(uploadStatus: Int): String {
        return when(uploadStatus){
            1 -> "Pending"
            2 -> "Uploading"
            3 -> "Uploaded"
            else -> ""
        }
    }

    private fun getDate(date: Long): String {
        return  "22/2/12"
    }
}