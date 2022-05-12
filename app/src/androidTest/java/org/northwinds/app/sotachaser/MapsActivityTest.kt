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

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Spinner
import androidx.core.content.edit
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.uiautomator.*
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.aprsdroid.app.testing.SharedPreferencesRule
import org.hamcrest.Matchers
import org.hamcrest.Matchers.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.northwinds.app.sotachaser.ui.MapsActivity

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class MapsActivityTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    private val context = ApplicationProvider.getApplicationContext<Context>()

    @get:Rule(order = 1)
    val prefsRule = SharedPreferencesRule() {
        it.edit() {
            putBoolean(context.getString(R.string.preference_asked_for_consent), true)
            putInt(context.getString(R.string.preference_changelog), BuildConfig.VERSION_CODE)
        }
    }

    @get:Rule(order = 2)
    val rule = ActivityScenarioRule(MapsActivity::class.java)

    @Test
    fun load_map_viewmodel() {
        Espresso.onIdle()
        rule.scenario.onActivity {
            val frag = it.supportFragmentManager.fragments[0] as MapsFragment
            val associations = frag.model.associations.value
            assertNotNull("No associations found", associations)
            assertEquals(
                "Incorrect number of associations",
                194, associations!!.count()
            )
            val associationIndex = associations.indexOf("W7O")
            assertNotNull("Can't find W7O association", associationIndex)
            frag.model.setAssociation(associationIndex)
        }
        Espresso.onIdle()
        rule.scenario.onActivity {
            val frag = it.supportFragmentManager.fragments[0] as MapsFragment
            assertEquals(
                "Incorrect number of regions for association",
                10, frag.model.regions.value!!.count()
            )
            val regionIndex = frag.model.regions.value!!.indexOf("WV")
            assertNotNull("Can't find WV region", regionIndex)
            frag.model.setRegion(regionIndex)
        }
        Espresso.onIdle()
        rule.scenario.onActivity {
            val frag = it.supportFragmentManager.fragments[0] as MapsFragment
            assertEquals(
                "Incorrect number of summits for region",
                138, frag.model.summits.value!!.count()
            )
            val associationIndex2 = frag.model.associations.value!!.indexOf("W7W")
            assertNotNull("Can't find W7W association", associationIndex2)
            frag.model.setAssociation(associationIndex2)
        }
        Espresso.onIdle()
        rule.scenario.onActivity {
            val frag = it.supportFragmentManager.fragments[0] as MapsFragment
            assertEquals(
                "Incorrect number of regions for association",
                17, frag.model.regions.value!!.count()
            )
            val regionIndex2 = frag.model.regions.value!!.indexOf("LC")
            assertNotNull("Can't find LC region", regionIndex2)
            frag.model.setRegion(regionIndex2)
        }
        Espresso.onIdle()
        rule.scenario.onActivity {
            val frag = it.supportFragmentManager.fragments[0] as MapsFragment
            assertEquals(
                "Incorrect number of summits for region",
                169, frag.model.summits.value!!.count()
            )
        }
    }

    @Test
    fun update_map_viewmodel() {
        Espresso.onIdle()
        rule.scenario.onActivity {
            val frag = it.supportFragmentManager.fragments[0] as MapsFragment
            frag.model.setAssociation(0)
        }
        Espresso.onIdle()
        rule.scenario.onActivity {
            val frag = it.supportFragmentManager.fragments[0] as MapsFragment
            frag.model.setRegion(0)
        }
        Espresso.onIdle()
        rule.scenario.onActivity {
            val frag = it.supportFragmentManager.fragments[0] as MapsFragment
            frag.model.setAssociation(1)
        }
        Espresso.onIdle()
        rule.scenario.onActivity {
            val frag = it.supportFragmentManager.fragments[0] as MapsFragment
            frag.model.setRegion(0)
        }
            //val associations = it.model.associations.value
            //assertNotNull("No associations found", associations)
            //assertEquals(
            //    "Incorrect number of associations",
            //    194, associations!!.count()
            //)
            //val associationIndex = associations.indexOf("W7O")
            //assertNotNull("Can't find W7O association", associationIndex)
            //it.model.set_association(associationIndex)
            //assertEquals(
            //    "Incorrect number of regions for association",
            //    10, it.model.regions.value!!.count()
            //)
            //val regionIndex = it.model.regions.value!!.indexOf("WV")
            //assertNotNull("Can't find WV region", regionIndex)
            //it.model.set_region(regionIndex)
            //assertEquals(
            //    "Incorrect number of summits for region",
            //    138, it.model.summits.value!!.count()
            //)
            //val associationIndex2 = associations.indexOf("W7W")
            //assertNotNull("Can't find W7W association", associationIndex2)
            //it.model.set_association(associationIndex2)
            //assertEquals(
            //    "Incorrect number of regions for association",
            //    17, it.model.regions.value!!.count()
            //)
            //val regionIndex2 = it.model.regions.value!!.indexOf("LC")
            //assertNotNull("Can't find LC region", regionIndex2)
            //it.model.set_region(regionIndex2)
            //assertEquals(
            //    "Incorrect number of summits for region",
            //    169, it.model.summits.value!!.count()
            //)
        //}
    }

    @Test
    fun load_map_activity() {
        Espresso.onIdle()
        rule.scenario.onActivity {
            val frag = it.supportFragmentManager.fragments[0] as MapsFragment
            frag.binding.association.setSelection(0)
        }
        Espresso.onIdle()
        rule.scenario.onActivity {
            val frag = it.supportFragmentManager.fragments[0] as MapsFragment
            frag.binding.region.setSelection(0)
        }
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

    @Test
    fun will_preserve_last_region() {
        onView(withId(R.id.association)).perform(ViewActions.click())
        Espresso.onData(
            Matchers.allOf(
                Matchers.`is`(Matchers.instanceOf(String::class.java)),
                Matchers.`is`("W7O")
            )
        ).perform(ViewActions.click())
        onView(withId(R.id.association)).check(matches(withSpinnerText(Matchers.containsString("W7O"))))
        onView(withId(R.id.region)).perform(ViewActions.click())
        Espresso.onData(
            Matchers.allOf(
                Matchers.`is`(Matchers.instanceOf(String::class.java)),
                Matchers.`is`("WV")
            )
        ).perform(ViewActions.click())
        onView(withId(R.id.region)).check(matches(withSpinnerText(Matchers.containsString("WV"))))
        rule.scenario.recreate()
        onView(withId(R.id.association)).check(matches(withSpinnerText(Matchers.containsString("W7O"))))
        onView(withId(R.id.region)).check(matches(withSpinnerText(Matchers.containsString("WV"))))
    }

    @Test
    fun willSendFeedback() {
        Intents.init()
        Espresso.openActionBarOverflowOrOptionsMenu(context)

        val appCompatTextView = onView(
            Matchers.allOf(
                withText("Feedback"),
                isDisplayed()
            )
        )
        appCompatTextView.perform(ViewActions.click())

        Log.d(TAG, "All Intents: ${Intents.getIntents().toString()}")
        Intents.intended(allOf(
            hasAction(Intent.ACTION_CHOOSER),
            hasExtra(`is`(Intent.EXTRA_INTENT), allOf(
                hasAction(Intent.ACTION_SENDTO),
                //hasType("text/plain"),
                hasExtra(Intent.EXTRA_EMAIL, arrayOf("penguin359@gmail.com")),
                hasExtra(`is`(Intent.EXTRA_SUBJECT), allOf(
                    containsStringIgnoringCase("feedback"),
                    containsStringIgnoringCase("SOTA Chaser"),
                )),
                hasExtra(`is`(Intent.EXTRA_TEXT), allOf(
                    containsStringIgnoringCase("Version")
                )),
            )),
        ))
        Intents.release()
    }

    companion object {
        private const val TAG = "SOTAChaser-MapsTest"
    }
}

//private const val PACKAGE = "org.northwinds.app.sotachaser"
private const val LAUNCH_TIMEOUT = 5000L

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
//@SdkSuppress(minSdkVersion = 18)
class MapsActivityUiTest {
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    private val context = ApplicationProvider.getApplicationContext<Context>()
    private val `package` = InstrumentationRegistry.getInstrumentation().targetContext.packageName

    @get:Rule
    val prefsRule = SharedPreferencesRule() {
        it.edit() {
            putBoolean(context.getString(R.string.preference_asked_for_consent), true)
            putInt(context.getString(R.string.preference_changelog), BuildConfig.VERSION_CODE)
        }
    }

    private val device = UiDevice.getInstance(getInstrumentation())

    private val associationSpinner = device.findObject(
        UiSelector().descriptionContains("Association")
            .className("android.widget.Spinner")
    )

    private val regionSpinner = device.findObject(
        UiSelector().descriptionContains("Region")
            .className("android.widget.Spinner")
    )

    private val selection = UiScrollable(UiSelector().className("android.widget.ListView"))

    @Before
    fun load_map_location() {
        device.pressHome()

        //val appAppsButton: UiObject = device.findObject(UiSelector().description("Apps"))
        //appAppsButton.clickAndWaitForNewWindow()

        val intent = context.packageManager.getLaunchIntentForPackage(
            `package`)?.apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
        context.startActivity(intent)

        device.wait(
            Until.hasObject(By.pkg(`package`).depth(0)),
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
        assertTrue(associationSpinner.exists() && associationSpinner.isEnabled)
        associationSpinner.click()
        device.wait(Until.findObject(By.text("W7O")), LAUNCH_TIMEOUT)
        val association: UiObject = selection.getChildByText(UiSelector().text("W7O"), "W7O")
        association.click()
        assertTrue(regionSpinner.exists() && regionSpinner.isEnabled)
//        Thread.sleep(10000)
        assertEquals("Wrong region visible", "CC", regionSpinner.getChild(UiSelector().className("android.widget.TextView")).text)
        regionSpinner.click()
        selection.getChildByText(UiSelector().text("CN"), "CN").click()
        //val marker = device.findObject(UiSelector().descriptionContains("Sydney"))
        //assertTrue("Can't find marker", marker.exists())
        //marker.click()
        //Thread.sleep(10000)
        //Espresso.onIdle()
        device.wait(Until.hasObject(By.descContains("/CN")), LAUNCH_TIMEOUT)
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

        associationSpinner.click()
        selection.getChildByText(UiSelector().text("W7O"), "W7O").click()
        regionSpinner.click()
        selection.getChildByText(UiSelector().text("CN"), "CN").click()
        device.wait(Until.hasObject(By.descContains("CN")), LAUNCH_TIMEOUT)
        //Thread.sleep(1000)

        assertTrue("Marker for summit W7O/CN-001 should be present",
            device.findObject(UiSelector().descriptionContains("W7O/CN-001")).exists())
        assertFalse("Marker for summit HL/JN-001 should NOT be present",
            device.findObject(UiSelector().descriptionContains("HL/JN-001")).exists())

        associationSpinner.click()
        selection.getChildByText(UiSelector().text("HL"), "HL").click()
        regionSpinner.click()
        selection.getChildByText(UiSelector().text("JN"), "JN").click()
        device.wait(Until.hasObject(By.descContains("JN")), LAUNCH_TIMEOUT)
        //Thread.sleep(1000)

        expectedSummits.forEach {
            val marker1 = device.findObject(UiSelector().descriptionContains(it))
            assertTrue("Marker for summit $it should be present", marker1.exists())
        }
    }

    @Test
    fun previous_locations_are_cleared() {
        associationSpinner.click()
        selection.getChildByText(UiSelector().text("W7O"), "W7O").click()
        regionSpinner.click()
        selection.getChildByText(UiSelector().text("NC"), "NC").click()
        device.wait(Until.hasObject(By.descContains("NC")), LAUNCH_TIMEOUT)
        //Thread.sleep(1000)

        assertTrue("Marker for summit W7O/NC-042 should be present",
            device.findObject(UiSelector().descriptionContains("W7O/NC-042")).exists())
        assertFalse("Marker for summit W7W/LC-001 should NOT be present",
            device.findObject(UiSelector().descriptionContains("W7W/LC-001")).exists())

        associationSpinner.click()
        selection.getChildByText(UiSelector().text("W7W"), "W7W").click()
        regionSpinner.click()
        selection.getChildByText(UiSelector().text("LC"), "LC").click()
        device.wait(Until.hasObject(By.descContains("LC")), LAUNCH_TIMEOUT)
        //Thread.sleep(1000)

        assertTrue("Marker for summit W7W/LC-001 should be present",
            device.findObject(UiSelector().descriptionContains("W7W/LC-001")).exists())
        assertFalse("Marker for summit W7O/NC-042 should NOT be present",
            device.findObject(UiSelector().descriptionContains("W7O/NC-042")).exists())
    }

    @Test
    fun will_preserve_last_region() {
        associationSpinner.click()
        selection.getChildByText(UiSelector().text("W7O"), "W7O").click()
        regionSpinner.click()
        selection.getChildByText(UiSelector().text("WV"), "WV").click()
        device.wait(Until.hasObject(By.descContains("WV")), LAUNCH_TIMEOUT)
        associationSpinner.getChild(UiSelector().className(""))
        //assertThat(//"Association",
        //    associationSpinner.getChild(UiSelector().className("android.widget.TextView")).text,
        //    //equalTo("W7O"))
        //    containsString("D"))
        //assertThat("Region",
        //    regionSpinner.getChild(UiSelector().className("android.widget.TextView")).text,
        //    equalTo("WV"))
        assertEquals("Wrong association visible", "W7O", associationSpinner.getChild(UiSelector().className("android.widget.TextView")).text)
        assertEquals("Wrong region visible", "WV", regionSpinner.getChild(UiSelector().className("android.widget.TextView")).text)
        device.pressBack()
        device.pressBack()
        load_map_location()
        assertEquals("Wrong association visible", "W7O", associationSpinner.getChild(UiSelector().className("android.widget.TextView")).text)
        assertEquals("Wrong region visible", "WV", regionSpinner.getChild(UiSelector().className("android.widget.TextView")).text)
    }
}
