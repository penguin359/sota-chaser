package org.northwinds.app.sotachaser.ui

import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.DataInteraction
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.dannyroa.espresso_samples.recyclerview.RecyclerViewMatcher
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import junit.framework.Assert.assertEquals
import org.hamcrest.Matchers.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.northwinds.app.sotachaser.R
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
        onView(withId(R.id.association)).check(matches(
            withSpinnerText(containsString("W7O"))
        ))
        onView(withId(R.id.region)).perform(click())
        onData(
            allOf(`is`(instanceOf(String::class.java)), `is`(equalTo("NC")))
        ).perform(click())
        onView(withId(R.id.region)).check(matches(
            withSpinnerText(containsString("NC"))
        ))
        onView(withId(R.id.list)).check { view, noMatchingViewException ->
            if(view == null)
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
        onView(withId(R.id.association)).check(matches(
            withSpinnerText(containsString("W7O"))
        ))
        onView(withId(R.id.region)).perform(click())
        onData(
            allOf(`is`(instanceOf(String::class.java)), `is`(equalTo("NC")))
        ).perform(click())
        onView(withId(R.id.region)).check(matches(
            withSpinnerText(containsString("NC"))
        ))
        onView(withId(R.id.list)).check { view, noMatchingViewException ->
            if(view == null)
                throw noMatchingViewException
            val recyclerView = view as RecyclerView
            assertEquals(127, recyclerView.adapter?.itemCount)
        }

        onView(withId(R.id.association)).perform(click())
        onData(
            allOf(`is`(instanceOf(String::class.java)), `is`(equalTo("BV")))
        ).perform(click())
        onView(withId(R.id.association)).check(matches(
            withSpinnerText(containsString("BV"))
        ))
        onView(withId(R.id.region)).perform(click())
        onData(
            allOf(`is`(instanceOf(String::class.java)), `is`(equalTo("HL")))
        ).perform(click())
        onView(withId(R.id.region)).check(matches(
            withSpinnerText(containsString("HL"))
        ))
        onView(withId(R.id.list)).check { view, noMatchingViewException ->
            if(view == null)
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
        onView(RecyclerViewMatcher(R.id.list).atPosition(0)).check(matches(hasDescendant(withText(containsString("지리산")))))
        onView(withId(R.id.list)).check(matches(atPosition(0, hasDescendant(allOf(withId(R.id.points), withText("10"))))))
        onView(RecyclerViewMatcher(R.id.list).atPositionOnView(0, R.id.points)).check(matches(withText("10")))
        onView(withId(R.id.list)).check(matches(atPosition(0, hasDescendant(allOf(withId(R.id.altitude), withText("6284"))))))
        onView(RecyclerViewMatcher(R.id.list).atPositionOnView(0, R.id.altitude)).check(matches(withText("6284")))
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
            scrollToPosition<MySummitRecyclerViewAdapter.ViewHolder>(summitPosition))
        onView(RecyclerViewMatcher(R.id.list).atPosition(summitPosition)).check(matches(hasDescendant(withText(containsString("뒷삐알산")))))
        onView(RecyclerViewMatcher(R.id.list).atPositionOnView(summitPosition, R.id.summit_id)).check(matches(withText(containsString("046"))))
        onView(RecyclerViewMatcher(R.id.list).atPositionOnView(summitPosition, R.id.name)).check(matches(withText(containsString("뒷삐알산"))))
        onView(RecyclerViewMatcher(R.id.list).atPositionOnView(summitPosition, R.id.points)).check(matches(withText("6")))
        onView(RecyclerViewMatcher(R.id.list).atPositionOnView(summitPosition, R.id.altitude)).check(matches(withText("2713")))
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
