package com.nishant.drivecopy.sync

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import androidx.work.testing.TestListenableWorkerBuilder
import com.nishant.drivecopy.data.DeviceImagesRepository
import com.nishant.drivecopy.data.IDeviceImagesRepository
import com.nishant.drivecopy.db.DriveDatabase
import com.nishant.drivecopy.db.entity.Images
import com.nishant.drivecopy.network.remote.IRemoteData
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*

import org.junit.Before
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

@RunWith(AndroidJUnit4::class)
class FetchImagesWorkerTest {

    private lateinit var worker: FetchImagesWorker
    private lateinit var database: DriveDatabase

    @Mock
    private lateinit var remoteData : IRemoteData
    @Mock
    private lateinit var deviceImagesRepository: IDeviceImagesRepository

    private val localImagesFromDevice = listOf(Images(id=2), Images(id=3), Images(id=4))

    private val remoteImagesList = HashMap<String,Images>().apply {
        put("3",Images(id=3, uploadStatus = 3))
        put("2",Images(id=2, uploadStatus = 3))
    }

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, DriveDatabase::class.java).build()
        val workerFactory = FetchImagesWorkerFactory(database,remoteData,deviceImagesRepository)
        worker = TestListenableWorkerBuilder<FetchImagesWorker>(context).setWorkerFactory(workerFactory).build()
    }

    @org.junit.Test
    fun testDoWork()  = runTest {
        Mockito.`when`(deviceImagesRepository.getAllImages()).thenReturn(localImagesFromDevice)
        Mockito.`when`(remoteData.getAllImages()).thenReturn(remoteImagesList)
        worker.doWork()
        val images = database.imagesDao().getImages().first()
        assertEquals(images,listOf(Images(id=2, uploadStatus = 3), Images(id=3, uploadStatus = 3), Images(id=4)))
    }


    class FetchImagesWorkerFactory(private val database: DriveDatabase
    ,private val remoteData : IRemoteData
    ,private val deviceImagesRepository: IDeviceImagesRepository) : WorkerFactory() {
        override fun createWorker(
            appContext: Context,
            workerClassName: String,
            workerParameters: WorkerParameters
        ): ListenableWorker {
            return FetchImagesWorker(appContext,workerParameters,database,remoteData,deviceImagesRepository)
        }
    }

}