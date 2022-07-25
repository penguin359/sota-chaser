package org.northwinds.app.sotachaser.network

import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.mock.*
import okhttp3.mock.MediaTypes.MEDIATYPE_JSON
import org.junit.Test
import org.northwinds.app.sotachaser.di.AppModule
import kotlin.test.assertEquals

class SmpApiServiceTest {
    @Test
    fun testCanGetGpxTracks() {
        val interceptor = MockInterceptor().apply {
            rule(url eq "https://api-db.sota.org.uk/smp/gpx/summit/W7W/LC-050") {
                val input = ClasspathResources.resource("gpx/W7W_LC-050.json")
                respond(input, MEDIATYPE_JSON)
            }
            rule(get) {
                respond { throw IllegalStateException("I/O Error") }
            }
        }
        val client = OkHttpClient.Builder().addInterceptor(interceptor).build()

        val tracks = runBlocking {
            AppModule.provideSmpApiService(client).getGpxTracks("W7W", "LC", "050")
        }
        assertEquals(1, tracks.count())
        assertEquals(69, tracks[0].points.count())
        println(tracks)
    }
}
