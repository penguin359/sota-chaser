package org.northwinds.app.sotachaser

import android.app.Application
import android.content.SharedPreferences
import android.util.Log
import androidx.preference.PreferenceManager
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.ktx.Firebase

import dagger.hilt.android.HiltAndroidApp




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
@HiltAndroidApp
class SotaChaserApplication : SotaChaserBaseApplication() {}

open class SotaChaserBaseApplication : Application() {
    companion object {
        private const val TAG = "SOTAChaser-App"
    }

    private lateinit var pref_enable_analytics: String
    private lateinit var pref_enable_crash_reports: String

    var analyticsEnabled = false
        private set
    var crashReportsEnabled = false
        private set

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    private val listener =
        SharedPreferences.OnSharedPreferenceChangeListener(function = { prefs, key ->
            if (key == pref_enable_analytics || key == null) {
                analyticsEnabled = prefs.getBoolean(pref_enable_analytics, false)
                Log.v(TAG, "Analytics status changed: $analyticsEnabled")
                firebaseAnalytics.setAnalyticsCollectionEnabled(analyticsEnabled)
            }
            if (key == pref_enable_crash_reports || key == null) {
                crashReportsEnabled = prefs.getBoolean(pref_enable_crash_reports, false)
                Log.v(TAG, "Crash reports status changed: $crashReportsEnabled")
                FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(crashReportsEnabled)
            }
        })

    override fun onCreate() {
        super.onCreate()
        pref_enable_analytics = getString(R.string.preference_enable_analytics)
        pref_enable_crash_reports = getString(R.string.preference_enable_crash_reports)

        firebaseAnalytics = Firebase.analytics

        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        prefs.registerOnSharedPreferenceChangeListener(listener)
        analyticsEnabled = prefs.getBoolean(pref_enable_analytics, false)
        crashReportsEnabled = prefs.getBoolean(pref_enable_crash_reports, false)
        Log.v(TAG, "Analytics status initial: $analyticsEnabled")
        Log.v(TAG, "Crash reports status initial: $crashReportsEnabled")
        firebaseAnalytics.setAnalyticsCollectionEnabled(analyticsEnabled)
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(crashReportsEnabled)
    }
}
