package org.northwinds.app.sotachaser

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.northwinds.app.sotachaser.room.SummitDao
import org.northwinds.app.sotachaser.room.SummitDatabase
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.junit.rules.BackgroundTestRule
import java.lang.Exception
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
@Config(manifest=Config.NONE)
class DatabaseTest {
    @get:Rule val backgroundRule = BackgroundTestRule()

    var db: SummitDatabase? = null
    lateinit var dao: SummitDao

    @Before
    fun setup_database() {
        val fileName = "src/main/res/raw/summitslist.csv"
        val myPath = Paths.get(fileName)

        val input = Files.newInputStream(myPath)
        val list = SummitList(input)

        db = Room.databaseBuilder(
            ApplicationProvider.getApplicationContext(),
            SummitDatabase::class.java, "database"
        ).build()
        dao = db!!.summitDao()
        try {
            SummitInterface.load_database(dao, list)
        } catch (ex: Exception) {
            shutdown_database()
            throw ex
        }
    }

    @After
    fun shutdown_database() {
        if (db?.isOpen == true) {
            db?.close()
        }

        db = null
    }

    @Test
    @BackgroundTestRule.BackgroundTest
    fun can_open_database() {
        val associations = dao.getAssociations()
        assertEquals(194, associations.count(), "Incorrect number of summit associations")
    }

    @Test
    @BackgroundTestRule.BackgroundTest
    fun has_correct_number_of_summits() {
        val fileName = "src/main/res/raw/summitslist.csv"
        val myPath = Paths.get(fileName)

        val input = Files.newInputStream(myPath)
        val list = SummitList(input)

        val db = Room.databaseBuilder(
            ApplicationProvider.getApplicationContext(),
            SummitDatabase::class.java, "database"
        ).build()
        val dao = db.summitDao()
        SummitInterface.load_database(dao, list)
        val associations = dao.getAssociations()
        assertEquals(194, associations.count(), "Incorrect number of summit associations")
    }
}
