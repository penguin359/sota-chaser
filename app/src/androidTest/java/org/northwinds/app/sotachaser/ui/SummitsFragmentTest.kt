package org.northwinds.app.sotachaser.ui

import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import junit.framework.Assert.assertEquals
import org.hamcrest.Matchers.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.northwinds.app.sotachaser.R
import org.northwinds.app.sotachaser.testing.HiltFragmentScenario
import org.northwinds.app.sotachaser.testing.SummitFragment

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
}
