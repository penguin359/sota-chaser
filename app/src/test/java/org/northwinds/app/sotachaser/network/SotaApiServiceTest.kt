package org.northwinds.app.sotachaser.network

import dagger.internal.DaggerGenerated
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.mock.*
import okhttp3.mock.MediaTypes.MEDIATYPE_JSON
import org.junit.Test
import org.northwinds.app.sotachaser.di.AppModule
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.test.assertEquals

class SotaApiServiceTest {
    @Test
    fun testCanGetAssociations() {
        val interceptor = MockInterceptor().apply {
            rule(url eq "https://api2.sota.org.uk/api/associations") {
                val input = ClasspathResources.resource("associations.json")
                respond(input, MEDIATYPE_JSON)
            }
            rule(get) {
                respond { throw IllegalStateException("I/O Error") }
            }
        }
        val client = OkHttpClient.Builder().addInterceptor(interceptor).build()

        val associations = runBlocking {
            AppModule.provideSotaApiService(client).getAssociations()
        }
        assertEquals(10, associations.count())
        println(associations)
    }

    //@Test
    //fun testCanGetAssociations() {
    //    val associations = runBlocking {
    //        AppModule.provideSotaApiService().getAssociations()
    //    }
    //    println(associations)
    //}
}
