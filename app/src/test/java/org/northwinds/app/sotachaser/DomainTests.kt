package org.northwinds.app.sotachaser

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.junit.Before
import org.junit.Test
import org.northwinds.app.sotachaser.room.SummitDao
import org.northwinds.app.sotachaser.room.model.*
import org.northwinds.app.sotachaser.util.asAssociationDatabaseModel
import org.northwinds.app.sotachaser.util.asRegionDatabaseModel
import org.northwinds.app.sotachaser.util.asSummitDatabaseModel
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.test.assertEquals

class DomainTests {
    private lateinit var list: SummitList

    class MockDao : SummitDao {
        override fun insertAssociation(vararg users: AssociationCsvEntity): List<Long> {
            TODO("Not yet implemented")
        }

        override fun insertAssociation(vararg users: AssociationEntity): List<Long> {
            return listOf()
        }

        override fun updateAssociation(vararg users: AssociationCsvEntity): Int {
            TODO("Not yet implemented")
        }

        override fun updateAssociation(vararg users: AssociationEntity): Int {
            return 0
        }

        override fun deleteAssociation(vararg users: AssociationEntity): Int {
            TODO("Not yet implemented")
        }

        override fun getAssociations(): LiveData<List<AssociationEntity>> {
            return MutableLiveData()
        }

        override fun getAssociationsPlain(): List<AssociationEntity> {
            TODO("Not yet implemented")
        }

        override fun getAssociationByCode(code: String): AssociationEntity? {
            return null
        }

        override fun getAssociationByCode2(code: String): LiveData<AssociationEntity?> {
            return MutableLiveData()
        }

        override fun insertRegion(vararg users: RegionCsvEntity): List<Long> {
            TODO("Not yet implemented")
        }

        override fun insertRegion(vararg users: RegionEntity): List<Long> {
            return listOf()
        }

        override fun updateRegion(vararg users: RegionCsvEntity): Int {
            TODO("Not yet implemented")
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

        override fun insertSummit(vararg users: SummitCsvEntity): List<Long> {
            TODO("Not yet implemented")
        }

        override fun insertSummit(vararg users: SummitJsonEntity): List<Long> {
            TODO("Not yet implemented")
        }

        override fun insertSummit(vararg users: SummitSingleJsonEntity): List<Long> {
            TODO("Not yet implemented")
        }

        override fun insertSummit(vararg users: SummitEntity): List<Long> {
            return listOf()
        }

        override fun updateSummit(vararg users: SummitCsvEntity): Int {
            TODO("Not yet implemented")
        }

        override fun updateSummit(vararg users: SummitJsonEntity): Int {
            TODO("Not yet implemented")
        }

        override fun updateSummit(vararg users: SummitSingleJsonEntity): Int {
            TODO("Not yet implemented")
        }

        override fun updateSummit(vararg users: SummitEntity): Int {
            return 0
        }

        override fun getSummitByCode(regionId: Long, code: String): SummitEntity? {
            return null
        }

        override fun getSummitByCodeLive(
            associationCode: String,
            regionCode: String,
            summitCode: String
        ): LiveData<SummitEntity?> {
            TODO("Not yet implemented")
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

        override fun insertGpxTrack(vararg users: GpxTrackEntity): List<Long> {
            TODO("Not yet implemented")
        }

        override fun deleteGpxTrack(vararg users: GpxTrackEntity): Int {
            TODO("Not yet implemented")
        }

        override fun getGpxTracks(summitId: Long): LiveData<List<GpxTrackEntity>> {
            TODO("Not yet implemented")
        }

        override fun getGpxTrack(id: Long): LiveData<GpxTrackEntity?> {
            TODO("Not yet implemented")
        }

        override fun insertGpxPoint(vararg users: GpxPointEntity): List<Long> {
            TODO("Not yet implemented")
        }

        override fun getGpxPoints(gpxTrackId: Long): LiveData<List<GpxPointEntity>> {
            TODO("Not yet implemented")
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
        //val regions = list.asRegionDatabaseModel(dummyDao, associationToId)
        val regionToId = list.asRegionDatabaseModel(dummyDao, associationToId).withIndex().map { (idx, it) ->
            "${idToAssociation[it.associationId]}/${it.code}" to idx.toLong() }.toMap()
        val summits = list.asSummitDatabaseModel(dummyDao, regionToId)
        assertEquals(138, summits.filter { it.regionId == regionToId["W7O/WV"] }.count(), "Incorrect number of summits")
        assertEquals(169, summits.filter { it.regionId == regionToId["W7W/LC"] }.count(), "Incorrect number of summits")
    }
}
