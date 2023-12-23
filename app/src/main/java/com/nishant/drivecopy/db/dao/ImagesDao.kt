package com.nishant.drivecopy.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.nishant.drivecopy.db.entity.Images
import kotlinx.coroutines.flow.Flow

@Dao
interface ImagesDao {

    @Query("SELECT * FROM Images")
    fun getImages(): Flow<List<Images>>

    @Query("SELECT * FROM Images where uploadStatus = :uploadStatus")
    suspend fun getImagesAwaitingUpload(uploadStatus : Int): List<Images>

    @Query("SELECT * FROM Images where id in (:ids) and uploadStatus = $PENDING")
    suspend fun getImagesByIds(ids : List<Int>): List<Images>

    @Upsert
    suspend fun upsert(images: List<Images>)

    @Query("DELETE FROM Images where id = :id")
    suspend fun delete(id : Long)

    companion object{
        const val PENDING = 1
    }
}