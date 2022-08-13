package org.northwinds.app.sotachaser.ui.summits

import android.Manifest
import android.content.Context
import androidx.core.content.edit
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import com.dannyroa.espresso_samples.recyclerview.RecyclerViewMatcher
import com.github.flank.utility.screenshot.ScreenshotTestRule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import org.aprsdroid.app.testing.SharedPreferencesRule
import org.hamcrest.Matchers.*
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.northwinds.app.sotachaser.BuildConfig
import org.northwinds.app.sotachaser.R
import org.northwinds.app.sotachaser.SotaChaserBaseApplication
import org.northwinds.app.sotachaser.repository.SummitsRepository
import org.northwinds.app.sotachaser.room.SummitDao
import org.northwinds.app.sotachaser.testing.HiltFragmentScenario
import org.northwinds.app.sotachaser.testing.Matcher.atPosition
import org.northwinds.app.sotachaser.ui.MainActivity
import org.northwinds.app.sotachaser.ui.associations.AssociationRecyclerViewAdapterVH
import org.northwinds.app.sotachaser.ui.regions.RegionFragmentDirections
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class SummitFragmentTest {
    private val context = ApplicationProvider.getApplicationContext<SotaChaserBaseApplication>()

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var dao: SummitDao

    @Inject
    lateinit var repo: SummitsRepository

    @get:Rule
    val permissionRule: GrantPermissionRule = GrantPermissionRule.grant(Manifest.permission.ACCESS_COARSE_LOCATION)

    @Before
    fun setUp() {
        hiltRule.inject()
        val prefs = context.getSharedPreferences("database", Context.MODE_PRIVATE)
        prefs.edit { putBoolean("database_loaded", false) }
    }

    @Test
    fun loadSummit() {
        val frag = HiltFragmentScenario.launchInHiltContainer(SummitFragment::class.java,
            RegionFragmentDirections.actionRegionFragmentToNavigationDashboard("HL", "GN").arguments)
        onView(withId(R.id.list)).check(matches(isDisplayed()))
        frag.close()
    }

    @Test
    fun loadSpecificSummitList() {
        runBlocking {
            repo.refreshAssociations(true)
            //repo.updateAssociation("W7O")
        }
        val frag = HiltFragmentScenario.launchInHiltContainer(SummitFragment::class.java,
            RegionFragmentDirections.actionRegionFragmentToNavigationDashboard("W7O", "NC").arguments)
        onView(withId(R.id.list)).check { view, noMatchingViewException ->
            if (view == null)
                throw noMatchingViewException
            val recyclerView = view as RecyclerView
            assertEquals(127, recyclerView.adapter?.itemCount)
        }
        //frag.close()
    }

    @Test
    fun loadMultipleSummitLists() {
        runBlocking {
            repo.refreshAssociations(true)
        }
        var frag = HiltFragmentScenario.launchInHiltContainer(SummitFragment::class.java,
            RegionFragmentDirections.actionRegionFragmentToNavigationDashboard("W7O", "NC").arguments)
        onView(withId(R.id.list)).check { view, noMatchingViewException ->
            if (view == null)
                throw noMatchingViewException
            val recyclerView = view as RecyclerView
            assertEquals(127, recyclerView.adapter?.itemCount)
        }
        //frag.close()

        frag = HiltFragmentScenario.launchInHiltContainer(SummitFragment::class.java,
            RegionFragmentDirections.actionRegionFragmentToNavigationDashboard("BV", "HL").arguments)
        onView(withId(R.id.list)).check { view, noMatchingViewException ->
            if (view == null)
                throw noMatchingViewException
            val recyclerView = view as RecyclerView
            assertEquals(76, recyclerView.adapter?.itemCount)
        }
        //frag.close()
    }

    @Test
    fun loadSummitSummary() {
        runBlocking {
            repo.refreshAssociations(true)
        }
        val frag = HiltFragmentScenario.launchInHiltContainer(SummitFragment::class.java,
            RegionFragmentDirections.actionRegionFragmentToNavigationDashboard("HL", "GN").arguments)
        onView(RecyclerViewMatcher(R.id.list).atPosition(0)).check(
            matches(
                hasDescendant(
                    withText(
                        containsString("지리산")
                    )
                )
            )
        )
        onView(withId(R.id.list)).check(
            matches(
                atPosition(
                    0,
                    hasDescendant(allOf(withId(R.id.points), withText(containsString("10"))))
                )
            )
        )
        onView(RecyclerViewMatcher(R.id.list).atPositionOnView(0, R.id.points)).check(
            matches(
                withText(containsString("10"))
            )
        )
        onView(withId(R.id.list)).check(
            matches(
                atPosition(
                    0,
                    hasDescendant(allOf(withId(R.id.altitude), withText(containsString("6284"))))
                )
            )
        )
        onView(RecyclerViewMatcher(R.id.list).atPositionOnView(0, R.id.altitude)).check(
            matches(
                withText(containsString("6284"))
            )
        )
        onView(RecyclerViewMatcher(R.id.list).atPositionOnView(0, R.id.activation_callsign)).check(
            matches(withText(containsString("DS5SQS")))
        )
        onView(RecyclerViewMatcher(R.id.list).atPositionOnView(0, R.id.activation_date)).check(
            matches(withText(containsString("2022-01-08")))
        )
        onView(RecyclerViewMatcher(R.id.list).atPositionOnView(0, R.id.activation_count)).check(
            matches(withText(containsString("37")))
        )
    }

    @Test
    fun loadSummitSummaryAtBottomOfList() {
        runBlocking {
            repo.refreshAssociations(true)
        }
        // Position for summit HL/GN-046
        val summitPosition = 44  // Zero-indexed and summit HL/GN-011 doesn't exist
        val frag = HiltFragmentScenario.launchInHiltContainer(SummitFragment::class.java,
            RegionFragmentDirections.actionRegionFragmentToNavigationDashboard("HL", "GN").arguments)
        onView(withId(R.id.list)).perform(
            scrollToPosition<SummitRecyclerViewAdapterVH>(summitPosition)
        )
        onView(RecyclerViewMatcher(R.id.list).atPosition(summitPosition)).check(
            matches(
                hasDescendant(withText(containsString("뒷삐알산")))
            )
        )
        onView(
            RecyclerViewMatcher(R.id.list).atPositionOnView(
                summitPosition,
                R.id.code
            )
        ).check(matches(withText(containsString("046"))))
        onView(RecyclerViewMatcher(R.id.list).atPositionOnView(summitPosition, R.id.name)).check(
            matches(withText(containsString("뒷삐알산")))
        )
        onView(RecyclerViewMatcher(R.id.list).atPositionOnView(summitPosition, R.id.points)).check(
            matches(withText(containsString("6")))
        )
        onView(
            RecyclerViewMatcher(R.id.list).atPositionOnView(
                summitPosition,
                R.id.altitude
            )
        ).check(matches(withText(containsString("2713"))))
        onView(
            RecyclerViewMatcher(R.id.list).atPositionOnView(
                summitPosition,
                R.id.activation_callsign
            )
        ).check(matches(withText(containsString("DS5VKX"))))
        onView(
            RecyclerViewMatcher(R.id.list).atPositionOnView(
                summitPosition,
                R.id.activation_date
            )
        ).check(matches(withText(containsString("2020-10-02"))))
        onView(
            RecyclerViewMatcher(R.id.list).atPositionOnView(
                summitPosition,
                R.id.activation_count
            )
        ).check(matches(withText(containsString("9"))))
        //kotlin.test.assertEquals("HL/GN-046", entry.summitCode)
        //kotlin.test.assertEquals("South Korea", entry.associationName)
        //kotlin.test.assertEquals("Gyeongnam", entry.regionName)
        //kotlin.test.assertEquals("뒷삐알산 (Dwitppialsan)", entry.summitName)
        //kotlin.test.assertEquals(827, entry.altM)
        //kotlin.test.assertEquals(2713, entry.altFt)
        //kotlin.test.assertEquals("128.9960", entry.gridRef1)
        //kotlin.test.assertEquals("35.4368", entry.gridRef2)
        //kotlin.test.assertEquals(128.996, entry.longitude)
        //kotlin.test.assertEquals(35.4368, entry.latitude)
        //kotlin.test.assertEquals(6, entry.points)
        //kotlin.test.assertEquals(3, entry.bonusPoints)
        //kotlin.test.assertEquals("01/07/2010", entry.validFrom)
        //kotlin.test.assertEquals("31/12/2099", entry.validTo)
        //kotlin.test.assertEquals(9, entry.activationCount)
        //kotlin.test.assertEquals("02/10/2020", entry.activationDate)
        //kotlin.test.assertEquals("DS5VKX", entry.activationCall)
    }

    @Test
    fun filterSummitList() {
        runBlocking {
            repo.refreshAssociations(true)
        }
        val frag = HiltFragmentScenario.launchInHiltContainer(SummitFragment::class.java,
            RegionFragmentDirections.actionRegionFragmentToNavigationDashboard("W7O", "CN").arguments)
        onView(withId(R.id.filter)).perform(ViewActions.replaceText("Ridge"))
        onView(withId(R.id.list)).check { view, noMatchingViewException ->
            if (view == null)
                throw noMatchingViewException
            val recyclerView = view as RecyclerView
            assertEquals(6, recyclerView.adapter?.itemCount)
        }
        onView(withId(R.id.filter)).perform(ViewActions.replaceText("Hood"))
        onView(withId(R.id.list)).check { view, noMatchingViewException ->
            if (view == null)
                throw noMatchingViewException
            val recyclerView = view as RecyclerView
            assertEquals(1, recyclerView.adapter?.itemCount)
        }
        onView(withId(R.id.filter)).perform(ViewActions.replaceText("Missing"))
        onView(withId(R.id.list)).check { view, noMatchingViewException ->
            if (view == null)
                throw noMatchingViewException
            val recyclerView = view as RecyclerView
            assertEquals(0, recyclerView.adapter?.itemCount)
        }
    }

    @Test
    fun loadSummitsAfterClear() {
        dao.clear()
        runBlocking {
            // FIXME This is passing, but it shouldn't without this line???
            //repo.updateAssociation("W7O")
        }
        val frag = HiltFragmentScenario.launchInHiltContainer(SummitFragment::class.java,
            RegionFragmentDirections.actionRegionFragmentToNavigationDashboard("W7O", "CN").arguments)
        onView(withId(R.id.list)).check(matches(isDisplayed()))
        onView(withId(R.id.list)).check { view, noMatchingViewException ->
            if (view == null)
                throw noMatchingViewException
            val recyclerView = view as RecyclerView
            assertThat(recyclerView.adapter?.itemCount,
                `is`(greaterThanOrEqualTo(10))
            )
        }
    }
}

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class SummitActivityTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    private val context = ApplicationProvider.getApplicationContext<SotaChaserBaseApplication>()

    @get:Rule(order = 1)
    val permissionRule: GrantPermissionRule = GrantPermissionRule.grant(Manifest.permission.ACCESS_COARSE_LOCATION)

    @get:Rule(order = 1)
    val prefsRule = SharedPreferencesRule {
        it.edit {
            clear()
            putBoolean(context.getString(R.string.preference_asked_for_consent), true)
            putInt(context.getString(R.string.preference_changelog), BuildConfig.VERSION_CODE)
        }
    }

    @get:Rule(order = 2)
    var mActivityScenarioRule = ActivityScenarioRule(MainActivity::class.java)

    @get:Rule(order = 2)
    val writeStorageRule: GrantPermissionRule = GrantPermissionRule.grant(Manifest.permission.WRITE_EXTERNAL_STORAGE)

    @get:Rule(order = 3)
    val mScreenshotTestRule = ScreenshotTestRule()

    @Test
    fun loadSummitDetailsFragment() {
        // Position for association W7O
        val summitPosition = 169  // Zero-indexed
        onView(withId(R.id.associationFragment)).perform(click())
        onView(withId(R.id.list)).perform(
            scrollToPosition<AssociationRecyclerViewAdapterVH>(summitPosition)
        )
        onView(withText(containsString("W7O"))).perform(click())
        onView(withText(containsString("CN"))).perform(click())
        onView(withText(containsString("W7O/CN-001"))).perform(click())
        onView(withId(R.id.map)).check(matches(isDisplayed()))
    }

    @Test
    fun loadCorrectSummitDetailsFragment() {
        // Position for association W7O
        val summitPosition = 169  // Zero-indexed
        onView(withId(R.id.associationFragment)).perform(click())
        onView(withId(R.id.list)).perform(
            scrollToPosition<AssociationRecyclerViewAdapterVH>(summitPosition)
        )
        onView(withText(containsString("W7O"))).perform(click())
        onView(withText(containsString("CN"))).perform(click())
        onView(withText(containsString("W7O/CN-001"))).perform(click())
        onView(withId(R.id.map)).check(matches(isDisplayed()))
        onView(withId(R.id.code))
            .check(matches(withText(containsString("W7O/CN-001"))))
        onView(withId(R.id.name))
            .check(matches(withText(containsString("Mount Hood"))))
        //onView(withId(R.id.name))
        //    .check(matches(withText(containsString("지리산"))))
    }

    @Test
    fun loadDifferentSummitDetailsFragment() {
        // Position for association HL
        val summitPosition = 55  // Zero-indexed
        onView(withId(R.id.associationFragment)).perform(click())
        onView(withId(R.id.list)).perform(
            scrollToPosition<AssociationRecyclerViewAdapterVH>(summitPosition)
        )
        onView(allOf(withId(R.id.code), withText(containsString("HL")))).perform(click())
        onView(withId(R.id.list)).perform(
            scrollToPosition<SummitRecyclerViewAdapterVH>(9))
        onView(withText(containsString("GN"))).perform(click())
        onView(withId(R.id.list)).perform(
            scrollToPosition<SummitRecyclerViewAdapterVH>(44))
        onView(withText(containsString("HL/GN-046"))).perform(click())
        onView(withId(R.id.map)).check(matches(isDisplayed()))
        onView(withId(R.id.code))
            .check(matches(withText(containsString("HL/GN-046"))))
        onView(withId(R.id.name))
            .check(matches(withText(containsString("뒷삐알산"))))
    }
}
