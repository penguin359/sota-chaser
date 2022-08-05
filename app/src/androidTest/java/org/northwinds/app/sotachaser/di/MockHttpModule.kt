package org.northwinds.app.sotachaser.di

import android.app.Application
import android.content.Context
import android.util.Log
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import okhttp3.Dispatcher
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.mock.*
import org.northwinds.app.sotachaser.R
import java.util.concurrent.ExecutorService
import javax.inject.Singleton

@Module
@TestInstallIn(components = [SingletonComponent::class], replaces = [HttpModule::class])
object MockHttpModule {
    @Singleton
    @Provides
    fun provideInterceptor(context: Application): MockInterceptor {
        val interceptor = MockInterceptor().apply {
            rule(url eq "https://www.sotadata.org.uk/summitslist.csv", times = anyTimes) {
                val input = AndroidResources.rawRes(context, R.raw.summitslist)
                respond(input)
            }
            rule(url eq "https://api2.sota.org.uk/api/associations", times = anyTimes) {
                val input = ClasspathResources.resource("associations.json")
                respond(input)
            }
            rule(url eq "https://api2.sota.org.uk/api/associations/W7O", times = anyTimes) {
                val input = ClasspathResources.resource("W7O.json")
                respond(input)
            }
            rule(url eq "https://api2.sota.org.uk/api/associations/W7W", times = anyTimes) {
                val input = ClasspathResources.resource("W7W.json")
                respond(input)
            }
            rule(url eq "https://api2.sota.org.uk/api/associations/BV", times = anyTimes) {
                val input = ClasspathResources.resource("BV.json")
                respond(input)
            }
            rule(url eq "https://api2.sota.org.uk/api/associations/3Y", times = anyTimes) {
                val input = ClasspathResources.resource("3Y.json")
                respond(input)
            }
            rule(url eq "https://api2.sota.org.uk/api/associations/HL", times = anyTimes) {
                val input = ClasspathResources.resource("HL.json")
                respond(input)
            }
            rule(url eq "https://api2.sota.org.uk/api/regions/3Y/BV", times = anyTimes) {
                val input = ClasspathResources.resource("3Y_BV.json")
                respond(input)
            }
            rule(url eq "https://api2.sota.org.uk/api/regions/HL/GN", times = anyTimes) {
                val input = ClasspathResources.resource("HL_GN.json")
                respond(input)
            }
            rule(url eq "https://api2.sota.org.uk/api/regions/W7O/CN", times = anyTimes) {
                val input = ClasspathResources.resource("W7O_CN.json")
                respond(input)
            }
            rule(url eq "https://api2.sota.org.uk/api/regions/W7W/LC", times = anyTimes) {
                val input = ClasspathResources.resource("W7W_LC.json")
                respond(input)
            }
            rule(url eq "https://api2.sota.org.uk/api/summits/HL/GN-001", times = anyTimes) {
                val input = ClasspathResources.resource("HL_GN-001.json")
                respond(input)
            }
            rule(url eq "https://api2.sota.org.uk/api/summits/W7W/LC-052", times = anyTimes) {
                val input = ClasspathResources.resource("W7W_LC-052.json")
                respond(input)
            }
            rule(url eq "https://api-db.sota.org.uk/smp/gpx/summit/W7W/LC-050", times = anyTimes) {
                val input = ClasspathResources.resource("gpx/W7W_LC-050.json")
                respond(input)
            }
            rule(path matches "/api/associations/(\\w+)".toRegex(), times = anyTimes) {
                respond {
                    val association = it.url.pathSegments[2]
                    body("""
                    |{
                    |    "associationCode": "$association",
                    |    "regionsCount": 0,
                    |    "summitsCount": 0
                    |}""".trimMargin(), MediaTypes.MEDIATYPE_JSON) }
            }
            rule(path matches "/api/regions/(\\w+)/(\\w+)".toRegex(), times = anyTimes) {
                respond {
                    val association = it.url.pathSegments[2]
                    val region = it.url.pathSegments[3]
                    body("""
                    |{
                    |    "region": {
                    |        "associationCode": "$association",
                    |        "regionCode": "$region",
                    |        "summits": 0
                    |    }
                    |}""".trimMargin(), MediaTypes.MEDIATYPE_JSON) }
            }
            rule(path matches "/api/summits/(\\w+)/(.*)".toRegex(), times = anyTimes) {
                respond {
                    val association = it.url.pathSegments[2]
                    val region = it.url.pathSegments[3].split("-")[0]
                    val summit = it.url.pathSegments[3].split("-")[1]
                    body("""
                    |{
                    |    "summitCode": "$association/$region-$summit",
                    |    "shortCode": "$region-$summit",
                    |    "altM": 0,
                    |    "altFt": 0,
                    |    "validFrom": "",
                    |    "validTo": "",
                    |    "longitude": 0,
                    |    "latitude": 0,
                    |    "points": 0,
                    |    "valid": false,
                    |    "restrictionMask": false
                    |}""".trimMargin(), MediaTypes.MEDIATYPE_JSON) }
            }
            rule(path matches "/smp/gpx/summit/(\\w+)/(.*)".toRegex(), times = anyTimes) {
                respond {
                    //val association = it.url.pathSegments[2]
                    body("""
                    |[
                    |]""".trimMargin(), MediaTypes.MEDIATYPE_JSON) }
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
    fun provideHttpClient(interceptor: MockInterceptor, executor: ExecutorService): OkHttpClient {
        val logging = HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {
            override fun log(message: String) {
                Log.d("SOTAChaser-OkHttp", message)
            }
        })
        logging.level = HttpLoggingInterceptor.Level.BASIC
        return OkHttpClient.Builder()
            .dispatcher(Dispatcher(executor))
            .addInterceptor(logging)
            .addInterceptor(interceptor)
            .build()
    }
}
