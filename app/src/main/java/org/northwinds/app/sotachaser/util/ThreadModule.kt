package org.northwinds.app.sotachaser.util

import android.util.Log
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import dagger.hilt.android.scopes.ViewModelScoped
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Module
@InstallIn(ViewModelComponent::class)
class ThreadModule {

    @Provides
    //@ActivityRetainedScoped
    @ViewModelScoped
    fun provideThreadExecutor(): ExecutorService {
        Log.d(Companion.TAG, "Creating single thread executor")
        return Executors.newSingleThreadExecutor()
    }

    companion object {
        private const val TAG = "SOTAChaser-ThreadModule"
    }
}
