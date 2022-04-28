/*
 * Copyright (c) 2022 Loren M. Lang
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.northwinds.app.sotachaser

import android.widget.Spinner
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.Matchers
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
//@HiltAndroidTest
class MapsActivityTest {
    @get:Rule val rule = ActivityScenarioRule(MapsActivity::class.java)

    @Test
    fun load_map_viewmodel() {
        rule.scenario.onActivity {
            assertEquals("Incorrect number of associations", 194, it.model.associations.value!!.count())
        }
    }

    @Test
    fun load_map_activity() {
        onView(withId(R.id.association)).check(matches(isDisplayed()))
        onView(withId(R.id.association)).check { view, noViewException ->
            if(view == null)
                throw noViewException
            assertEquals(194, (view as Spinner).count)
        }
        onView(withId(R.id.association)).check(matches(withSpinnerText(Matchers.containsString("3Y"))))
        onView(withId(R.id.association)).perform(ViewActions.click())
        Espresso.onData(
            Matchers.allOf(
                Matchers.`is`(Matchers.instanceOf(String::class.java)),
                Matchers.`is`("W7O")
            )
        ).perform(ViewActions.click())
        onView(withId(R.id.association)).check(matches(withSpinnerText(Matchers.containsString("W7O"))))
        onView(withId(R.id.region)).check(matches(withSpinnerText(Matchers.containsString("CC"))))
        onView(withId(R.id.region)).check { view, noViewException ->
            if(view == null)
                throw noViewException
            assertEquals(10, (view as Spinner).count)
        }
        onView(withId(R.id.association)).perform(ViewActions.click())
        Espresso.onData(
            Matchers.allOf(
                Matchers.`is`(Matchers.instanceOf(String::class.java)),
                Matchers.`is`("W7W")
            )
        ).perform(ViewActions.click())
        onView(withId(R.id.association)).check(matches(withSpinnerText(Matchers.containsString("W7W"))))
        onView(withId(R.id.region)).check(matches(withSpinnerText(Matchers.containsString("CH"))))
        onView(withId(R.id.region)).check { view, noViewException ->
            if(view == null)
                throw noViewException
            assertEquals(17, (view as Spinner).count)
        }
    }
}