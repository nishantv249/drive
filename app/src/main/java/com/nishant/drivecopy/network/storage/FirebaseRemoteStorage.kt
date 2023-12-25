package com.nishant.drivecopy.network.storage

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

class FirebaseRemoteStorage @Inject constructor() : IRemoteStorage {

    private val storageRef by lazy {
        FirebaseStorage.getInstance().getReferenceFromUrl(PATH_TO_STORAGE)
    }

    // flow from callback needs to be replaced by callBackFlow
    override suspend fun uploadImage(images: Uri): Flow<UploadingState> {
        val fileName = UUID.randomUUID().toString()
        val storagePath = "images/$fileName"
        val ref = storageRef.child(storagePath)
        val uploadProgress = flow{
            ref.putFile(images).addOnProgressListener {
                val progress = (100.0 * it.bytesTransferred) / it.totalByteCount
                suspend {
                    emit(progress.toInt())
                }
            }
        }
        val downloadUrlFlow = flow{
            emit(null)
            ref.downloadUrl.addOnCompleteListener {
                if (it.isSuccessful) {
                    suspend {
                      emit(it.result)
                    }
                }
            }
        }
        return uploadProgress.combine(downloadUrlFlow){  progress, uri ->
                 UploadingState(progress,uri,uri!=null)
        }
    }

    override suspend fun deleteImage(link: String) {
        FirebaseStorage.getInstance().getReferenceFromUrl(link).delete().await()
    }

    companion object {
        const val PATH_TO_STORAGE = "gs://drivecopy-b9297.appspot.com"
    }
}