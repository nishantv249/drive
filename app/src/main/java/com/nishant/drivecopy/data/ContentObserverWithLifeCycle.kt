package com.nishant.drivecopy.data

import android.content.Context
import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.os.Looper
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

abstract class ContentObserverWithLifeCycle @OptIn(ExperimentalCoroutinesApi::class)
    constructor(@ApplicationContext private val context: Context,private val dispatcher: CoroutineDispatcher
    = Dispatchers.IO.limitedParallelism(1))  {

    private lateinit var scope: CoroutineScope

    private val contentObserver = object : ContentObserver(Handler(Looper.getMainLooper())) {
        override fun onChange(selfChange: Boolean) {
            loadContentInBackGround()
        }
    }

    fun initialize(){
        scope = CoroutineScope(dispatcher)
        loadContentInBackGround()
        context.contentResolver.registerContentObserver(getUri(), true, contentObserver)
    }

    fun clear(){
        context.contentResolver.unregisterContentObserver(contentObserver)
        scope.cancel("context out of this lifecycle")
    }

    private fun loadContentInBackGround(){
        scope.launch {
            loadContent(context)
        }
    }

    abstract fun getUri() : Uri

    abstract suspend fun loadContent(context : Context)

}