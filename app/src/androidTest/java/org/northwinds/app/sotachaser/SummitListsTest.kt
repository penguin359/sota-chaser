package org.northwinds.app.sotachaser

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

@RunWith(AndroidJUnit4::class)
class SummitListsTest {
    @Test
    fun canLoadSummitList() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val list = SummitList(appContext.resources.openRawResource(R.raw.summitslist))

        assertNotNull(list.summits)
    }
}