package com.nishant.drivecopy.ui.screens

import android.content.Context
import androidx.lifecycle.ViewModel
import com.nishant.drivecopy.data.ImagesObserver
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

}