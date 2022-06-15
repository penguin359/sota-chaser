package org.northwinds.app.sotachaser.network

import okhttp3.OkHttpClient
import okhttp3.mock.*
import okhttp3.mock.MediaTypes.MEDIATYPE_TEXT
import org.junit.Test
import kotlin.test.assertEquals
import org.northwinds.app.sotachaser.R
import java.nio.file.Files
import java.nio.file.Paths

class SummitDataTest {
    @Test
    fun testCanQueryData() {
        val summitList = getSummitData(OkHttpClient())
    }

    @Test
    fun testCanDecodeTestData() {
        val interceptor = MockInterceptor().apply {
            rule(url eq "http://www.sotadata.org.uk/summitslist.csv") {
                //AndroidResources.rawRes(R.raw.summitslist)
                //val input = ClasspathResources.resource("summitslist.csv")
                val fileName = "src/main/res/raw/summitslist.csv"
                val myPath = Paths.get(fileName)

                val input = Files.newInputStream(myPath)
                respond(input)
                //respond("", MEDIATYPE_TEXT)
            }
            rule(get) {
                respond { throw IllegalStateException("I/O Error") }
            }
        }
        val list = getSummitData(OkHttpClient.Builder().addInterceptor(interceptor).build())
        assertEquals(164345, list.summits.count(), "Incorrect number of summits")
        assertEquals(164345, list.names.count(), "Incorrect number of summit designators")
        assertEquals(1422, list.regions.count(), "Incorrect number of summit regions")
        assertEquals(194, list.associations.count(), "Incorrect number of summit associations")
    }
}
