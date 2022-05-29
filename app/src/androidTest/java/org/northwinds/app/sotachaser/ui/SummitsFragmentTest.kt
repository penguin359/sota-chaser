package org.northwinds.app.sotachaser.ui

import android.Manifest
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.DataInteraction
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import com.dannyroa.espresso_samples.recyclerview.RecyclerViewMatcher
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import junit.framework.Assert.assertEquals
import org.aprsdroid.app.testing.SharedPreferencesRule
import org.hamcrest.Matchers.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.northwinds.app.sotachaser.BuildConfig
import org.northwinds.app.sotachaser.R
import org.northwinds.app.sotachaser.SotaChaserBaseApplication
import org.northwinds.app.sotachaser.testing.HiltFragmentScenario
import org.northwinds.app.sotachaser.testing.Matcher.atPosition
import org.northwinds.app.sotachaser.testing.MySummitRecyclerViewAdapter
import org.northwinds.app.sotachaser.testing.SummitFragment
import java.lang.Thread.sleep

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class SummitsFragmentTest {
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
    val permissionRule = GrantPermissionRule.grant(Manifest.permission.ACCESS_COARSE_LOCATION)

    @Test
    fun loadSummit() {
        val frag = HiltFragmentScenario.launchInHiltContainer(SummitFragment::class.java)
        onView(withId(R.id.list)).check(matches(isDisplayed()))
    }

    @Test
    fun loadSpecificSummitList() {
        val frag = HiltFragmentScenario.launchInHiltContainer(SummitFragment::class.java)
        onView(withId(R.id.association)).perform(click())
        onData(
            allOf(`is`(instanceOf(String::class.java)), `is`(equalTo("W7O")))
        ).perform(click())
        onView(withId(R.id.association)).check(
            matches(
                withSpinnerText(containsString("W7O"))
            )
        )
        onView(withId(R.id.region)).perform(click())
        onData(
            allOf(`is`(instanceOf(String::class.java)), `is`(equalTo("NC")))
        ).perform(click())
        onView(withId(R.id.region)).check(
            matches(
                withSpinnerText(containsString("NC"))
            )
        )
        onView(withId(R.id.list)).check { view, noMatchingViewException ->
            if (view == null)
                throw noMatchingViewException
            val recyclerView = view as RecyclerView
            assertEquals(127, recyclerView.adapter?.itemCount)
        }
    }

    @Test
    fun loadMultipleSummitLists() {
        val frag = HiltFragmentScenario.launchInHiltContainer(SummitFragment::class.java)
        onView(withId(R.id.association)).perform(click())
        onData(
            allOf(`is`(instanceOf(String::class.java)), `is`(equalTo("W7O")))
        ).perform(click())
        onView(withId(R.id.association)).check(
            matches(
                withSpinnerText(containsString("W7O"))
            )
        )
        onView(withId(R.id.region)).perform(click())
        onData(
            allOf(`is`(instanceOf(String::class.java)), `is`(equalTo("NC")))
        ).perform(click())
        onView(withId(R.id.region)).check(
            matches(
                withSpinnerText(containsString("NC"))
            )
        )
        onView(withId(R.id.list)).check { view, noMatchingViewException ->
            if (view == null)
                throw noMatchingViewException
            val recyclerView = view as RecyclerView
            assertEquals(127, recyclerView.adapter?.itemCount)
        }

        onView(withId(R.id.association)).perform(click())
        onData(
            allOf(`is`(instanceOf(String::class.java)), `is`(equalTo("BV")))
        ).perform(click())
        onView(withId(R.id.association)).check(
            matches(
                withSpinnerText(containsString("BV"))
            )
        )
        onView(withId(R.id.region)).perform(click())
        onData(
            allOf(`is`(instanceOf(String::class.java)), `is`(equalTo("HL")))
        ).perform(click())
        onView(withId(R.id.region)).check(
            matches(
                withSpinnerText(containsString("HL"))
            )
        )
        onView(withId(R.id.list)).check { view, noMatchingViewException ->
            if (view == null)
                throw noMatchingViewException
            val recyclerView = view as RecyclerView
            assertEquals(76, recyclerView.adapter?.itemCount)
        }
    }

    @Test
    fun loadSummitSummary() {
        val frag = HiltFragmentScenario.launchInHiltContainer(SummitFragment::class.java)
        onView(withId(R.id.association)).perform(click())
        onData(
            allOf(`is`(instanceOf(String::class.java)), `is`(equalTo("HL")))
        ).perform(click())
        onView(withId(R.id.region)).perform(click())
        onData(
            allOf(`is`(instanceOf(String::class.java)), `is`(equalTo("GN")))
        ).perform(click())
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
            matches(withText(containsString("08/01/2022")))
        )
        onView(RecyclerViewMatcher(R.id.list).atPositionOnView(0, R.id.activation_count)).check(
            matches(withText(containsString("37")))
        )
    }

    @Test
    fun loadSummitSummaryAtBottomOfList() {
        // Position for summit HL/GN-046
        val summitPosition = 44  // Zero-indexed and summit HL/GN-011 doesn't exist
        val frag = HiltFragmentScenario.launchInHiltContainer(SummitFragment::class.java)
        onView(withId(R.id.association)).perform(click())
        onData(
            allOf(`is`(instanceOf(String::class.java)), `is`(equalTo("HL")))
        ).perform(click())
        onView(withId(R.id.region)).perform(click())
        onData(
            allOf(`is`(instanceOf(String::class.java)), `is`(equalTo("GN")))
        ).perform(click())
        onView(withId(R.id.list)).perform(
            scrollToPosition<MySummitRecyclerViewAdapter.ViewHolder>(summitPosition)
        )
        onView(RecyclerViewMatcher(R.id.list).atPosition(summitPosition)).check(
            matches(
                hasDescendant(withText(containsString("뒷삐알산")))
            )
        )
        onView(
            RecyclerViewMatcher(R.id.list).atPositionOnView(
                summitPosition,
                R.id.summit_id
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
        ).check(matches(withText(containsString("02/10/2020"))))
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
}

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class SummitsActivityTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    private val context = ApplicationProvider.getApplicationContext<SotaChaserBaseApplication>()

    @get:Rule(order = 1)
    val permissionRule = GrantPermissionRule.grant(Manifest.permission.ACCESS_COARSE_LOCATION)

    @get:Rule(order = 1)
    val prefsRule = SharedPreferencesRule() {
        it.edit() {
            clear()
            putBoolean(context.getString(R.string.preference_asked_for_consent), true)
            putInt(context.getString(R.string.preference_changelog), BuildConfig.VERSION_CODE)
        }
    }

    @get:Rule(order = 2)
    var mActivityScenarioRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun loadSummitDetailsFragment() {
        onView(withId(R.id.navigation_dashboard)).perform(click())
        onView(withId(R.id.association)).perform(click())
        onData(
            allOf(`is`(instanceOf(String::class.java)), `is`(equalTo("W7O")))
        ).perform(click())
        onView(withId(R.id.region)).perform(click())
        onData(
            allOf(`is`(instanceOf(String::class.java)), `is`(equalTo("CN")))
        ).perform(click())
        sleep(5000)
        onView(withText(containsString("W7O/CN-001"))).perform(click())
        onView(withId(R.id.map)).check(matches(isDisplayed()))
    }

    @Test
    fun loadCorrectSummitDetailsFragment() {
        onView(withId(R.id.navigation_dashboard)).perform(click())
        onView(withId(R.id.association)).perform(click())
        onData(
            allOf(`is`(instanceOf(String::class.java)), `is`(equalTo("W7O")))
        ).perform(click())
        onView(withId(R.id.region)).perform(click())
        onData(
            allOf(`is`(instanceOf(String::class.java)), `is`(equalTo("CN")))
        ).perform(click())
        //sleep(5000)
        onView(withText(containsString("W7O/CN-001"))).perform(click())
        onView(withId(R.id.map)).check(matches(isDisplayed()))
        onView(withId(R.id.summit_id))
            .check(matches(withText(containsString("W7O/CN-001"))))
        onView(withId(R.id.name))
            .check(matches(withText(containsString("Mount Hood"))))
        //onView(withId(R.id.name))
        //    .check(matches(withText(containsString("지리산"))))
    }

    @Test
    fun loadDifferentSummitDetailsFragment() {
        onView(withId(R.id.navigation_dashboard)).perform(click())
        onView(withId(R.id.association)).perform(click())
        onData(
            allOf(`is`(instanceOf(String::class.java)), `is`(equalTo("HL")))
        ).perform(click())
        onView(withId(R.id.region)).perform(click())
        onData(
            allOf(`is`(instanceOf(String::class.java)), `is`(equalTo("GN")))
        ).perform(click())
        onView(withId(R.id.list)).perform(
            scrollToPosition<MySummitRecyclerViewAdapter.ViewHolder>(44))
        onView(withText(containsString("HL/GN-046"))).perform(click())
        onView(withId(R.id.map)).check(matches(isDisplayed()))
        onView(withId(R.id.summit_id))
            .check(matches(withText(containsString("HL/GN-046"))))
        onView(withId(R.id.name))
            .check(matches(withText(containsString("뒷삐알산"))))
    }
}
