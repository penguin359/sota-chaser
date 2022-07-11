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

import android.Manifest
import android.app.Activity
import android.app.Application
import android.app.Instrumentation
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Spinner
import androidx.annotation.VisibleForTesting
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.core.content.edit
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.rule.GrantPermissionRule
import androidx.test.uiautomator.*
import com.github.flank.utility.screenshot.UiScreenshotTestRule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.aprsdroid.app.testing.SharedPreferencesRule
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.northwinds.app.sotachaser.repository.SummitsRepository
import org.northwinds.app.sotachaser.testing.HiltFragmentScenario
import org.northwinds.app.sotachaser.ui.MainActivity
import org.northwinds.app.sotachaser.ui.MapsViewModel
import org.northwinds.app.sotachaser.ui.home.MapsFragment
import java.util.concurrent.CountDownLatch
import java.util.concurrent.ExecutorService
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class MainActivityTest {
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
    val rule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun willSendFeedback() {
        Intents.init()
        intending(hasAction(Intent.ACTION_CHOOSER)).respondWith(
            Instrumentation.ActivityResult(
                Activity.RESULT_OK,
                Intent()
            )
        )
        Espresso.openActionBarOverflowOrOptionsMenu(context)

        val appCompatTextView = onView(
            allOf(
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
                    containsStringIgnoringCase("Version"),
                    containsString(BuildConfig.VERSION_NAME),
                )),
            )),
        ))
        Intents.release()
    }

    companion object {
        private const val TAG = "SOTAChaser-MapsTest"
    }
}

@VisibleForTesting(otherwise = VisibleForTesting.NONE)
fun <T> LiveData<T>.getOrAwaitValue(
    time: Long = 2,
    timeUnit: TimeUnit = TimeUnit.SECONDS,
    afterObserve: () -> Unit = {}
): T {
    var data: T? = null
    val latch = CountDownLatch(1)
    val observer = object : Observer<T> {
        override fun onChanged(o: T?) {
            data = o
            latch.countDown()
            this@getOrAwaitValue.removeObserver(this)
        }
    }
    this.observeForever(observer)
    try {
        afterObserve.invoke()
        if (!latch.await(time, timeUnit)) {
            throw TimeoutException("LiveData value was never set.")
        }
    } finally {
        this.removeObserver(observer)
    }
    @Suppress("UNCHECKED_CAST")
    return data as T
}

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class MapsModelViewTest {
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var executor: ExecutorService

    @Inject
    lateinit var repo: SummitsRepository

    private lateinit var model: MapsViewModel

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        hiltRule.inject()
        val app = ApplicationProvider.getApplicationContext<Application>()
        model = MapsViewModel(app, executor, repo)
    }

    @Test
    fun load_map_viewmodel() {
        val associations = model.associations.getOrAwaitValue()
        assertEquals(
            "Incorrect number of associations",
            194, associations.count()
        )
        val associationIndex = associations.indexOfFirst { it.code == "W7O" }
        assertThat("Can't find W7O association", associationIndex, greaterThanOrEqualTo(0))
        model.setAssociation(associationIndex)

        assertEquals(
            "Incorrect number of regions for association",
            10, model.regions.getOrAwaitValue().count()
        )
        val regionIndex = model.regions.getOrAwaitValue().indexOfFirst { it.code == "WV" }
        assertThat("Can't find WV region", regionIndex, greaterThanOrEqualTo(0))
        model.setRegion(regionIndex)

        assertEquals(
            "Incorrect number of summits for region",
            138, model.summits.getOrAwaitValue().count()
        )
        val associationIndex2 = model.associations.getOrAwaitValue().indexOfFirst { it.code == "W7W" }
        assertThat("Can't find W7W association", associationIndex2, greaterThanOrEqualTo(0))
        model.setAssociation(associationIndex2)

        model.regions.getOrAwaitValue()
        Espresso.onIdle()
        assertEquals(
            "Incorrect number of regions for association",
            17, model.regions.getOrAwaitValue().count()
        )
        val regionIndex2 = model.regions.getOrAwaitValue().indexOfFirst { it.code == "LC" }
        assertThat("Can't find LC region", regionIndex2, greaterThanOrEqualTo(0))
        model.setRegion(regionIndex2)
    }

    @Test
    fun update_map_viewmodel() {
        // TODO This can be called before data is loaded
        model.associations.getOrAwaitValue()
        model.setAssociation(0)
        model.regions.getOrAwaitValue()
        model.setRegion(0)
        model.setAssociation(1)
        model.regions.getOrAwaitValue()
        model.setRegion(0)

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
}


@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class MapsFragmentTest {
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    private lateinit var frag: HiltFragmentScenario<MapsFragment>

    @Before
    fun setup() {
        frag = HiltFragmentScenario.launchInHiltContainer(MapsFragment::class.java)
    }

    @Test
    fun load_map_activity() {
        Espresso.onIdle()
        frag.onFragment {
            it.binding.association.setSelection(0)
        }
        Espresso.onIdle()
        frag.onFragment {
            it.binding.region.setSelection(0)
        }
        onView(withId(R.id.association)).check(matches(isDisplayed()))
        onView(withId(R.id.association)).check { view, noViewException ->
            if (view == null)
                throw noViewException
            assertEquals(194, (view as Spinner).count)
        }
        onView(withId(R.id.association)).check(matches(withSpinnerText(containsString("3Y"))))
        onView(withId(R.id.association)).perform(ViewActions.click())
        Espresso.onData(
            allOf(`is`(instanceOf(Map::class.java)), hasEntry("code", "W7O"))
        ).perform(ViewActions.click())
        onView(withId(R.id.association)).check(matches(withSpinnerText(containsString("W7O"))))
        onView(withId(R.id.region)).check(matches(withSpinnerText(containsString("CC"))))
        onView(withId(R.id.region)).check { view, noViewException ->
            if (view == null)
                throw noViewException
            assertEquals(10, (view as Spinner).count)
        }
        onView(withId(R.id.association)).perform(ViewActions.click())
        Espresso.onData(
            allOf(`is`(instanceOf(Map::class.java)), hasEntry("code", "W7W"))
        ).perform(ViewActions.click())
        onView(withId(R.id.association)).check(matches(withSpinnerText(containsString("W7W"))))
        onView(withId(R.id.region)).check(matches(withSpinnerText(containsString("CH"))))
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
            allOf(`is`(instanceOf(Map::class.java)), hasEntry("code", "W7O"))
        ).perform(ViewActions.click())
        onView(withId(R.id.association)).check(matches(withSpinnerText(containsString("W7O"))))
        onView(withId(R.id.region)).perform(ViewActions.click())
        Espresso.onData(
            allOf(`is`(instanceOf(Map::class.java)), hasEntry("code", "WV"))
        ).perform(ViewActions.click())
        onView(withId(R.id.region)).check(matches(withSpinnerText(containsString("WV"))))
        frag.recreate()
        onView(withId(R.id.association)).check(matches(withSpinnerText(containsString("W7O"))))
        onView(withId(R.id.region)).check(matches(withSpinnerText(containsString("WV"))))
    }

    companion object {
        private const val TAG = "SOTAChaser-MapsFragTest"
    }
}

//private const val PACKAGE = "org.northwinds.app.sotachaser"
private const val LAUNCH_TIMEOUT = 5000L

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
//@SdkSuppress(minSdkVersion = 18)
class MainActivityUiTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val writeStorageRule = GrantPermissionRule.grant(Manifest.permission.WRITE_EXTERNAL_STORAGE)

    @get:Rule(order = 2)
    val screenshotRule = UiScreenshotTestRule()

    private val context = ApplicationProvider.getApplicationContext<Context>()
    private val `package` = InstrumentationRegistry.getInstrumentation().targetContext.packageName

    @get:Rule
    val prefsRule = SharedPreferencesRule() {
        it.edit() {
            clear()  // Some tests rely on starting region to be default
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
        assertEquals("Wrong region visible", "CC", regionSpinner.getChild(UiSelector().className("android.widget.TextView")).text)
        regionSpinner.click()
        selection.getChildByText(UiSelector().text("CN"), "CN").click()
        device.wait(Until.hasObject(By.descContains("/CN-100")), LAUNCH_TIMEOUT)
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
        device.wait(Until.hasObject(By.descContains("CN-100")), LAUNCH_TIMEOUT)

        assertTrue("Marker for summit W7O/CN-001 should be present",
            device.findObject(UiSelector().descriptionContains("W7O/CN-001")).exists())
        assertFalse("Marker for summit HL/JN-001 should NOT be present",
            device.findObject(UiSelector().descriptionContains("HL/JN-001")).exists())

        associationSpinner.click()
        selection.getChildByText(UiSelector().text("HL"), "HL").click()
        regionSpinner.click()
        selection.getChildByText(UiSelector().text("JN"), "JN").click()
        device.wait(Until.hasObject(By.descContains("JN")), LAUNCH_TIMEOUT)

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
        device.wait(Until.hasObject(By.descContains("NC-100")), LAUNCH_TIMEOUT)

        assertTrue("Marker for summit W7O/NC-042 should be present",
            device.findObject(UiSelector().descriptionContains("W7O/NC-042")).exists())
        assertFalse("Marker for summit W7W/LC-001 should NOT be present",
            device.findObject(UiSelector().descriptionContains("W7W/LC-001")).exists())

        associationSpinner.click()
        selection.getChildByText(UiSelector().text("W7W"), "W7W").click()
        regionSpinner.click()
        selection.getChildByText(UiSelector().text("LC"), "LC").click()
        device.wait(Until.hasObject(By.descContains("LC")), LAUNCH_TIMEOUT)

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
