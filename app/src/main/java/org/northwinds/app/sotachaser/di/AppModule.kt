package org.northwinds.app.sotachaser.di

import android.app.Application
import androidx.room.Room
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import org.northwinds.app.sotachaser.network.SotaApiService
import org.northwinds.app.sotachaser.repository.SummitsRepository
import org.northwinds.app.sotachaser.repository.SummitsRepositoryImpl
import org.northwinds.app.sotachaser.room.SummitDao
import org.northwinds.app.sotachaser.room.SummitDatabase
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.ExecutorService
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
        fun provideDatabase(context: Application, executor: ExecutorService): SummitDatabase {
            return Room.databaseBuilder(context, SummitDatabase::class.java, "database")
                .fallbackToDestructiveMigration()
                //.setQueryExecutor(executor)
                .build()
        }

        @Singleton
        @Provides
        fun provideSotaApiService(client: OkHttpClient): SotaApiService {
            val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
            val retrofit = Retrofit.Builder()
                .baseUrl("https://api2.sota.org.uk/")
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .client(client)
                .build()

            return retrofit.create(SotaApiService::class.java)
        }
    }
}
