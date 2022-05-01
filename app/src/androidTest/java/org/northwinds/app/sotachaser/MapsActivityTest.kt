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

import android.content.Context
import android.content.Intent
import android.widget.Spinner
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.uiautomator.*
import org.hamcrest.Matchers
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
//@HiltAndroidTest
class MapsActivityTest {
    @get:Rule
    val rule = ActivityScenarioRule(MapsActivity::class.java)

    @Test
    fun load_map_viewmodel() {
        rule.scenario.onActivity {
            val associations = it.model.associations.value
            assertNotNull("No associations found", associations)
            assertEquals(
                "Incorrect number of associations",
                194, associations!!.count()
            )
            val associationIndex = associations.indexOf("W7O")
            assertNotNull("Can't find W7O association", associationIndex)
            it.model.set_association(associationIndex)
            assertEquals(
                "Incorrect number of regions for association",
                10, it.model.regions.value!!.count()
            )
            val regionIndex = it.model.regions.value!!.indexOf("WV")
            assertNotNull("Can't find WV region", regionIndex)
            it.model.set_region(regionIndex)
            assertEquals(
                "Incorrect number of summits for region",
                138, it.model.summits.value!!.count()
            )
            val associationIndex2 = associations.indexOf("W7W")
            assertNotNull("Can't find W7W association", associationIndex2)
            it.model.set_association(associationIndex2)
            assertEquals(
                "Incorrect number of regions for association",
                17, it.model.regions.value!!.count()
            )
            val regionIndex2 = it.model.regions.value!!.indexOf("LC")
            assertNotNull("Can't find LC region", regionIndex2)
            it.model.set_region(regionIndex2)
            assertEquals(
                "Incorrect number of summits for region",
                169, it.model.summits.value!!.count()
            )
        }
    }

    @Test
    fun load_map_activity() {
        onView(withId(R.id.association)).check(matches(isDisplayed()))
        onView(withId(R.id.association)).check { view, noViewException ->
            if (view == null)
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
            if (view == null)
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
            if (view == null)
                throw noViewException
            assertEquals(17, (view as Spinner).count)
        }
    }
}

private const val PACKAGE = "org.northwinds.app.sotachaser"
private const val LAUNCH_TIMEOUT = 5000L

@RunWith(AndroidJUnit4::class)
//@HiltAndroidTest
//@SdkSuppress(minSdkVersion = 18)
class MapsActivityUiTest {
    private val device: UiDevice = UiDevice.getInstance(getInstrumentation())

    @Before
    fun load_map_location() {
        device.pressHome()

        //val appAppsButton: UiObject = device.findObject(UiSelector().description("Apps"))
        //appAppsButton.clickAndWaitForNewWindow()

        val context = ApplicationProvider.getApplicationContext<Context>()
        val intent = context.packageManager.getLaunchIntentForPackage(
            PACKAGE)?.apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
        context.startActivity(intent)

        device.wait(
            Until.hasObject(By.pkg(PACKAGE).depth(0)),
            LAUNCH_TIMEOUT
        )
    }

    @Test
    fun has_location() {
        val expectedSummits = listOf(
            "W7O/CN-001",
            "W7O/CN-014",
            "W7O/CN-039",
            "W7O/CN-052",
            "W7O/CN-077",
        )
        expectedSummits.forEach {
            val marker = device.findObject(UiSelector().descriptionContains(it))
            assertFalse("Marker for summit $it should not be present", marker.exists())
        }
        val associationSpinner = device.findObject(
            UiSelector().descriptionContains("Association")
                .className("android.widget.Spinner")
        )
        val regionSpinner = device.findObject(
            UiSelector().descriptionContains("Region")
                .className("android.widget.Spinner")
        )
        assertTrue(associationSpinner.exists() && associationSpinner.isEnabled)
        associationSpinner.click()
        val selection = UiScrollable(UiSelector().className("android.widget.ListView"))
        val association: UiObject = selection.getChildByText(UiSelector().text("W7O"), "W7O")
        association.click()
        assertTrue(regionSpinner.exists() && regionSpinner.isEnabled)
//        Thread.sleep(10000)
        assertEquals("Wrong region visible", "CC", regionSpinner.getChild(UiSelector().className("android.widget.TextView")).text)
        regionSpinner.click()
        val selection2 = UiScrollable(UiSelector().className("android.widget.ListView"))
        selection2.getChildByText(UiSelector().text("CN"), "CN").click()
        //val marker = device.findObject(UiSelector().descriptionContains("Sydney"))
        //assertTrue("Can't find marker", marker.exists())
        //marker.click()
        //Thread.sleep(10000)
        expectedSummits.forEach {
            val marker1 = device.findObject(UiSelector().descriptionContains(it))
            assertTrue("Marker for summit $it should be present", marker1.exists())
        }
    }

    @Test
    fun will_move_locations() {
        val expectedSummits = listOf(
            "HL/JN-001",
            "HL/JN-039",
            "HL/JN-211",
            "HL/JN-319",
            "HL/JN-346",
        )
        expectedSummits.forEach {
            val marker = device.findObject(UiSelector().descriptionContains(it))
            assertFalse("Marker for summit $it should not be present", marker.exists())
        }
        val associationSpinner = device.findObject(
            UiSelector().descriptionContains("Association")
                .className("android.widget.Spinner")
        )
        val regionSpinner = device.findObject(
            UiSelector().descriptionContains("Region")
                .className("android.widget.Spinner")
        )
        val selection = UiScrollable(UiSelector().className("android.widget.ListView"))

        associationSpinner.click()
        selection.getChildByText(UiSelector().text("W7O"), "W7O").click()
        regionSpinner.click()
        selection.getChildByText(UiSelector().text("CN"), "CN").click()

        assertTrue("Marker for summit W7O/CN-001 should be present",
            device.findObject(UiSelector().descriptionContains("W7O/CN-001")).exists())
        assertFalse("Marker for summit HL/JN-001 should NOT be present",
            device.findObject(UiSelector().descriptionContains("HL/JN-001")).exists())

        associationSpinner.click()
        selection.getChildByText(UiSelector().text("HL"), "HL").click()
        regionSpinner.click()
        selection.getChildByText(UiSelector().text("JN"), "JN").click()

        expectedSummits.forEach {
            val marker1 = device.findObject(UiSelector().descriptionContains(it))
            assertTrue("Marker for summit $it should be present", marker1.exists())
        }
    }
}
