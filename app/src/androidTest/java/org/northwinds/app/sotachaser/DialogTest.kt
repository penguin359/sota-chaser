/**********************************************************************************
 * Copyright (c) 2022 Loren M. Lang                                               *
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


import androidx.core.content.edit
import androidx.preference.PreferenceManager
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.pressBack
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.aprsdroid.app.testing.SharedPreferencesRule
import org.hamcrest.Matchers.not
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.northwinds.app.sotachaser.ui.MainActivity

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class DialogTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val prefsRule = SharedPreferencesRule() {
        it.edit() {
            clear()
        }
    }

    @get:Rule(order = 2)
    val scenarioRule = ActivityScenarioRule(MainActivity::class.java)

    private val appContext = InstrumentationRegistry.getInstrumentation().targetContext
    private val prefEnableAnalytics = appContext.getString(R.string.preference_enable_analytics)
    private val prefEnableCrashReports =
        appContext.getString(R.string.preference_enable_crash_reports)
    private val prefs = PreferenceManager(appContext).sharedPreferences!!

    @Test
    fun analyticsAreDisabledByDefault() {
        assertFalse("Analytics enabled", prefs.getBoolean(prefEnableAnalytics, false))
        assertFalse("Crash reports enabled", prefs.getBoolean(prefEnableCrashReports, false))
    }

    @Test
    fun dialogIsShownOnLoad() {
        onView(withText(appContext.getString(R.string.title_analytics)))
            .inRoot(isDialog())
            .check(matches(isDisplayed()))
    }

    @Test
    fun boxesAreUncheckedByDefault() {
        onView(withText(appContext.getString(R.string.title_anonymous_usage)))
            .inRoot(isDialog())
            .check(matches(not(isChecked())))
        onView(withText(appContext.getString(R.string.title_crash_reports)))
            .inRoot(isDialog())
            .check(matches(not(isChecked())))
    }

    @Test
    fun analyticsStayDisabledByDefault() {
        onView(withText("OK"))
            .inRoot(isDialog())
            .perform(click())
        assertFalse("Analytics enabled", prefs.getBoolean(prefEnableAnalytics, false))
        assertFalse("Crash reports enabled", prefs.getBoolean(prefEnableCrashReports, false))
    }

    @Test
    fun analyticsWillEnableAnalyticsWhenChecked() {
        onView(withText(appContext.getString(R.string.title_anonymous_usage)))
            .inRoot(isDialog())
            .perform(click())
        onView(withText("OK"))
            .inRoot(isDialog())
            .perform(click())
        assertTrue("Analytics enabled", prefs.getBoolean(prefEnableAnalytics, false))
        assertFalse("Crash reports enabled", prefs.getBoolean(prefEnableCrashReports, false))
    }

    @Test
    fun analyticsWillEnableCrashReportsWhenChecked() {
        onView(withText(appContext.getString(R.string.title_crash_reports)))
            .inRoot(isDialog())
            .perform(click())
        onView(withText("OK"))
            .inRoot(isDialog())
            .perform(click())
        assertFalse("Analytics enabled", prefs.getBoolean(prefEnableAnalytics, false))
        assertTrue("Crash reports enabled", prefs.getBoolean(prefEnableCrashReports, false))
    }

    @Test
    fun analyticsWillNotEnableAnyOnBackButton() {
        onView(withText(appContext.getString(R.string.title_anonymous_usage)))
            .inRoot(isDialog())
            .perform(click())
        onView(withText(appContext.getString(R.string.title_crash_reports)))
            .inRoot(isDialog())
            .perform(click())
        onView(withText(appContext.getString(R.string.title_analytics)))
            .inRoot(isDialog())
            .perform(pressBack())
        assertFalse("Analytics enabled", prefs.getBoolean(prefEnableAnalytics, false))
        assertFalse("Crash reports enabled", prefs.getBoolean(prefEnableCrashReports, false))
    }
}

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class ChanglogDialogTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val prefsRule = SharedPreferencesRule() {
        it.edit() {
            putBoolean(appContext.getString(R.string.preference_asked_for_consent), true)
            putInt(prefChangelog, BuildConfig.VERSION_CODE)
        }
    }

    @get:Rule(order = 2)
    val scenarioRule = ActivityScenarioRule(MainActivity::class.java)

    private val appContext = InstrumentationRegistry.getInstrumentation().targetContext
    private val prefChangelog = appContext.getString(R.string.preference_changelog)
    private val prefs = PreferenceManager(appContext).sharedPreferences!!

    @Test
    fun dialogDoesNotShowNormally() {
        onView(withText(appContext.getString(R.string.title_changelog)))
            .check(doesNotExist())
    }

    @Test
    fun dialogIsShownOnFirstLoad() {
        prefs.edit() { remove(prefChangelog) }
        scenarioRule.scenario.recreate()
        onView(withText(appContext.getString(R.string.title_changelog)))
            .inRoot(isDialog())
            .check(matches(isDisplayed()))
    }

    @Test
    fun dialogIsShownOnUpdate() {
        prefs.edit() { putInt(prefChangelog, BuildConfig.VERSION_CODE-1) }
        scenarioRule.scenario.recreate()
        onView(withText(appContext.getString(R.string.title_changelog)))
            .inRoot(isDialog())
            .check(matches(isDisplayed()))
    }

    @Test
    fun dialogIsShownOnAllUpdates() {
        prefs.edit() { putInt(prefChangelog, 1) }
        scenarioRule.scenario.recreate()
        onView(withText(appContext.getString(R.string.title_changelog)))
            .inRoot(isDialog())
            .check(matches(isDisplayed()))
    }

    @Test
    fun dialogIsShownOnlyOncePerUpdate() {
        prefs.edit() { putInt(prefChangelog, BuildConfig.VERSION_CODE-1) }
        scenarioRule.scenario.recreate()
        onView(withText(appContext.getString(R.string.title_changelog)))
            .inRoot(isDialog())
            .check(matches(isDisplayed()))
        onView(withId(android.R.id.button1)).perform(click())
        onView(withText(appContext.getString(R.string.title_changelog)))
            .check(doesNotExist())
        scenarioRule.scenario.recreate()
        onView(withText(appContext.getString(R.string.title_changelog)))
            .check(doesNotExist())
    }
}
