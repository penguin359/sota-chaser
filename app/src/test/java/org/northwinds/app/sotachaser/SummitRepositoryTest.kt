package org.northwinds.app.sotachaser

import android.app.Application
import android.content.Context
import android.os.Looper.getMainLooper
import androidx.core.content.edit
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okhttp3.mock.MockInterceptor
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.hamcrest.core.StringContains
import org.junit.*
import org.junit.Assert.*
import org.northwinds.app.sotachaser.repository.SummitsRepository
import javax.inject.Inject
import org.junit.runner.RunWith
import org.northwinds.app.sotachaser.room.SummitDao
import org.northwinds.app.sotachaser.room.SummitDatabase
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import java.util.concurrent.CountDownLatch

@RunWith(RobolectricTestRunner::class)
@Config(application = HiltTestApplication::class)
@HiltAndroidTest
class SummitRepositoryTest {
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject lateinit var dao: SummitDao
    @Inject lateinit var repo: SummitsRepository
    @Inject lateinit var db: SummitDatabase
    @Inject lateinit var context: Application

    fun forceRefresh() {
        val prefs = context.getSharedPreferences("database", Context.MODE_PRIVATE)
        prefs.edit { putBoolean("database_loaded", false) }
    }

    @Before
    fun setUp() {
        hiltRule.inject()
        forceRefresh()
    }

    @After
    fun tearDown() {
        if(this::db.isInitialized)
            db.close()
    }

    @Test
    fun testCanOpenRepository() {
        assertTrue("Repository is null", this::repo.isInitialized)
    }

    @Test
    fun testCanLoadAssociations() {
        runBlocking {
            repo.refreshAssociations()
        }
        val result2 = /*liveData<List<Association>> {
            withContext(Dispatchers.IO) {*/
                repo.getAssociations()
            /*}
        }*/
        //assertEquals("Looper queued up", true, Looper.myLooper()!!.queue.isIdle)

        val result = result2.blockingObserve()
        //assertEquals("Looper queued up", true, Looper.myLooper()!!.queue.isIdle)
        //val result = .blockingObserve()
        //shadowOf(getMainLooper()).idle()
        assertNotNull("Association result is null", result)
        assertEquals("Incorrect number of associations", 194, result!!.count())
        val associationMatches = result.filter { it.code == "3Y" }
        assertEquals("Failed to find association 3Y", 1, associationMatches.count())
        val association = associationMatches[0]
        assertEquals("3Y", association.code)
        assertEquals("Bouvet Island", association.name)
    }

    @Test
    fun testCanLoadRegions() {
        runBlocking {
            repo.refreshAssociations()
        }
        val result2 = repo.getRegionsInAssociationName("W7W")

        val result = result2.blockingObserve()
        assertNotNull("Region result is null", result)
        assertEquals("Incorrect number of regions", 17, result!!.count())
        val regionMatches = result.filter { it.code == "LC" }
        assertEquals("Failed to one association 3Y", 1, regionMatches.count())
        val region = regionMatches[0]
        assertEquals("LC", region.code)
        assertEquals("WA-Lower Columbia", region.name)
    }

    @Inject lateinit var interceptor: MockInterceptor

    @Test
    fun testWillLoadRefreshFromNetwork() {
        assertFalse(interceptor.rules[0].isConsumed)
        runBlocking {
            repo.refreshAssociations()
        }
        //assertTrue("HTTP request not made", interceptor.rules[0].isConsumed)
    }

    @Test
    fun testWillLoadAllAssociationExtraDetails() {
        runBlocking {
            withContext(Dispatchers.IO) {
                dao.clear()
            }
            repo.refreshAssociations()
        }
        //assertTrue("HTTP request not made", interceptor.rules[0].isConsumed)
        val association = repo.getAssociationByCode("W7O")
        association.observeForever {  }
        shadowOf(getMainLooper()).idle()
        //assertTrue("HTTP request not made", interceptor.rules[1].isConsumed)
        assertNotNull("No value returned", association.value)
        val value = association.value!!
        assertEquals("W7O", value.code)
        assertEquals("USA - Oregon", value.name)
        assertEquals("Etienne", value.manager)
        assertEquals("K7ATN", value.managerCallsign)
        assertEquals("2010-07-01T00:00:00", value.activeFrom)
        assertEquals("291", value.dxcc)
        assertEquals(46.105, value.maxLat)
        assertEquals(-116.6597, value.maxLong)
        assertEquals(41.9951, value.minLat)
        assertEquals(-124.436, value.minLong)
        assertEquals(10, value.regionsCount)
        assertEquals(1990, value.summitsCount)
    }

    @Test
    fun testWillLoadAssociationExtraDetails() {
        runBlocking {
            repo.updateAssociation("W7O")
        }
        //assertTrue("HTTP request not made", interceptor.rules[0].isConsumed)
        val association = repo.getAssociationByCode("W7O")
        association.observeForever {  }
        shadowOf(getMainLooper()).idle()
        //assertTrue("HTTP request not made", interceptor.rules[2].isConsumed)
        assertNotNull("No value returned", association.value)
        val value = association.value!!
        assertEquals("W7O", value.code)
        assertEquals("USA - Oregon", value.name)
        assertEquals("Etienne", value.manager)
        assertEquals("K7ATN", value.managerCallsign)
        assertEquals("2010-07-01T00:00:00", value.activeFrom)
        assertEquals("291", value.dxcc)
        assertEquals(46.105, value.maxLat)
        assertEquals(-116.6597, value.maxLong)
        assertEquals(41.9951, value.minLat)
        assertEquals(-124.436, value.minLong)
        assertEquals(10, value.regionsCount)
        assertEquals(1990, value.summitsCount)
    }

    @Test
    fun testWillLoadRegionExtraDetails() {
        runBlocking {
            repo.updateAssociation("W7O")
            repo.updateRegion("W7O", "CN")
        }
        val association = repo.getAssociationByCode("W7O")
        association.observeForever {  }
        shadowOf(getMainLooper()).idle()
        // TODO Change to getOrAwaitValue()???
        val region = repo.getRegionByCode(association.value!!, "CN")
        region.observeForever {  }
        shadowOf(getMainLooper()).idle()
        //assertTrue("HTTP request not made", interceptor.rules[3].isConsumed)
        assertNotNull("No value returned", region.value)
        val value = region.value!!
        assertEquals(association.value!!.id, value.associationId)
        assertEquals("CN", value.code)
        assertEquals("OR-Cascades North", value.name)
        assertEquals("Dan Smith", value.manager)
        assertEquals("KK7DS", value.managerCallsign)
        assertEquals("The North Oregon Cascades are part of a much larger mountain range that extends from British Columbia south into extreme southern California.  The Oregon Cascades are best known for their recreational opportunities with major, world-class ski resorts on Mt. Hood and Mt. Bachelor in the south to seven major national forests including Crater Lake National Park and the Oregon Caves National Monument.  \n\nMost of the peaks in the Cascades can be dangerous and do experience life-threatening winter weather.", value.notes)
        assertEquals(45.6485, value.maxLat ?: 0.0, 0.00001)
        assertEquals(-121.0406, value.maxLong ?: 0.0, 0.00001)
        assertEquals(44.5081, value.minLat ?: 0.0, 0.00001)
        assertEquals(-121.9929, value.minLong ?: 0.0, 0.00001)
        assertEquals(103, value.summitsCount)
    }

    @Test
    fun testWillLoadAllAssociationCsvDetails() {
        runBlocking {
            withContext(Dispatchers.IO) {
                dao.clear()
            }
            repo.refreshAssociations()
        }
        val association = repo.getAssociationByCode("HL")
        association.observeForever {  }
        shadowOf(getMainLooper()).idle()
        assertNotNull("No value returned", association.value)
        val value = association.value!!
        assertEquals("HL", value.code)
        assertEquals(value.name, "South Korea")
        assertThat(value.manager, `is`(notNullValue()))
    }

    @Test
    fun testWillKeepAssociationNonCsvDetailsOnRegionUpdate() {
        runBlocking {
            repo.updateAssociation("HL")
            repo.updateRegion("HL", "GN", true)
        }
        val association = repo.getAssociationByCode("HL")
        association.observeForever {  }
        shadowOf(getMainLooper()).idle()
        assertNotNull("No value returned", association.value)
        val value = association.value!!
        assertEquals("HL", value.code)
        assertEquals(value.name, "South Korea")
        assertThat(value.manager, `is`(notNullValue()))
    }

    @Test
    fun testWillLoadAllRegionCsvDetails() {
        runBlocking {
            withContext(Dispatchers.IO) {
                dao.clear()
            }
            repo.updateAssociation("HL")
            repo.updateRegion("HL", "GN")
        }
        val association = repo.getAssociationByCode("HL").blockingObserve()
        assertThat(association, `is`(notNullValue()))
        val region = repo.getRegionByCode(association!!, "GN")
        region.observeForever {  }
        shadowOf(getMainLooper()).idle()
        assertNotNull("No value returned", region.value)
        val value = region.value!!
        assertEquals("GN", value.code)
        assertEquals(value.name, "Gyeongnam")
        assertThat(value.manager, `is`(notNullValue()))
    }

    @Test
    fun testWillKeepRegionNonCsvDetailsOnSummitUpdate() {
        runBlocking {
            repo.updateAssociation("HL")
            repo.updateRegion("HL", "GN")
            repo.updateSummit("HL", "GN", "001", true)
        }
        val association = repo.getAssociationByCode("HL").blockingObserve()
        assertThat(association, `is`(notNullValue()))
        val region = repo.getRegionByCode(association!!, "GN")
        region.observeForever {  }
        shadowOf(getMainLooper()).idle()
        assertNotNull("No value returned", region.value)
        val value = region.value!!
        assertEquals("GN", value.code)
        assertEquals(value.name, "Gyeongnam")
        assertThat(value.manager, `is`(notNullValue()))
    }

    @Test
    fun testWillLoadAllSummitCsvDetails() {
        runBlocking {
            withContext(Dispatchers.IO) {
                dao.clear()
            }
            repo.refreshAssociations()
        }
        val summit = repo.getSummitByCode("HL", "GN", "001")
        summit.observeForever {  }
        shadowOf(getMainLooper()).idle()
        //assertTrue("HTTP request not made", interceptor.rules[1].isConsumed)
        assertNotNull("No value returned", summit.value)
        val value = summit.value!!
        assertEquals("001", value.code)
        assertEquals(value.name, "지리산 (Jirisan)")
        assertEquals(value.altFt, 6284)
        assertEquals(value.altM, 1915)
        assertEquals(value.gridRef1, "127.7307")
        assertEquals(value.gridRef2, "35.3369")
        assertEquals(value.longitude, 127.73070, 1e-7)
        assertEquals(value.latitude, 35.33690, 1e-7)
        assertEquals(value.points, 10)
        assertEquals(value.bonusPoints, 3)
        assertEquals(value.validFrom, "01/07/2010")
        assertEquals(value.validTo, "31/12/2099")
        assertEquals(value.activationCount, 37)
        assertEquals(value.activationDate, "08/01/2022")
        assertEquals(value.activationCall, "DS5SQS")
    }

    @Test
    fun testWillKeepSummitNonCsvDetailsOnAssociationUpdate() {
        runBlocking {
            repo.updateAssociation("HL")
            repo.updateSummit("HL", "GN", "001")
            repo.updateAssociation("HL", true)
        }
        val summit = repo.getSummitByCode("HL", "GN", "001")
        summit.observeForever { }
        shadowOf(getMainLooper()).idle()
        assertNotNull("No value returned", summit.value)
        val value = summit.value!!
        assertEquals("001", value.code)
        assertEquals(value.name, "지리산 (Jirisan)")
        assertEquals(value.altFt, 6284)
        assertEquals(value.altM, 1915)
        assertEquals(value.gridRef1, "127.7307")
        assertEquals(value.gridRef2, "35.3369")
        assertEquals(value.longitude, 127.73070, 1e-5)
        assertEquals(value.latitude, 35.33690, 1e-5)
        assertEquals(value.points, 10)
        assertThat(value.valid, `is`(notNullValue()))
    }

    @Test
    fun testWillKeepAllSummitCsvDetailsOnRegionUpdate() {
        runBlocking {
            //withContext(Dispatchers.IO) {
            //    dao.clear()
            //}
            repo.refreshAssociations()
            repo.updateRegion("HL", "GN")
        }
        val summit = repo.getSummitByCode("HL", "GN", "001")
        summit.observeForever {  }
        shadowOf(getMainLooper()).idle()
        //assertTrue("HTTP request not made", interceptor.rules[1].isConsumed)
        assertNotNull("No value returned", summit.value)
        val value = summit.value!!
        assertEquals("001", value.code)
        assertEquals(value.name, "지리산 (Jirisan)")
        assertEquals(value.altFt, 6284)
        assertEquals(value.altM, 1915)
        assertEquals(value.gridRef1, "127.7307")
        assertEquals(value.gridRef2, "35.3369")
        assertEquals(value.longitude, 127.73070, 1e-5)
        assertEquals(value.latitude, 35.33690, 1e-5)
        assertEquals(value.points, 10)
        assertEquals(value.bonusPoints, 3)
        // FIXME dates are in a different format between APIs
        //assertEquals(value.validFrom, "01/07/2010")
        //assertEquals(value.validTo, "31/12/2099")
        assertEquals(value.activationCount, 37)
        //assertEquals(value.activationDate, "08/01/2022")
        assertEquals(value.activationCall, "DS5SQS")
    }

    @Test
    fun testCanEmptyGpxTracks() {
        runBlocking {
            repo.refreshAssociations()
        }
        val sld = repo.getSummits("W7W", "LC")
        val result = sld.blockingObserve()
        assertThat("Summits result is null", result, `is`(notNullValue()))
        assertThat("Has summits", result!!.count(), `is`(greaterThanOrEqualTo(1)))
        val summitIndex = result.indexOfFirst { it.code.contains("001") }
        assertThat("Found summit", summitIndex, `is`(greaterThanOrEqualTo(0)))
        val summit = result[summitIndex]
        runBlocking {
            repo.updateGpxTracks(summit)
        }
        val result2 = repo.getGpxTracks(summit)

        val gpxTracks = result2.blockingObserve()
        assertThat("Gpx Tracks is null", gpxTracks, `is`(notNullValue()))
        assertThat("Correct number of tracks", gpxTracks, `is`(empty()))
    }

    @Test
    fun testCanGpxTracks() {
        runBlocking {
            repo.refreshAssociations()
        }
        val sld = repo.getSummits("W7W", "LC")
        val result = sld.blockingObserve()
        assertThat("Summits result is null", result, `is`(notNullValue()))
        assertThat("Has summits", result!!.count(), `is`(greaterThanOrEqualTo(1)))
        val summitIndex = result.indexOfFirst { it.code.contains("050") }
        assertThat("Found summit", summitIndex, `is`(greaterThanOrEqualTo(0)))
        val summit = result[summitIndex]
        runBlocking {
            repo.updateGpxTracks(summit)
        }
        val result2 = repo.getGpxTracks(summit)

        val gpxTracks = result2.blockingObserve()
        assertThat("Gpx Tracks is null", gpxTracks, `is`(notNullValue()))
        assertThat("Correct number of tracks", gpxTracks!!.count(), `is`(equalTo(1)))
        val track = gpxTracks[0]
        assertThat("Has correct callsign", track.callsign, `is`(equalTo("ND7Y")))
        assertThat("Has valid notes", track.trackNotes, StringContains("Hike the trail"))
        assertThat("Has valid title", track.trackTitle, StringContains("Tillicum Campground"))
    }

    @Test
    fun testCanGpxTrackPoints() {
        runBlocking {
            repo.refreshAssociations()
        }
        val sld = repo.getSummits("W7W", "LC")
        val result = sld.blockingObserve()
        assertThat("Summits result is null", result, `is`(notNullValue()))
        assertThat("Has summits", result!!.count(), `is`(greaterThanOrEqualTo(1)))
        val summitIndex = result.indexOfFirst { it.code.contains("050") }
        assertThat("Found summit", summitIndex, `is`(greaterThanOrEqualTo(0)))
        val summit = result[summitIndex]
        runBlocking {
            repo.updateGpxTracks(summit)
        }
        val result2 = repo.getGpxTracks(summit)

        val gpxTracks = result2.blockingObserve()
        assertThat("Gpx Tracks is null", gpxTracks, `is`(notNullValue()))
        assertThat("Correct number of tracks", gpxTracks!!.count(), `is`(equalTo(1)))
        val track = gpxTracks[0]
        val points = repo.getGpxPoints(track).blockingObserve()
        assertThat("Gpx track points is null", points, `is`(notNullValue()))
        assertThat("Correct number of track points", points, hasSize(69))
        assertThat("Correct index", points!![0].index, `is`(equalTo(1)))
        assertThat("Correct latitude", points[0].latitude, `is`(closeTo(46.124222, 1e-6)))
        assertThat("Correct longitude", points[0].longitude, `is`(closeTo(-121.782583, 1e-6)))
        assertThat("Correct altitude", points[0].altitude, `is`(closeTo(1179.734, 1e-6)))
        assertThat("Correct distance", points[0].distance, `is`(closeTo(0.000, 1e-6)))
        assertThat("Correct index", points[68].index, `is`(equalTo(69)))
        assertThat("Correct latitude", points[68].latitude, `is`(closeTo(46.117085, 1e-6)))
        assertThat("Correct longitude", points[68].longitude, `is`(closeTo(-121.802399, 1e-6)))
        assertThat("Correct altitude", points[68].altitude, `is`(closeTo(1372.899, 1e-6)))
        assertThat("Correct distance", points[68].distance, `is`(closeTo(10.357, 1e-6)))
    }
}

private fun <T> LiveData<T>.blockingObserve(): T? {
    var value: T? = null
    val latch = CountDownLatch(1)

    val observer = Observer<T> { t ->
        value = t
        latch.countDown()
    }
    //assertEquals("Looper queued up", true, Looper.myLooper()!!.queue.isIdle)

    observeForever(observer)
    //assertEquals("Looper queued up", true, Looper.myLooper()!!.queue.isIdle)

    shadowOf(getMainLooper()).idle()
    //latch.await(35, TimeUnit.SECONDS)
    //assertEquals("Looper queued up", true, Looper.myLooper()!!.queue.isIdle)
    return value
}
