package org.northwinds.app.sotachaser.ui.associations

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.greaterThanOrEqualTo
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.northwinds.app.sotachaser.repository.SummitsRepository
import org.northwinds.app.sotachaser.ui.getOrAwaitValue
import java.util.concurrent.ExecutorService
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class AssociationViewModelTest {
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var executor: ExecutorService

    @Inject
    lateinit var repo: SummitsRepository

    private lateinit var model: AssociationViewModel

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        hiltRule.inject()
        model = AssociationViewModel(executor, repo)
    }

    @Test
    fun load_map_view_model() {
        val associations = model.list_items.getOrAwaitValue()
        assertEquals(
            "Incorrect number of associations",
            194, associations.count()
        )
        val associationIndex = associations.indexOfFirst { it.code == "W7O" }
        assertThat(
            "Can't find W7O association",
            associationIndex,
            greaterThanOrEqualTo(0)
        )

        val associationIndex2 = model.list_items.getOrAwaitValue().indexOfFirst { it.code == "W7W" }
        assertThat(
            "Can't find W7W association",
            associationIndex2,
            greaterThanOrEqualTo(0)
        )
    }

    @Test
    fun can_filter_on_code_map_view_model() {
        model.setFilter("3")
        val associations = model.list_items.getOrAwaitValue()
        assertEquals(
            "Incorrect number of associations",
            13, associations.count()
        )
        assertEquals("3Y", model.list_items.value!![0].code)
        assertEquals("ZL3", model.list_items.value!![12].code)
    }

    @Test
    fun can_filter_on_single_code_map_view_model() {
        model.setFilter("3Y")
        val associations = model.list_items.getOrAwaitValue()
        assertEquals(
            "Incorrect number of associations",
            1, associations.count()
        )
        assertEquals("3Y", model.list_items.value!![0].code)
    }

    @Test
    fun can_filter_out_all_codes_map_view_model() {
        model.setFilter("3YZ")
        val associations = model.list_items.getOrAwaitValue()
        assertEquals(
            "Incorrect number of associations",
            0, associations.count()
        )
    }

    @Test
    fun can_filter_ignoring_case_on_codes_map_view_model() {
        model.setFilter("vk")
        val associations = model.list_items.getOrAwaitValue()
        assertEquals(
            "Incorrect number of associations",
            9, associations.count()
        )
    }

    @Test
    fun can_filter_on_name_map_view_model() {
        model.setFilter("Mex")
        val associations = model.list_items.getOrAwaitValue()
        assertEquals(
            "Incorrect number of associations",
            4, associations.count()
        )
        assertEquals("W5N", model.list_items.value!![0].code)
        assertEquals("XE3", model.list_items.value!![3].code)
    }

    @Test
    fun can_filter_on_single_name_map_view_model() {
        model.setFilter("New Mex")
        val associations = model.list_items.getOrAwaitValue()
        assertEquals(
            "Incorrect number of associations",
            1, associations.count()
        )
        assertEquals("W5N", model.list_items.value!![0].code)
    }

    @Test
    fun can_filter_out_all_names_map_view_model() {
        model.setFilter("Mexa")
        val associations = model.list_items.getOrAwaitValue()
        assertEquals(
            "Incorrect number of associations",
            0, associations.count()
        )
    }

    @Test
    fun can_filter_ignoring_case_on_names_map_view_model() {
        model.setFilter("us")
        val associations = model.list_items.getOrAwaitValue()
        assertEquals(
            "Incorrect number of associations",
            51, associations.count()
        )
    }

    @Test
    fun can_filter_ignoring_case_on_codes_and_names_map_view_model() {
        model.setFilter("DU")
        val associations = model.list_items.getOrAwaitValue()
        assertEquals(
            "Incorrect number of associations",
            5, associations.count()
        )
        assertEquals("DU2", model.list_items.value!![0].code)
        assertEquals("HR", model.list_items.value!![4].code)
    }

    @Test
    fun update_map_view_model() {
        model.refresh()
    }
}
