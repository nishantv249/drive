package com.nishant.drivecopy.sync

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import androidx.work.testing.TestListenableWorkerBuilder
import com.nishant.drivecopy.db.DriveDatabase
import com.nishant.drivecopy.db.entity.Images
import com.nishant.drivecopy.db.entity.UploadStatus
import com.nishant.drivecopy.network.remote.IRemoteData
import com.nishant.drivecopy.network.remote.fake.FakeRemoteData
import com.nishant.drivecopy.network.storage.IRemoteStorage
import com.nishant.drivecopy.network.storage.fake.FakeRemoteStorage
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DeleteImagesWorkerTest {

    private lateinit var worker: DeleteImagesWorker
    private val remoteData = FakeRemoteData()
    private val remoteStorage  = FakeRemoteStorage()
    private lateinit var driveDatabase : DriveDatabase

    private val imagesAwaitingUpload = listOf(Images(id=1, uri = "test1"), Images(id=2, uri = "test2"),
                                        Images(id =3, uri = "test3"))

    private val expectedInRemote = listOf(Images(id=1, uri = "test1", uploadStatus = 3, link = FakeRemoteStorage.BASE_PATH.plus("test1")),
        Images(id=2, uri = "test2", uploadStatus = 3, link = FakeRemoteStorage.BASE_PATH.plus("test2")),
        Images(id =3, uri = "test3", uploadStatus = 3, link = FakeRemoteStorage.BASE_PATH.plus("test3")))

    private val extraItemInStorage = FakeRemoteStorage.BASE_PATH.plus("test4")
    private val extraItemInRemote = Images(id = 1, uploadStatus = UploadStatus.UPLOADED.value, link = extraItemInStorage)

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        driveDatabase = Room.inMemoryDatabaseBuilder(context,DriveDatabase::class.java).build()
        worker = TestListenableWorkerBuilder<DeleteImagesWorker>(context)
            .setWorkerFactory(TestWorkerFactory(driveDatabase,remoteData,remoteStorage)).build()
    }

    @Test
    fun testSyncImagesWorker() = runTest {
        driveDatabase.imagesDao().upsert(imagesAwaitingUpload)
        worker.doWork()
        val imagesInStorage = remoteStorage.imageStorage.toList()
        val imagesInRemote = remoteData.getAllImages().values.toList()
        assertEquals(listOf(FakeRemoteStorage.BASE_PATH.plus("test1"),FakeRemoteStorage.BASE_PATH.plus("test2"),FakeRemoteStorage.BASE_PATH.plus("test3")),
                    imagesInStorage)
        assertEquals(expectedInRemote,imagesInRemote)
    }

    @Test
    fun testSyncImagesWorker_extraItemInStorage() = runTest {
        remoteStorage.imageStorage.add(extraItemInStorage)
        remoteData.putImage(extraItemInRemote)
        worker.doWork()
        val imagesInStorage = remoteStorage.imageStorage.toList()
        val imagesInRemote = remoteData.getAllImages().values.toList()
        assertEquals(emptyList<Images>(),imagesInStorage)
        assertEquals(emptyList<Images>(),imagesInRemote)
    }

    private class TestWorkerFactory(private val driveDatabase: DriveDatabase,
        private val remoteData: IRemoteData, private val remoteStorage: IRemoteStorage) : WorkerFactory(){
        override fun createWorker(
            appContext: Context,
            workerClassName: String,
            workerParameters: WorkerParameters
        ): ListenableWorker {
            return DeleteImagesWorker(appContext,workerParameters,driveDatabase,remoteData,remoteStorage)
        }
    }

}