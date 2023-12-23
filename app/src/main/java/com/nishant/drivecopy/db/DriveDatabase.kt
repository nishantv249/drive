package com.nishant.drivecopy.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.nishant.drivecopy.db.dao.ImagesDao
import com.nishant.drivecopy.db.entity.Images

@Database(entities = [Images::class], version = 1)
abstract class DriveDatabase : RoomDatabase() {
    abstract fun imagesDao(): ImagesDao
}