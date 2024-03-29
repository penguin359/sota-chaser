package org.northwinds.app.sotachaser.ui.summitdetails

import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import androidx.core.os.bundleOf
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.intent.matcher.IntentMatchers.hasData
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.containsString
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.northwinds.app.sotachaser.R
import org.northwinds.app.sotachaser.repository.SummitsRepository
import org.northwinds.app.sotachaser.testing.HiltFragmentScenario
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class SummitDetailsFragmentTest {
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var repo: SummitsRepository

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun canOpenFragment() {
        HiltFragmentScenario.launchInHiltContainer(
            SummitDetailsFragment::class.java, bundleOf(
            "association" to "HL",
            "region" to "GN",
            "summit" to "001",
        ))
    }

    @Test
    fun canOpenSpecificSummit() {
        runBlocking {
            repo.updateAssociation("HL")
            repo.updateRegion("HL", "GN")
        }
        HiltFragmentScenario.launchInHiltContainer(
            SummitDetailsFragment::class.java, bundleOf(
            "association" to "HL",
            "region" to "GN",
            "summit" to "001",
        ))
        Espresso.onView(withId(R.id.code))
            .check(matches(withText(containsString("HL/GN-001"))))
        Espresso.onView(withId(R.id.name))
            .check(matches(withText(containsString("지리산"))))
        Espresso.onView(withId(R.id.points))
            .check(matches(withText(containsString("10"))))
        Espresso.onView(withId(R.id.altitude))
            .check(matches(withText(containsString("6284"))))
        Espresso.onView(withId(R.id.activation_callsign))
            .check(matches(withText(containsString("DS5SQS"))))
        Espresso.onView(withId(R.id.activation_date))
            .check(matches(withText(containsString("2022-01-08"))))
        Espresso.onView(withId(R.id.activation_count))
            .check(matches(withText(containsString("37"))))
    }

    @Test
    fun canOpenDifferentSummit() {
        runBlocking {
            repo.updateAssociation("W7W")
            repo.updateRegion("W7W", "LC")
        }
        HiltFragmentScenario.launchInHiltContainer(
            SummitDetailsFragment::class.java, bundleOf(
            "association" to "W7W",
            "region" to "LC",
            "summit" to "052",
        ))
        Espresso.onView(withId(R.id.code))
            .check(matches(withText(containsString("W7W/LC-052"))))
        Espresso.onView(withId(R.id.name))
            .check(matches(withText(containsString("West Soda Peaks"))))
        Espresso.onView(withId(R.id.points))
            .check(matches(withText(containsString("4"))))
        Espresso.onView(withId(R.id.altitude))
            .check(matches(withText(containsString("4540"))))
        Espresso.onView(withId(R.id.activation_callsign))
            .check(matches(withText(containsString("K7WXW"))))
        Espresso.onView(withId(R.id.activation_date))
            .check(matches(withText(containsString("2022-06-25"))))
        Espresso.onView(withId(R.id.activation_count))
            .check(matches(withText(containsString("8"))))
    }

    @Test
    fun canOpenUrlToSota() {
        Intents.init()
        Intents.intending(hasAction(Intent.ACTION_VIEW)).respondWith(
            Instrumentation.ActivityResult(Activity.RESULT_OK, Intent())
        )
        HiltFragmentScenario.launchInHiltContainer(
            SummitDetailsFragment::class.java, bundleOf(
                "association" to "HL",
                "region" to "GN",
                "summit" to "001",
            )
        )
        Espresso.onView(withId(R.id.sota_btn)).perform(click())
        Intents.intended(
            allOf(
                hasAction(Intent.ACTION_VIEW),
                hasData("https://summits.sota.org.uk/summit/HL/GN-001")
            )
        )
        Intents.release()
    }

    @Test
    fun canOpenUrlToSotlas() {
        Intents.init()
        Intents.intending(hasAction(Intent.ACTION_VIEW)).respondWith(
            Instrumentation.ActivityResult(Activity.RESULT_OK, Intent())
        )
        HiltFragmentScenario.launchInHiltContainer(
            SummitDetailsFragment::class.java, bundleOf(
                "association" to "HL",
                "region" to "GN",
                "summit" to "001",
            )
        )
        Espresso.onView(withId(R.id.sotlas_btn)).perform(click())
        Espresso.onIdle()
        Intents.intended(
            allOf(
                hasAction(Intent.ACTION_VIEW),
                hasData("https://sotl.as/summits/HL/GN-001")
            )
        )
        Intents.release()
    }
}
