package org.northwinds.app.sotachaser

import android.os.Looper.getMainLooper
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import kotlinx.coroutines.runBlocking
import okhttp3.mock.MockInterceptor
import org.junit.After
import org.junit.Assert.*
import org.junit.Test
import org.northwinds.app.sotachaser.repository.SummitsRepository
import javax.inject.Inject
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith
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

    @Inject lateinit var repo: SummitsRepository
    @Inject lateinit var db: SummitDatabase

    @Before
    fun setUp() {
        hiltRule.inject()
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
            repo.checkForRefresh()
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
            repo.checkForRefresh()
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
            repo.checkForRefresh()
        }
        assertTrue("HTTP request not made", interceptor.rules[0].isConsumed)
    }

    @Test
    fun testWillLoadAssociationExtraDetails() {
        runBlocking {
            repo.checkForRefresh()
            repo.updateAssociation("W7O")
        }
        assertTrue("HTTP request not made", interceptor.rules[0].isConsumed)
        val association = repo.getAssociationByCode("W7O")
        association.observeForever {  }
        shadowOf(getMainLooper()).idle()
        assertTrue("HTTP request not made", interceptor.rules[2].isConsumed)
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
            repo.checkForRefresh()
            repo.updateRegion("W7O", "CN")
        }
        val association = repo.getAssociationByCode("W7O")
        association.observeForever {  }
        shadowOf(getMainLooper()).idle()
        // TODO Change to getOrAwaitValue()???
        val region = repo.getRegionByCode(association.value!!, "CN")
        region.observeForever {  }
        shadowOf(getMainLooper()).idle()
        assertTrue("HTTP request not made", interceptor.rules[3].isConsumed)
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
