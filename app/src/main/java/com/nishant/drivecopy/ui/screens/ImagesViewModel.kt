package com.nishant.drivecopy.ui.screens

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.nishant.drivecopy.data.ImagesObserver
import com.nishant.drivecopy.sync.UploadRequestedImages
import com.nishant.drivecopy.usecases.GetImagesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ImagesViewModel @Inject constructor(private val getImagesUseCase: GetImagesUseCase,
        private val imagesObserver: ImagesObserver) : ViewModel(){

    fun getImages() = getImagesUseCase()

    fun fetchImages(current: Context) {
        imagesObserver.initialize()
    }

    override fun onCleared() {
        super.onCleared()
        imagesObserver.clear()
    }

    fun uploadImage(id: Long, context: Context) {
        val data = Data.Builder().putIntArray(UploadRequestedImages.COLLECTED_IMAGES_IDS, intArrayOf(id.toInt())).build()
        val worker = OneTimeWorkRequestBuilder<UploadRequestedImages>().setInputData(data).build()
        WorkManager.getInstance(context.applicationContext).beginUniqueWork(id.toString(),ExistingWorkPolicy.KEEP,worker)
            .enqueue()
    }

}