package org.northwinds.app.sotachaser.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import androidx.core.content.edit
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import androidx.test.uiautomator.*
import com.github.flank.utility.screenshot.UiScreenshotTestRule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.aprsdroid.app.testing.SharedPreferencesRule
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.northwinds.app.sotachaser.BuildConfig
import org.northwinds.app.sotachaser.R

private const val LAUNCH_TIMEOUT = 5000L

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class SummitDetailsFragmentUiTest {
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

    private val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

    private val associationSpinner = device.findObject(
        UiSelector().descriptionContains("Association")
            .className("android.widget.Spinner")
    )

    private val regionSpinner = device.findObject(
        UiSelector().descriptionContains("Region")
            .className("android.widget.Spinner")
    )

    private val selection = UiScrollable(UiSelector().className("android.widget.ListView"))
    private val recycler = UiScrollable(UiSelector().className("androidx.recyclerview.widget.RecyclerView"))

    @Before
    fun loadSummitDetails() {
        device.pressHome()

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
    fun canOpenTopSummitDetails() {
        device.findObject(UiSelector().descriptionContains("Summit List")).click()
        associationSpinner.click()
        selection.getChildByText(UiSelector().text("W7W"), "W7W").click()
        regionSpinner.click()
        selection.getChildByText(UiSelector().text("LC"), "LC").click()
        device.wait(Until.hasObject(By.descContains("W7W/LC")),
            LAUNCH_TIMEOUT
        )
        device.findObject(UiSelector().descriptionContains("W7W/LC-001")).click()
        assertTrue("Has map marker", device.findObject(UiSelector().descriptionContains("Google Map").childSelector(UiSelector().descriptionContains("W7W/LC-001"))).exists())
    }

    @Test
    fun canOpenBottomSummitDetails() {
        device.findObject(UiSelector().descriptionContains("Summit List")).click()
        associationSpinner.click()
        selection.getChildByText(UiSelector().text("W7W"), "W7W").click()
        regionSpinner.click()
        selection.getChildByText(UiSelector().text("LC"), "LC").click()
        device.wait(Until.hasObject(By.descContains("W7W/LC")),
            LAUNCH_TIMEOUT
        )
        //recycler.getChildByText(UiSelector().text("W7W/LC-"), "W7W/LC-169").click()
        recycler.maxSearchSwipes = recycler.maxSearchSwipes*2
        recycler.scrollIntoView(UiSelector().text("W7W/LC-169"))

        device.findObject(UiSelector().descriptionContains("W7W/LC-169")).click()
        assertTrue("Has map marker", device.findObject(UiSelector().descriptionContains("Google Map").childSelector(UiSelector().descriptionContains("W7W/LC-169"))).exists())
    }
}
