package org.northwinds.app.sotachaser.di

import android.app.Application
import androidx.room.Room
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.northwinds.app.sotachaser.repository.SummitsRepository
import org.northwinds.app.sotachaser.repository.SummitsRepositoryImpl
import org.northwinds.app.sotachaser.room.SummitDao
import org.northwinds.app.sotachaser.room.SummitDatabase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {
    @Binds abstract fun provideSummitsRepository(repo: SummitsRepositoryImpl): SummitsRepository

    companion object {
        @Provides
        fun provideSummitsDao(database: SummitDatabase): SummitDao {
            return database.summitDao()
        }

        @Singleton
        @Provides
        fun provideDatabase(context: Application): SummitDatabase {
            return Room.databaseBuilder(context, SummitDatabase::class.java, "database").build()
        }
    }
}
