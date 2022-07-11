/**********************************************************************************
 * Copyright (c) 2020 Loren M. Lang                                               *
 *                                                                                *
 * Permission is hereby granted, free of charge, to any person obtaining a copy   *
 * of this software and associated documentation files (the "Software"), to deal  *
 * in the Software without restriction, including without limitation the rights   *
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell      *
 * copies of the Software, and to permit persons to whom the Software is          *
 * furnished to do so, subject to the following conditions:                       *
 *                                                                                *
 * The above copyright notice and this permission notice shall be included in all *
 * copies or substantial portions of the Software.                                *
 *                                                                                *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR     *
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,       *
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE    *
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER         *
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,  *
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE  *
 * SOFTWARE.                                                                      *
 **********************************************************************************/

package org.northwinds.app.sotachaser


import android.Manifest
import android.view.View
import android.view.ViewGroup
import androidx.core.content.edit
import androidx.lifecycle.Lifecycle
import androidx.preference.PreferenceManager
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.FlakyTest
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import androidx.test.uiautomator.UiDevice
import com.github.flank.utility.screenshot.UiScreenshotTestRule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.aprsdroid.app.testing.SharedPreferencesRule
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.*
import org.hamcrest.TypeSafeMatcher
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.northwinds.app.sotachaser.ui.MainActivity
import org.northwinds.app.sotachaser.ui.SettingsActivity

@LargeTest
@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class SettingsSimpleTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    var mActivityScenarioRule = ActivityScenarioRule(SettingsActivity::class.java)

    @get:Rule(order = 1)
    val writeStorageRule = GrantPermissionRule.grant(Manifest.permission.WRITE_EXTERNAL_STORAGE)

    @get:Rule(order = 2)
    val screenshotRule = UiScreenshotTestRule()

    @Test
    fun settingsBackButtonExitTest() {
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        device.pressBack()
        assertEquals(Lifecycle.State.DESTROYED, mActivityScenarioRule.scenario.state)
    }

    @Test
    @FlakyTest
    fun settingsActionBarExitTest() {
        val appCompatImageButton = onView(
            allOf(
                withContentDescription("Navigate up")/*,
                childAtPosition(
                    allOf(
                        withId(android.R.id.action_bar),
                        childAtPosition(
                            withId(android.R.id.action_bar_container),
                            0
                        )
                    ),
                    1
                )*/,
                isDisplayed()
            )
        )
        appCompatImageButton.perform(click())
        assertEquals(Lifecycle.State.DESTROYED, mActivityScenarioRule.scenario.state)
    }

    private fun childAtPosition(
        parentMatcher: Matcher<View>, position: Int,
    ): Matcher<View> {

        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("Child at position $position in parent ")
                parentMatcher.describeTo(description)
            }

            public override fun matchesSafely(view: View): Boolean {
                val parent = view.parent
                return parent is ViewGroup && parentMatcher.matches(parent)
                        && view == parent.getChildAt(position)
            }
        }
    }
}

@LargeTest
@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class SettingsOpenTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    private val context = ApplicationProvider.getApplicationContext<SotaChaserBaseApplication>()

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

    @Test
    fun settingsTest() {
        openActionBarOverflowOrOptionsMenu(context)

        val appCompatTextView = onView(
            allOf(
                withText("Settings"),
                isDisplayed()
            )
        )
        appCompatTextView.perform(click())

        onView(
            allOf(
                withClassName(containsString("TextView")),
                withText(R.string.title_anonymous_usage)
            )
        )
            .check(matches(allOf(isDisplayed(), not(isChecked()))))

        onView(
            allOf(
                withClassName(containsString("TextView")),
                withText(R.string.title_crash_reports)
            )
        )
            .check(matches(allOf(isDisplayed(), not(isChecked()))))
    }
}

@LargeTest
@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class SettingsTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    private val context = ApplicationProvider.getApplicationContext<SotaChaserBaseApplication>()
    private val prefs = PreferenceManager.getDefaultSharedPreferences(context)

    @get:Rule(order = 1)
    val prefsRule = SharedPreferencesRule() {
        it.edit() {
            clear()
        }
    }

    @get:Rule(order = 2)
    var mActivityScenarioRule = ActivityScenarioRule(SettingsActivity::class.java)

    @Test
    fun canEnableAnalyticsTest() {
        val prefEnableAnalytics = context.getString(R.string.preference_enable_analytics)

        assertThat(
            "Analytics enabled",
            context.analyticsEnabled,
            `is`(equalTo(false))
        )

        onView(allOf(
            withClassName(containsString("TextView")),
            withText(R.string.title_anonymous_usage)))
            .check(matches(not(isChecked())))
            .perform(click())
        assertThat(
            "Analytics pref enabled",
            prefs.getBoolean(prefEnableAnalytics, false),
            `is`(equalTo(true))
        )
        assertThat(
            "Analytics enabled",
            context.analyticsEnabled,
            `is`(equalTo(true))
        )
        assertThat(
            "Crash Reports enabled",
            context.crashReportsEnabled,
            `is`(equalTo(false))
        )
    }

    @Test
    fun showsEnabledAnalyticsTest() {
        val prefEnableAnalytics = context.getString(R.string.preference_enable_analytics)
        onView(allOf(
            withParent(hasSibling(
                withChild(allOf(
                    withClassName(containsString("TextView")),
                    withText(R.string.title_anonymous_usage))))), withClassName(containsString("Switch"))
        ))
            .check(matches(not(isChecked())))
        prefs.edit() { putBoolean(prefEnableAnalytics, true) }
        Espresso.onIdle()

        assertThat(
            "Analytics enabled",
            context.analyticsEnabled,
            `is`(equalTo(true))
        )
        mActivityScenarioRule.scenario.recreate()

        onView(allOf(
            withParent(hasSibling(
            withChild(allOf(
            withClassName(containsString("TextView")),
            withText(R.string.title_anonymous_usage))))), withClassName(containsString("Switch"))
        ))
            .check(matches(isChecked()))
    }

    @Test
    fun canEnableCrashReportsTest() {
        val prefEnableCrashReports = context.getString(R.string.preference_enable_crash_reports)

        assertThat(
            "Crash Reports enabled",
            context.crashReportsEnabled,
            `is`(equalTo(false))
        )

        onView(
            allOf(
                withClassName(containsString("TextView")),
                withText(R.string.title_crash_reports)
            )
        )
            .check(matches(not(isChecked())))
            .perform(click())
        assertThat(
            "Crash Reports pref enabled",
            prefs.getBoolean(prefEnableCrashReports, false),
            `is`(equalTo(true))
        )
        assertThat(
            "Analytics enabled",
            context.analyticsEnabled,
            `is`(equalTo(false))
        )
        assertThat(
            "Crash Reports enabled",
            context.crashReportsEnabled,
            `is`(equalTo(true))
        )
    }

    @Test
    fun showsEnabledCrashReportsTest() {
        val prefEnableCrashReports = context.getString(R.string.preference_enable_crash_reports)
        onView(allOf(
            withParent(hasSibling(
                withChild(allOf(
                    withClassName(containsString("TextView")),
                    withText(R.string.title_crash_reports))))), withClassName(containsString("Switch"))
        ))
            .check(matches(not(isChecked())))
        prefs.edit() { putBoolean(prefEnableCrashReports, true) }
        Espresso.onIdle()

        assertThat(
            "Crash Reports enabled",
            context.crashReportsEnabled,
            `is`(equalTo(true))
        )
        mActivityScenarioRule.scenario.recreate()

        onView(allOf(
            withParent(hasSibling(
                withChild(allOf(
                    withClassName(containsString("TextView")),
                    withText(R.string.title_crash_reports))))), withClassName(containsString("Switch"))
        ))
            .check(matches(isChecked()))
    }

    private fun childAtPosition(
        parentMatcher: Matcher<View>, position: Int,
    ): Matcher<View> {

        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("Child at position $position in parent ")
                parentMatcher.describeTo(description)
            }

            public override fun matchesSafely(view: View): Boolean {
                val parent = view.parent
                return parent is ViewGroup && parentMatcher.matches(parent)
                        && view == parent.getChildAt(position)
            }
        }
    }
}
