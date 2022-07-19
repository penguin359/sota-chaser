package org.northwinds.app.sotachaser

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.junit.Before
import org.junit.Test
import org.northwinds.app.sotachaser.room.SummitDao
import org.northwinds.app.sotachaser.room.model.AssociationEntity
import org.northwinds.app.sotachaser.room.model.RegionEntity
import org.northwinds.app.sotachaser.room.model.SummitEntity
import org.northwinds.app.sotachaser.util.asAssociationDatabaseModel
import org.northwinds.app.sotachaser.util.asRegionDatabaseModel
import org.northwinds.app.sotachaser.util.asSummitDatabaseModel
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.test.assertEquals

class DomainTests {
    private lateinit var list: SummitList

    class MockDao : SummitDao {
        override fun insertAssociation(vararg users: AssociationEntity): List<Long> {
            return listOf()
        }

        override fun updateAssociation(vararg users: AssociationEntity): Int {
            return 0
        }

        override fun getAssociations(): LiveData<List<AssociationEntity>> {
            return MutableLiveData()
        }

        override fun getAssociationByCode(code: String): AssociationEntity? {
            return null
        }

        override fun getAssociationByCode2(code: String): LiveData<AssociationEntity?> {
            return MutableLiveData()
        }

        override fun insertRegion(vararg users: RegionEntity): List<Long> {
            return listOf()
        }

        override fun updateRegion(vararg users: RegionEntity): Int {
            return 0
        }

        override fun getRegionByCode(associationId: Long, code: String): RegionEntity? {
            return null
        }

        override fun getRegionByCode2(associationId: Long, code: String): LiveData<RegionEntity?> {
            return MutableLiveData()
        }

        override fun getRegionsInAssociation(associationId: Long): List<RegionEntity> {
            return listOf()
        }

        override fun getRegionsInAssociationName(associationId: String): LiveData<List<RegionEntity>> {
            return MutableLiveData()
        }

        override fun insertSummit(vararg users: SummitEntity): List<Long> {
            return listOf()
        }

        override fun updateSummit(vararg users: SummitEntity): Int {
            return 0
        }

        override fun getSummitByCode(regionId: Long, code: String): SummitEntity? {
            return null
        }

        override fun getSummitsInRegion(regionId: Long): List<SummitEntity> {
            return listOf()
        }

        override fun getSummits(
            associationId: String,
            region: String
        ): LiveData<List<SummitEntity>> {
            return MutableLiveData()
        }

        override fun clear() {
        }
    }

    private val dummyDao = MockDao()

    @Before
    fun setup() {
        val fileName = "src/main/res/raw/summitslist.csv"
        val myPath = Paths.get(fileName)

        val input = Files.newInputStream(myPath)
        list = SummitList(input)
    }

    @Test
    fun canMapToAssociationModel() {
        assertEquals(194, list.asAssociationDatabaseModel(dummyDao).count(), "Incorrect number of summit associations")
    }

    @Test
    fun canMapToRegionModel() {
        val w7oId = 1L
        val w7wId = 2L
        val associationToId = list.asAssociationDatabaseModel(dummyDao).map { it.code to 0L }.toMap().toMutableMap()
        associationToId["W7O"] = w7oId
        associationToId["W7W"] = w7wId
        val regions = list.asRegionDatabaseModel(dummyDao, associationToId)
        assertEquals(10, regions.filter { it.associationId == w7oId }.count(), "Incorrect number of summit regions")
        assertEquals(17, regions.filter { it.associationId == w7wId }.count(), "Incorrect number of summit regions")
    }

    @Test
    fun canMapToSummitModel() {
        val associationToId = list.asAssociationDatabaseModel(dummyDao).withIndex().map { (idx, it) -> it.code to idx.toLong() }.toMap()
        val idToAssociation = associationToId.entries.associate { (k, v) -> v to k }
        val regions = list.asRegionDatabaseModel(dummyDao, associationToId)
        val regionToId = list.asRegionDatabaseModel(dummyDao, associationToId).withIndex().map { (idx, it) ->
            "${idToAssociation[it.associationId]}/${it.code}" to idx.toLong() }.toMap()
        val summits = list.asSummitDatabaseModel(dummyDao, regionToId)
        assertEquals(138, summits.filter { it.regionId == regionToId["W7O/WV"] }.count(), "Incorrect number of summits")
        assertEquals(169, summits.filter { it.regionId == regionToId["W7W/LC"] }.count(), "Incorrect number of summits")
    }
}
