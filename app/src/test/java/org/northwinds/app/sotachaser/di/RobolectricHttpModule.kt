package org.northwinds.app.sotachaser.di

import android.util.Log
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
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
            rule(url eq "https://www.sotadata.org.uk/summitslist.csv") {
                val input = RoboResources.rawRes(R.raw.summitslist)
                respond(input)
            }
            rule(url eq "https://api2.sota.org.uk/api/associations") {
                val input = ClasspathResources.resource("associations.json")
                respond(input)
            }
            rule(url eq "https://api2.sota.org.uk/api/associations/W7O") {
                val input = ClasspathResources.resource("W7O.json")
                respond(input)
            }
            rule(url eq "https://api2.sota.org.uk/api/regions/W7O/CN") {
                val input = ClasspathResources.resource("CN.json")
                respond(input)
            }
            rule(get) {
                respond { throw IllegalStateException("I/O Error") }
            }
        }
        interceptor.behavior(Behavior.UNORDERED)
        return interceptor
    }

    @Singleton
    @Provides
    fun provideHttpClient(interceptor: MockInterceptor): OkHttpClient {
        val logging = HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {
            override fun log(message: String) {
                Log.d("SOTAChaser-OkHttp", message)
            }
        })
        logging.level = HttpLoggingInterceptor.Level.BASIC
        return OkHttpClient.Builder().addInterceptor(logging).addInterceptor(interceptor).build()
    }
}
