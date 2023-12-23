package com.nishant.drivecopy.network.storage

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

class FirebaseRemoteStorage @Inject constructor() : IRemoteStorage {

    private val storageRef by lazy {
        FirebaseStorage.getInstance().getReferenceFromUrl(PATH_TO_STORAGE)
    }

    override suspend fun uploadImage(images: Uri): String {
        val fileName = UUID.randomUUID().toString()
        val storagePath = "images/$fileName"
        val ref = storageRef.child(storagePath)
        ref.putFile(images).await()
        return ref.downloadUrl.await().toString()
    }

    override suspend fun deleteImage(link: String) {
        FirebaseStorage.getInstance().getReferenceFromUrl(link).delete().await()
    }

    companion object {
        const val PATH_TO_STORAGE = "gs://drivecopy-b9297.appspot.com"
    }
}