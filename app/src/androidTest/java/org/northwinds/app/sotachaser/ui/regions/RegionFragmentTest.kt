package org.northwinds.app.sotachaser.ui.regions

import android.Manifest
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
import org.junit.Assert.assertEquals
import org.aprsdroid.app.testing.SharedPreferencesRule
import org.hamcrest.Matchers
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.containsString
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
import org.northwinds.app.sotachaser.ui.associations.AssociationFragment
import org.northwinds.app.sotachaser.ui.associations.AssociationFragmentDirections
import org.northwinds.app.sotachaser.ui.associations.AssociationRecyclerViewAdapterVH
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class RegionFragmentTest {
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var dao: SummitDao

    @get:Rule
    val permissionRule: GrantPermissionRule = GrantPermissionRule.grant(Manifest.permission.ACCESS_COARSE_LOCATION)

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun loadRegion() {
        val frag = HiltFragmentScenario.launchInHiltContainer(RegionFragment::class.java,
            AssociationFragmentDirections.actionAssociationFragmentToRegionFragment("HL").arguments)
        onView(withId(R.id.list)).check(matches(isDisplayed()))
        frag.close()
    }

    @Test
    fun loadSpecificRegionList() {
        val frag = HiltFragmentScenario.launchInHiltContainer(RegionFragment::class.java,
            AssociationFragmentDirections.actionAssociationFragmentToRegionFragment("W7O").arguments)
        onView(withId(R.id.list)).check { view, noMatchingViewException ->
            if (view == null)
                throw noMatchingViewException
            val recyclerView = view as RecyclerView
            assertEquals(10, recyclerView.adapter?.itemCount)
        }
        frag.close()
    }

    @Test
    fun loadMultipleRegionLists() {
        var frag = HiltFragmentScenario.launchInHiltContainer(RegionFragment::class.java,
            AssociationFragmentDirections.actionAssociationFragmentToRegionFragment("W7O").arguments)
        onView(withId(R.id.list)).check { view, noMatchingViewException ->
            if (view == null)
                throw noMatchingViewException
            val recyclerView = view as RecyclerView
            assertEquals(10, recyclerView.adapter?.itemCount)
        }
        frag.close()

        frag = HiltFragmentScenario.launchInHiltContainer(RegionFragment::class.java,
            AssociationFragmentDirections.actionAssociationFragmentToRegionFragment("BV").arguments)
        onView(withId(R.id.list)).check { view, noMatchingViewException ->
            if (view == null)
                throw noMatchingViewException
            val recyclerView = view as RecyclerView
            assertEquals(18, recyclerView.adapter?.itemCount)
        }
        frag.close()
    }

    @Test
    fun loadRegionSummary() {
        val frag = HiltFragmentScenario.launchInHiltContainer(RegionFragment::class.java,
            AssociationFragmentDirections.actionAssociationFragmentToRegionFragment("W7O").arguments)
        onView(RecyclerViewMatcher(R.id.list).atPosition(0)).check(
            matches(
                hasDescendant(
                    withText(
                        containsString("CC")
                    )
                )
            )
        )
        onView(withId(R.id.list)).check(
            matches(
                atPosition(
                    0,
                    hasDescendant(allOf(withId(R.id.name), withText(containsString("OR-Central Coast"))))
                )
            )
        )
        frag.close()
    }

    @Test
    fun loadRegionSummaryAtBottomOfList() {
        // Position for region YU
        val summitPosition = 17  // Zero-indexed
        val frag = HiltFragmentScenario.launchInHiltContainer(RegionFragment::class.java,
            AssociationFragmentDirections.actionAssociationFragmentToRegionFragment("BV").arguments)
        onView(withId(R.id.list)).perform(
            scrollToPosition<RegionRecyclerViewAdapterVH>(summitPosition)
        )
        onView(RecyclerViewMatcher(R.id.list).atPosition(summitPosition)).check(
            matches(
                hasDescendant(withText(containsString("Huawei")))
            )
        )
        onView(
            RecyclerViewMatcher(R.id.list).atPositionOnView(
                summitPosition,
                R.id.code
            )
        ).check(matches(withText(containsString("YU"))))
        onView(RecyclerViewMatcher(R.id.list).atPositionOnView(summitPosition, R.id.name)).check(
            matches(withText(containsString("Yulin")))
        )
        onView(RecyclerViewMatcher(R.id.list).atPositionOnView(summitPosition, R.id.manager)).check(
            matches(withText(containsString("Huawei Su")))
        )
        onView(
            RecyclerViewMatcher(R.id.list).atPositionOnView(
                summitPosition,
                R.id.manager_callsign
            )
        ).check(matches(withText(containsString("BX2AI"))))
        onView(
            RecyclerViewMatcher(R.id.list).atPositionOnView(
                summitPosition,
                R.id.summit_count
            )
        ).check(matches(withText(containsString("3"))))
        frag.close()
    }

    @Test
    fun filterRegionList() {
        val frag = HiltFragmentScenario.launchInHiltContainer(RegionFragment::class.java,
            AssociationFragmentDirections.actionAssociationFragmentToRegionFragment("W7O").arguments)
        onView(withId(R.id.filter)).perform(ViewActions.replaceText("Cas"))
        onView(withId(R.id.list)).check { view, noMatchingViewException ->
            if (view == null)
                throw noMatchingViewException
            val recyclerView = view as RecyclerView
            assertEquals(3, recyclerView.adapter?.itemCount)
        }
        onView(withId(R.id.filter)).perform(ViewActions.replaceText("WV"))
        onView(withId(R.id.list)).check { view, noMatchingViewException ->
            if (view == null)
                throw noMatchingViewException
            val recyclerView = view as RecyclerView
            assertEquals(1, recyclerView.adapter?.itemCount)
        }
        onView(withId(R.id.filter)).perform(ViewActions.replaceText("WVZ"))
        onView(withId(R.id.list)).check { view, noMatchingViewException ->
            if (view == null)
                throw noMatchingViewException
            val recyclerView = view as RecyclerView
            assertEquals(0, recyclerView.adapter?.itemCount)
        }
        frag.close()
    }

    @Test
    fun loadRegionsAfterClear() {
        dao.clear()
        val frag = HiltFragmentScenario.launchInHiltContainer(RegionFragment::class.java,
            AssociationFragmentDirections.actionAssociationFragmentToRegionFragment("W7O").arguments)
        onView(withId(R.id.list)).check(matches(isDisplayed()))
        onView(withId(R.id.list)).check { view, noMatchingViewException ->
            if (view == null)
                throw noMatchingViewException
            val recyclerView = view as RecyclerView
            assertThat(recyclerView.adapter!!.itemCount,
                Matchers.`is`(Matchers.greaterThanOrEqualTo(10))
            )
        }
        frag.close()
    }
}

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class RegionActivityTest {
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
    fun loadSummitFragment() {
        onView(withId(R.id.associationFragment)).perform(click())
        onView(withText(containsString("3Y"))).perform(click())
        onView(withText(containsString("BV"))).perform(click())
        onView(withId(R.id.name)).check(matches(allOf(isDisplayed(), withText(containsString("Olavtoppen")))))
    }

    @Test
    fun loadCorrectRegionDetailsFragment() {
        onView(withId(R.id.associationFragment)).perform(click())
        onView(withText(containsString("3Y"))).perform(click())
        onView(withText(containsString("BV"))).perform(click())
        onView(allOf(withId(R.id.code), withText(containsString("001"))))
            .check(matches(isDisplayed()))
        onView(allOf(withId(R.id.name), withText(containsString("Olavtoppen"))))
            .check(matches(isDisplayed()))
    }

    @Test
    fun loadDifferentRegionDetailsFragment() {
        // Position for association W7O
        val summitPosition = 169  // Zero-indexed
        onView(withId(R.id.associationFragment)).perform(click())
        onView(withId(R.id.list)).perform(
            scrollToPosition<AssociationRecyclerViewAdapterVH>(summitPosition)
        )
        onView(withText(containsString("W7O"))).perform(click())
        onView(withText(containsString("CN"))).perform(click())
        onView(allOf(withId(R.id.code), withText(containsString("003"))))
            .check(matches(isDisplayed()))
        onView(allOf(withId(R.id.name), withText(containsString("Mount Hood"))))
            .check(matches(isDisplayed()))
    }
}
