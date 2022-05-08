package org.northwinds.app.sotachaser

import android.text.method.TextKeyListener.clear
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.firebase.crashlytics.FirebaseCrashlytics
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.lang.Thread.sleep

@RunWith(AndroidJUnit4::class)
class AnalyticsTest {
    private val context = ApplicationProvider.getApplicationContext<SotaChaserBaseApplication>()
    private val prefs = PreferenceManager.getDefaultSharedPreferences(context)
    private val prefEnableAnalytics = context.getString(R.string.preference_enable_analytics)
    private val prefEnableCrashReports = context.getString(R.string.preference_enable_crash_reports)

    @Before
    fun setUp() {
        prefs.edit() { clear() }
        Espresso.onIdle()
    }

    @Test
    fun disabledIfPreferencesCleared() {
        assertThat("Analytics enabled", context.analyticsEnabled, `is`(equalTo(false)))
        assertThat("Crash Reports enabled", context.crashReportsEnabled, `is`(equalTo(false)))
    }

    @Test
    fun canEnableAnalytics() {
        prefs.edit() { putBoolean(prefEnableAnalytics, true) }
        Espresso.onIdle()
        assertThat("Analytics enabled", context.analyticsEnabled, `is`(equalTo(true)))
        assertThat("Crash Reports enabled", context.crashReportsEnabled, `is`(equalTo(false)))
    }

    @Test
    fun willDisableAnalyticsWhenAsked() {
        prefs.edit() { putBoolean(prefEnableAnalytics, true) }
        Espresso.onIdle()
        assertThat("Analytics enabled", context.analyticsEnabled, `is`(equalTo(true)))
        prefs.edit() { putBoolean(prefEnableAnalytics, false) }
        Espresso.onIdle()
        assertThat("Analytics enabled", context.analyticsEnabled, `is`(equalTo(false)))
    }

    @Test
    fun willToggleAnalyticsWhenAsked() {
        prefs.edit() { putBoolean(prefEnableAnalytics, true) }
        Espresso.onIdle()
        assertThat("Analytics enabled", context.analyticsEnabled, `is`(equalTo(true)))
        prefs.edit() { putBoolean(prefEnableAnalytics, false) }
        Espresso.onIdle()
        assertThat("Analytics enabled", context.analyticsEnabled, `is`(equalTo(false)))
        prefs.edit() { putBoolean(prefEnableAnalytics, true) }
        Espresso.onIdle()
        assertThat("Analytics enabled", context.analyticsEnabled, `is`(equalTo(true)))
    }

    @Test
    fun willDisableAnalyticsWhenPrefsCleared() {
        prefs.edit() { putBoolean(prefEnableAnalytics, true) }
        Espresso.onIdle()
        assertThat("Analytics enabled", context.analyticsEnabled, `is`(equalTo(true)))
        prefs.edit() { clear() }
        Espresso.onIdle()
        assertThat("Analytics enabled", context.analyticsEnabled, `is`(equalTo(false)))
    }

    @Test
    fun canEnableCrashReports() {
        prefs.edit() { putBoolean(prefEnableCrashReports, true) }
        Espresso.onIdle()
        assertThat("Analytics enabled", context.analyticsEnabled, `is`(equalTo(false)))
        assertThat("Crash Reports enabled", context.crashReportsEnabled, `is`(equalTo(true)))
    }

    @Test
    fun willDisableCrashReportsWhenAsked() {
        prefs.edit() { putBoolean(prefEnableCrashReports, true) }
        Espresso.onIdle()
        assertThat("Crash Reports enabled", context.crashReportsEnabled, `is`(equalTo(true)))
        prefs.edit() { putBoolean(prefEnableCrashReports, false) }
        Espresso.onIdle()
        assertThat("Crash Reports enabled", context.crashReportsEnabled, `is`(equalTo(false)))
    }

    @Test
    fun willToggleCrashReportsWhenAsked() {
        prefs.edit() { putBoolean(prefEnableCrashReports, true) }
        Espresso.onIdle()
        assertThat("Crash Reports enabled", context.crashReportsEnabled, `is`(equalTo(true)))
        prefs.edit() { putBoolean(prefEnableCrashReports, false) }
        Espresso.onIdle()
        assertThat("Crash Reports enabled", context.crashReportsEnabled, `is`(equalTo(false)))
        prefs.edit() { putBoolean(prefEnableCrashReports, true) }
        Espresso.onIdle()
        assertThat("Crash Reports enabled", context.crashReportsEnabled, `is`(equalTo(true)))
    }

    @Test
    fun willDisableCrashReportsWhenPrefsCleared() {
        prefs.edit() { putBoolean(prefEnableCrashReports, true) }
        Espresso.onIdle()
        assertThat("Crash Reports enabled", context.crashReportsEnabled, `is`(equalTo(true)))
        prefs.edit() { clear() }
        Espresso.onIdle()
        assertThat("Crash Reports enabled", context.crashReportsEnabled, `is`(equalTo(false)))
    }
}
