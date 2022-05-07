package org.northwinds.app.sotachaser.util

import android.util.Log
import androidx.test.espresso.idling.concurrent.IdlingThreadPoolExecutor
import dagger.Module
import dagger.Provides
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import dagger.hilt.testing.TestInstallIn
import java.util.concurrent.*

@Module
@TestInstallIn(
    components = [ViewModelComponent::class],
    replaces = [ThreadModule::class]
)
class EspressoThreadModule {
    @Provides
    @ViewModelScoped
    fun provideThreadExecutor(): ExecutorService {
        Log.d(Companion.TAG, "Creating idling thread pool")
        return IdlingThreadPoolExecutor("EspressoTestPool", 1, 1, 50, TimeUnit.MILLISECONDS, LinkedBlockingDeque<Runnable>(), Executors.defaultThreadFactory())
    }

    companion object {
        private const val TAG = "SOTAChaser-EspressoThreadModule"
    }
}
