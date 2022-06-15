package org.northwinds.app.sotachaser

import android.os.Looper
import android.os.Looper.getMainLooper
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.liveData
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okhttp3.mock.MockInterceptor
import org.junit.Assert.*
import org.junit.Test
import org.northwinds.app.sotachaser.repository.SummitsRepository
import javax.inject.Inject
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith
import org.northwinds.app.sotachaser.domain.models.Association
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.robolectric.annotation.LooperMode
import org.robolectric.junit.rules.BackgroundTestRule
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@RunWith(RobolectricTestRunner::class)
@Config(application = HiltTestApplication::class)
@HiltAndroidTest
class SummitRepositoryTest {
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule val backgroundRule = BackgroundTestRule()

    @Inject lateinit var repo: SummitsRepository

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun testCanOpenRepository() {
        assertTrue("Repository is null", this::repo.isInitialized)
    }

    @Test
    @LooperMode(LooperMode.Mode.LEGACY)
    fun testCanLoadAssociations() {
        runBlocking {
            repo.checkForRefresh()
        }
        //Robolectric.getBackgroundThreadScheduler().unPause()
        val result2 = /*liveData<List<Association>> {
            withContext(Dispatchers.IO) {*/
                repo.getAssociations()
            /*}
        }*/
        //assertEquals("Looper queued up", true, Looper.myLooper()!!.queue.isIdle)

        val result = result2.blockingObserve()
        //assertEquals("Looper queued up", true, Looper.myLooper()!!.queue.isIdle)
        //val result = .blockingObserve()
        shadowOf(getMainLooper()).idle()
        assertNotNull("Association result is null", result)
        assertEquals("Incorrect number of associations", 194, result!!.count())
    }

    @Inject lateinit var interceptor: MockInterceptor

    @Test
    fun testWillLoadRefreshFromNetwork() {
        assertFalse(interceptor.rules[0].isConsumed)
        runBlocking {
            repo.checkForRefresh()
        }
        assertTrue("HTTP request not made", interceptor.rules[0].isConsumed)
    }
}

private fun <T> LiveData<T>.blockingObserve(): T? {
    var value: T? = null
    val latch = CountDownLatch(1)

    val observer = Observer<T> { t ->
        value = t
        latch.countDown()
    }
    //assertEquals("Looper queued up", true, Looper.myLooper()!!.queue.isIdle)

    observeForever(observer)
    //assertEquals("Looper queued up", true, Looper.myLooper()!!.queue.isIdle)

    latch.await(35, TimeUnit.SECONDS)
    Robolectric.flushBackgroundThreadScheduler()
    Robolectric.flushForegroundThreadScheduler()
    //assertEquals("Looper queued up", true, Looper.myLooper()!!.queue.isIdle)
    return value
}
