package org.northwinds.app.sotachaser.ui.associations

import android.Manifest
import android.content.Context
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
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
import org.northwinds.app.sotachaser.room.SummitDao
import org.northwinds.app.sotachaser.testing.HiltFragmentScenario
import org.northwinds.app.sotachaser.testing.Matcher.atPosition
import org.northwinds.app.sotachaser.ui.MainActivity
import java.lang.Thread.sleep
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class AssociationFragmentTest {
    private val context = ApplicationProvider.getApplicationContext<SotaChaserBaseApplication>()

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var dao: SummitDao

    @get:Rule
    val permissionRule: GrantPermissionRule = GrantPermissionRule.grant(Manifest.permission.ACCESS_COARSE_LOCATION)

    @Before
    fun setUp() {
        hiltRule.inject()
        val prefs = context.getSharedPreferences("database", Context.MODE_PRIVATE)
        prefs.edit { putBoolean("database_loaded", false) }
    }

    @Test
    fun loadAssociation() {
        val frag = HiltFragmentScenario.launchInHiltContainer(AssociationFragment::class.java)
        onView(withId(R.id.list)).check(matches(isDisplayed()))
        frag.close()
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
        frag.close()
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
        frag.close()
    }

    @Test
    fun loadAssociationSummaryAtBottomOfList() {
        // Position for association W7O
        val summitPosition = 167  // Zero-indexed
        val frag = HiltFragmentScenario.launchInHiltContainer(AssociationFragment::class.java)
        onView(withId(R.id.list)).perform(
            scrollToPosition<AssociationRecyclerViewAdapterVH>(summitPosition)
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
        frag.close()
    }

    @Test
    fun filterAssociationList() {
        val frag = HiltFragmentScenario.launchInHiltContainer(AssociationFragment::class.java)
        onView(withId(R.id.filter)).perform(replaceText("3"))
        onView(withId(R.id.list)).check { view, noMatchingViewException ->
            if (view == null)
                throw noMatchingViewException
            val recyclerView = view as RecyclerView
            assertEquals(13, recyclerView.adapter?.itemCount)
        }
        onView(withId(R.id.filter)).perform(replaceText("3Y"))
        onView(withId(R.id.list)).check { view, noMatchingViewException ->
            if (view == null)
                throw noMatchingViewException
            val recyclerView = view as RecyclerView
            assertEquals(1, recyclerView.adapter?.itemCount)
        }
        onView(withId(R.id.filter)).perform(replaceText("3YZ"))
        onView(withId(R.id.list)).check { view, noMatchingViewException ->
            if (view == null)
                throw noMatchingViewException
            val recyclerView = view as RecyclerView
            assertEquals(0, recyclerView.adapter?.itemCount)
        }
        frag.close()
    }

    @Test
    fun loadAssociationsAfterClear() {
        dao.clear()
        val frag = HiltFragmentScenario.launchInHiltContainer(AssociationFragment::class.java)
        onView(withId(R.id.list)).check(matches(isDisplayed()))
        onView(withId(R.id.list)).check { view, noMatchingViewException ->
            if (view == null)
                throw noMatchingViewException
            val recyclerView = view as RecyclerView
            assertThat(recyclerView.adapter?.itemCount, `is`(greaterThanOrEqualTo(10)))
        }
        frag.close()
    }
}

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class AssociationActivityTest {
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

    @Before
    fun setUp() {
        // FIXME Workaround caching issue with Summit Repository
        val prefs = context.getSharedPreferences("database", Context.MODE_PRIVATE)
        prefs.edit { putBoolean("database_loaded", false) }
    }

    @Test
    fun loadRegionFragment() {
        onView(withId(R.id.associationFragment)).perform(click())
        onView(withText(containsString("3Y"))).perform(click())
        onView(withId(R.id.name)).check(matches(allOf(isDisplayed(), withText(containsString("Bouvetøya")))))
    }

    @Test
    fun loadCorrectAssociationDetailsFragment() {
        // Position for association W7O
        val summitPosition = 169  // Zero-indexed
        onView(withId(R.id.associationFragment)).perform(click())
        onView(withId(R.id.list)).perform(
            scrollToPosition<AssociationRecyclerViewAdapterVH>(summitPosition)
        )
        onView(withText(containsString("W7O"))).perform(click())
        onView(allOf(withId(R.id.code), withText(containsString("CC"))))
            .check(matches(isDisplayed()))
        onView(allOf(withId(R.id.name), withText(containsString("Central Coast"))))
            .check(matches(isDisplayed()))
    }

    @Test
    fun loadDifferentAssociationDetailsFragment() {
        // Position for association BV
        val summitPosition = 11  // Zero-indexed
        onView(withId(R.id.associationFragment)).perform(click())
        onView(withId(R.id.list)).perform(
            scrollToPosition<AssociationRecyclerViewAdapterVH>(summitPosition)
        )
        onView(withText(containsString("BV"))).perform(click())
        onView(allOf(withId(R.id.code), withText(containsString("CA"))))
            .check(matches(isDisplayed()))
        onView(allOf(withId(R.id.name), withText(containsString("Changhua"))))
            .check(matches(isDisplayed()))
    }
}
