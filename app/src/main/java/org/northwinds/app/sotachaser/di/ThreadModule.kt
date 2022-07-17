package org.northwinds.app.sotachaser.di

import android.util.Log
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ThreadModule {

    @Provides
    @Singleton
    fun provideThreadExecutor(): ExecutorService {
        Log.d(TAG, "Creating cached thread executor")
        return Executors.newCachedThreadPool()
    }

    companion object {
        private const val TAG = "SOTAChaser-ThreadModule"
    }
}
