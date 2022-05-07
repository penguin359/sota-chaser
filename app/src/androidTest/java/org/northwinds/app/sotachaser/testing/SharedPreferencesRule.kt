package org.aprsdroid.app.testing

import org.junit.rules.TestRule
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.runner.Description
import org.junit.runners.model.Statement

//abstract class SharedPreferencesRule : TestRule {
class SharedPreferencesRule constructor(val modifyPreferences: (SharedPreferences) -> Unit) : TestRule {
    var preferences: SharedPreferences? = null
        private set

    //protected abstract fun modifyPreferences(preferences: SharedPreferences?)
    override fun apply(base: Statement, description: Description): Statement {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        preferences = PreferenceManager.getDefaultSharedPreferences(appContext)
        modifyPreferences(preferences!!)
        return base
    }
}