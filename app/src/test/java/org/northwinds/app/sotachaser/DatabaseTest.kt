package org.northwinds.app.sotachaser

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
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
    fun canFindAssociationByCode() {
        assertThat("Association Name",
            dao.getAssociationByCode("W7O")?.name,
            containsString("Oregon"))
        assertThat("Association Name",
            dao.getAssociationByCode("PR8")?.name,
            containsString("Maranhão"))
        assertThat("Association Name",
            dao.getAssociationByCode("PYT")?.name,
            containsString("Trindade & Martim"))
    }

    @Test
    @BackgroundTestRule.BackgroundTest
    fun canFindRegionByCode() {
        assertThat("Region Name",
            dao.getAssociationByCode("W7O")?.let {
                dao.getRegionByCode(it.id, "NC")?.name
            }, containsString("North Coastal"))
        assertThat("Region Name",
            dao.getAssociationByCode("A6")?.let {
                dao.getRegionByCode(it.id, "AJ")?.name
            }, containsString("عجمان"))
        assertThat("Region Name",
            dao.getAssociationByCode("DM")?.let {
                dao.getRegionByCode(it.id, "BW")?.name
            }, containsString("Baden-Württemberg"))
    }

    @Test
    @BackgroundTestRule.BackgroundTest
    fun canFindSummitByCode() {
        assertThat("Summit Name",
            dao.getAssociationByCode("W7O")?.let {
                dao.getRegionByCode(it.id, "NC")?.let {
                    dao.getSummitByCode(it.id, "001")?.name
            }}, containsString("Rogers Peak"))
        assertThat("Summit Name",
            dao.getAssociationByCode("UT")?.let {
                dao.getRegionByCode(it.id, "CR")?.let {
                    dao.getSummitByCode(it.id, "042")?.name
                }}, containsString("Таш-Джорган"))
        assertThat("Summit Name",
            dao.getAssociationByCode("HL")?.let {
                dao.getRegionByCode(it.id, "GN")?.let {
                    dao.getSummitByCode(it.id, "217")?.name
                }}, containsString("황새봉"))
    }
    @Test
    @BackgroundTestRule.BackgroundTest
    fun loadsAllSummitDetails() {
        val summitValue = dao.getAssociationByCode("HL")?.let {
            dao.getRegionByCode(it.id, "GN")?.let {
                dao.getSummitByCode(it.id, "046")
        }}

        assertThat("Summit object", summitValue, `is`(notNullValue()))
        val summit = summitValue!!
        assertEquals("046", summit.code)
        assertEquals("뒷삐알산 (Dwitppialsan)", summit.name)
        assertEquals(827, summit.altM)
        assertEquals(2713, summit.altFt)
        assertEquals("128.9960", summit.gridRef1)
        assertEquals("35.4368", summit.gridRef2)
        assertEquals(128.996, summit.longitude)
        assertEquals(35.4368, summit.latitude)
        assertEquals(6, summit.points)
        assertEquals(3, summit.bonusPoints)
        assertEquals("01/07/2010", summit.validFrom)
        assertEquals("31/12/2099", summit.validTo)
        assertEquals(9, summit.activationCount)
        assertEquals("02/10/2020", summit.activationDate)
        assertEquals("DS5VKX", summit.activationCall)
    }

    @Test
    @BackgroundTestRule.BackgroundTest
    fun has_correct_number_of_summits() {
        val associations = dao.getAssociations()
        assertEquals(194, associations.count(), "Incorrect number of summit associations")
        assertThat("Region Count",
            dao.getAssociationByCode("W7O")?.let {
                dao.getRegionsInAssociation(it.id)
            }?.count(),
            equalTo(10))
        assertThat("Region Count",
            dao.getRegionsInAssociationName("W7O").count(),
            equalTo(10))
        assertThat("Summit Count",
            dao.getAssociationByCode("W7O")?.let { assoc ->
                dao.getRegionByCode(assoc.id, "NC")?.let { region ->
                    dao.getSummitsInRegion(region.id)
                }
            }?.count(),
            equalTo(127))
        assertThat("Summit Count",
            dao.getSummits("W7O", "NC").count(),
            equalTo(127))
    }
}
