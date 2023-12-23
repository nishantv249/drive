package com.nishant.drivecopy.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Images(@PrimaryKey val id : Long = 1, val name : String = "", val date : Long = 1,
                  val uri : String = "", var uploadStatus : Int = 1, var link : String = "")


enum class UploadStatus {
    PENDING,
    UPLOADING,
    UPLOADED;

    val value: Int = ordinal + 1
}