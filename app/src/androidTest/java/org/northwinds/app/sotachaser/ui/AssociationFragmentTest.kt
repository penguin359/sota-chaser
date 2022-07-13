package org.northwinds.app.sotachaser.ui

import android.Manifest
import androidx.core.content.edit
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
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
import org.northwinds.app.sotachaser.ui.associations.AssociationFragment
import org.northwinds.app.sotachaser.ui.associations.AssociationRecyclerViewAdapter

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class AssociationFragmentTest {
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
    val permissionRule = GrantPermissionRule.grant(Manifest.permission.ACCESS_COARSE_LOCATION)

    @Test
    fun loadAssociation() {
        val frag = HiltFragmentScenario.launchInHiltContainer(AssociationFragment::class.java)
        onView(withId(R.id.list)).check(matches(isDisplayed()))
    }

    @Test
    fun loadFullAssociationList() {
        val frag = HiltFragmentScenario.launchInHiltContainer(AssociationFragment::class.java)
        onView(withId(R.id.list)).check { view, noMatchingViewException ->
            if (view == null)
                throw noMatchingViewException
            val recyclerView = view as RecyclerView
            assertEquals(194, recyclerView.adapter?.itemCount)
        }
    }

    @Test
    fun loadAssociationSummary() {
        val frag = HiltFragmentScenario.launchInHiltContainer(AssociationFragment::class.java)
        onView(RecyclerViewMatcher(R.id.list).atPosition(0)).check(
            matches(
                hasDescendant(
                    withText(
                        containsString("3Y")
                    )
                )
            )
        )
        onView(withId(R.id.list)).check(
            matches(
                atPosition(
                    0,
                    hasDescendant(allOf(withId(R.id.name), withText(containsString("Bouvet Island"))))
                )
            )
        )
    }

    @Test
    fun loadAssociationSummaryAtBottomOfList() {
        // Position for association W7O
        val summitPosition = 167  // Zero-indexed
        val frag = HiltFragmentScenario.launchInHiltContainer(AssociationFragment::class.java)
        onView(withId(R.id.list)).perform(
            scrollToPosition<AssociationRecyclerViewAdapter.ViewHolder>(summitPosition)
        )
        onView(RecyclerViewMatcher(R.id.list).atPosition(summitPosition)).check(
            matches(
                hasDescendant(withText(containsString("Oregon")))
            )
        )
        onView(
            RecyclerViewMatcher(R.id.list).atPositionOnView(
                summitPosition,
                R.id.code
            )
        ).check(matches(withText(containsString("W7O"))))
        onView(RecyclerViewMatcher(R.id.list).atPositionOnView(summitPosition, R.id.name)).check(
            matches(withText(containsString("USA - Oregon")))
        )
        onView(RecyclerViewMatcher(R.id.list).atPositionOnView(summitPosition, R.id.manager)).check(
            matches(withText(containsString("Etienne")))
        )
        onView(
            RecyclerViewMatcher(R.id.list).atPositionOnView(
                summitPosition,
                R.id.manager_callsign
            )
        ).check(matches(withText(containsString("K7ATN"))))
        onView(
            RecyclerViewMatcher(R.id.list).atPositionOnView(
                summitPosition,
                R.id.active_from
            )
        ).check(matches(withText(containsString("2010-07-01T00:00:00"))))
        onView(
            RecyclerViewMatcher(R.id.list).atPositionOnView(
                summitPosition,
                R.id.region_count
            )
        ).check(matches(withText(containsString("10"))))
        onView(
            RecyclerViewMatcher(R.id.list).atPositionOnView(
                summitPosition,
                R.id.summit_count
            )
        ).check(matches(withText(containsString("1990"))))
    }
}

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class AssociationActivityTest {
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

    @get:Rule(order = 2)
    val writeStorageRule = GrantPermissionRule.grant(Manifest.permission.WRITE_EXTERNAL_STORAGE)

    @get:Rule(order = 3)
    val mScreenshotTestRule = ScreenshotTestRule()

    @Test
    fun loadRegionFragment() {
        onView(withId(R.id.navigation_dashboard)).perform(click())
        onView(withText(containsString("W7O"))).perform(click())
        onView(withId(R.id.points)).check(matches(isDisplayed()))
    }

    @Test
    fun loadCorrectAssociationDetailsFragment() {
        onView(withId(R.id.navigation_dashboard)).perform(click())
        onView(withText(containsString("W7O"))).perform(click())
        onView(withId(R.id.points)).check(matches(isDisplayed()))
        onView(allOf(withId(R.id.code)))
            .check(matches(withText(containsString("W7O/CN-001"))))
        onView(withId(R.id.name))
            .check(matches(withText(containsString("Mount Hood"))))
        //onView(withId(R.id.name))
        //    .check(matches(withText(containsString("지리산"))))
    }

    @Test
    fun loadDifferentAssociationDetailsFragment() {
        onView(withId(R.id.navigation_dashboard)).perform(click())
        onView(withId(R.id.association)).perform(click())
        onData(
            allOf(`is`(instanceOf(Map::class.java)), hasEntry("code", "HL"))
        ).perform(click())
        onView(withId(R.id.region)).perform(click())
        onData(
            allOf(`is`(instanceOf(Map::class.java)), hasEntry("code", "GN"))
        ).perform(click())
        onView(withId(R.id.list)).perform(
            scrollToPosition<AssociationRecyclerViewAdapter.ViewHolder>(44))
        onView(withText(containsString("HL/GN-046"))).perform(click())
        onView(withId(R.id.map)).check(matches(isDisplayed()))
        onView(withId(R.id.summit_id))
            .check(matches(withText(containsString("HL/GN-046"))))
        onView(withId(R.id.name))
            .check(matches(withText(containsString("뒷삐알산"))))
    }
}
