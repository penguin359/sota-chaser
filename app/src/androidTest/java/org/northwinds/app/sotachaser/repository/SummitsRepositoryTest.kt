package org.northwinds.app.sotachaser.repository

import androidx.test.espresso.Espresso
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.northwinds.app.sotachaser.room.SummitDao
import java.lang.Thread.sleep
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class SummitsRepositoryTest {
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var dao: SummitDao

    @Inject
    lateinit var repo: SummitsRepository

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    //@UiThreadTest
    fun canGetGpxTracks() {
        //val association = repo.getAssociationByCode("W7W")
        //association.observeForever { }
        //Espresso.onIdle()
        //val region = repo.getRegionByCode(association.value!!, "LC")
        //val summit = repo.getSummits(region.value!!.id, "050")
        dao.clear()
        runBlocking {
            repo.checkForRefresh()
        }
        val summitsData = repo.getSummits("W7W", "LC")
        runOnUiThread {
            summitsData.observeForever { }
        }
        Espresso.onIdle()
        sleep(1000)
        Espresso.onIdle()
        assertThat("Has list of summits", summitsData.value, `is`(notNullValue()))
        val summits = summitsData.value!!
        assertThat("Has many summits", summits.count(), `is`(greaterThanOrEqualTo(50)))
        val summitIndex = summits.indexOfFirst { it.code == "W7W/LC-050" }
        assertThat("Has required summit", summitIndex, `is`(greaterThanOrEqualTo(0)))
        val summit = summits[summitIndex]
        //val summit = Summit(
        //    code = "W7W/LC-050",
        //)
        runBlocking {
            repo.updateGpxTracks(summit)
        }
        val tracksData = repo.getGpxTracks(summit)
        runOnUiThread {
            tracksData.observeForever { }
        }
        Espresso.onIdle()
        assertThat("Has a track value", tracksData.value, `is`(notNullValue()))
        val tracks = tracksData.value!!
        assertThat("Has a single track", tracks, hasSize(1))
        val track = tracks[0]
        assertThat(track.callsign, containsString("ND7Y"))
    }
}
