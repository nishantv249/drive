package com.nishant.drivecopy.network.remote

import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.nishant.drivecopy.db.entity.Images
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class RemoteData @Inject constructor()  : IRemoteData {

    private val firebaseDatabase by lazy {
        Firebase.database.getReference(PATH_TO_IMAGES)
    }

    override suspend fun getAllImages(): HashMap<String, Images> {
        val task = firebaseDatabase.get().await()
        val type = object : GenericTypeIndicator<HashMap<String,Images>>(){}
        var result = task.getValue(type)
        if(result == null){
            result = HashMap()
        }
        return result
    }

    override suspend fun deleteImage(id: Long) {
        firebaseDatabase.child("$id").removeValue().await()
    }

    // TO DO return flow of progress updates
    override suspend fun putImage(image: Images) {
        firebaseDatabase.child("${image.id}").setValue(image).await()
    }

    companion object{
        const val PATH_TO_IMAGES = "images"
    }
}