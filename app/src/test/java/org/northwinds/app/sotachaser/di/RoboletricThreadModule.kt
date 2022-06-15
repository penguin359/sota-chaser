package org.northwinds.app.sotachaser.di

import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import org.northwinds.app.sotachaser.di.ThreadModule
import org.robolectric.android.util.concurrent.InlineExecutorService
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
