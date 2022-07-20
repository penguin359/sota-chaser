package org.northwinds.app.sotachaser.network

import okhttp3.OkHttpClient
import okhttp3.mock.*
import okhttp3.mock.MediaTypes.MEDIATYPE_TEXT
import org.junit.Test
import kotlin.test.assertEquals
import org.northwinds.app.sotachaser.R
import org.northwinds.app.sotachaser.testing.SummitValues.TOTAL_REGIONS_IN_CSV
import org.northwinds.app.sotachaser.testing.SummitValues.TOTAL_SUMMITS_IN_CSV
import java.nio.file.Files
import java.nio.file.Paths

class SummitDataTest {
    @Test
    fun testCanQueryData() {
        SummitData(OkHttpClient()).getSummitData()
    }

    @Test
    fun testCanDecodeTestData() {
        val interceptor = MockInterceptor().apply {
            rule(url eq "https://www.sotadata.org.uk/summitslist.csv") {
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
        val list = SummitData(OkHttpClient.Builder().addInterceptor(interceptor).build()).getSummitData()
        assertEquals(TOTAL_SUMMITS_IN_CSV, list.summits.count(), "Incorrect number of summits")
        assertEquals(TOTAL_SUMMITS_IN_CSV, list.names.count(), "Incorrect number of summit designators")
        assertEquals(TOTAL_REGIONS_IN_CSV, list.regions.count(), "Incorrect number of summit regions")
        assertEquals(194, list.associations.count(), "Incorrect number of summit associations")
    }
}
