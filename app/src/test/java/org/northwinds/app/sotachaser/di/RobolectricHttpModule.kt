package org.northwinds.app.sotachaser.di

import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import okhttp3.OkHttpClient
import okhttp3.mock.*
import org.northwinds.app.sotachaser.R
import javax.inject.Singleton

@Module
@TestInstallIn(components = [SingletonComponent::class], replaces = [HttpModule::class])
object RobolectricHttpModule {
    @Singleton
    @Provides
    fun provideInterceptor(): MockInterceptor {
        val interceptor = MockInterceptor().apply {
            rule(url eq "http://www.sotadata.org.uk/summitslist.csv") {
                val input = RoboResources.rawRes(R.raw.summitslist)
                respond(input)
            }
            rule(get) {
                respond { throw IllegalStateException("I/O Error") }
            }
        }
        return interceptor
    }

    @Singleton
    @Provides
    fun provideHttpClient(interceptor: MockInterceptor): OkHttpClient {
        return OkHttpClient.Builder().addInterceptor(interceptor).build()
    }
}
