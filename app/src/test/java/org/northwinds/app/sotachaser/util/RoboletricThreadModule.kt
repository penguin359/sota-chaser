package org.northwinds.app.sotachaser.util

import android.app.Application
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import org.robolectric.android.util.concurrent.InlineExecutorService
import org.robolectric.android.util.concurrent.RoboExecutorService
import java.util.concurrent.ExecutorService
import javax.inject.Singleton

@Module
@TestInstallIn(components = [SingletonComponent::class], replaces = [ThreadModule::class])
class RoboletricThreadModule {
    @Provides
    @Singleton
    fun provideThreadExecutor(): ExecutorService {
        return InlineExecutorService()
    }
}
