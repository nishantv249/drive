package com.nishant.drivecopy.network.storage.fake

import android.net.Uri
import com.nishant.drivecopy.network.storage.IRemoteStorage
import java.lang.IllegalArgumentException

class FakeRemoteStorage  {
/*
    // expose it for testing
    val imageStorage by lazy {
        HashSet<String>()
    }

    override suspend fun uploadImage(images: Uri): String {
        val path = images.path ?: throw IllegalArgumentException("please provide valid uri")
        val url = appendPath(path)
        imageStorage.add(url)
        return url
    }

    override suspend fun deleteImage(link: String) {
        imageStorage.remove(link)
    }

    private fun appendPath(path : String) : String{
        return BASE_PATH + path
    }

    companion object{
        const val BASE_PATH = "https://dummurl.com/"
    }*/
}