package com.nishant.drivecopy.di

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import com.nishant.drivecopy.data.DeviceImagesRepository
import com.nishant.drivecopy.data.IDeviceImagesRepository
import com.nishant.drivecopy.data.ImagesObserver
import com.nishant.drivecopy.db.DriveDatabase
import com.nishant.drivecopy.network.remote.IRemoteData
import com.nishant.drivecopy.network.remote.RemoteData
import com.nishant.drivecopy.network.storage.FirebaseRemoteStorage
import com.nishant.drivecopy.network.storage.IRemoteStorage
import com.nishant.drivecopy.sync.utils.UploadImages
import com.nishant.drivecopy.sync.utils.UploadImagesImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun getDatabase(@ApplicationContext context:  Context) : DriveDatabase{
        return Room.databaseBuilder(context, DriveDatabase::class.java,"drive.db").build()
    }

    @Provides
    fun getRemoteData() : IRemoteData{
        return RemoteData()
    }

    @Provides
    fun getRemoteStorage() : IRemoteStorage{
        return FirebaseRemoteStorage()
    }

    @Provides
    fun getImageFromDevice(@ApplicationContext context: Context ) : IDeviceImagesRepository{
        return DeviceImagesRepository(context)
    }

    @Provides
    fun getImagesObserver(@ApplicationContext context: Context, driveDatabase: DriveDatabase,deviceImagesRepository: DeviceImagesRepository) : ImagesObserver{
        return ImagesObserver(context, driveDatabase,deviceImagesRepository)
    }

    @Provides
    fun getUploadImages(driveDatabase: DriveDatabase,remoteStorage: FirebaseRemoteStorage,remoteData: RemoteData) : UploadImages{
        return UploadImagesImpl(driveDatabase,remoteData,remoteStorage)
    }

}