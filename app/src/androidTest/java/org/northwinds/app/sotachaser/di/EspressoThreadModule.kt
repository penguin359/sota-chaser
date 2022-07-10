package org.northwinds.app.sotachaser.di

import android.util.Log
import androidx.test.espresso.idling.concurrent.IdlingThreadPoolExecutor
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [ThreadModule::class]
)
class EspressoThreadModule {
    @Provides
    @Singleton
    fun provideThreadExecutor(): ExecutorService {
        Log.d(TAG, "Creating idling thread pool")
        val id = counter.getAndIncrement()
        return IdlingThreadPoolExecutor("EspressoTestPool-$id", 5, 64, 50, TimeUnit.MILLISECONDS, LinkedBlockingDeque<Runnable>(), Executors.defaultThreadFactory())
    }

    companion object {
        private const val TAG = "SOTAChaser-EspressoThreadModule"
        val counter = AtomicInteger(1)
    }
}
