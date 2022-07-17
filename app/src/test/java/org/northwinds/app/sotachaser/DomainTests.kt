package org.northwinds.app.sotachaser

import org.junit.Before
import org.junit.Test
import org.northwinds.app.sotachaser.util.asAssociationDatabaseModel
import org.northwinds.app.sotachaser.util.asRegionDatabaseModel
import org.northwinds.app.sotachaser.util.asSummitDatabaseModel
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.test.assertEquals

class DomainTests {
    private lateinit var list: SummitList

    @Before
    fun setup() {
        val fileName = "src/main/res/raw/summitslist.csv"
        val myPath = Paths.get(fileName)

        val input = Files.newInputStream(myPath)
        list = SummitList(input)
    }

    @Test
    fun canMapToAssociationModel() {
        assertEquals(194, list.asAssociationDatabaseModel().count(), "Incorrect number of summit associations")
    }

    @Test
    fun canMapToRegionModel() {
        val w7oId = 1L
        val w7wId = 2L
        val associationToId = list.asAssociationDatabaseModel().map { it.code to 0L }.toMap().toMutableMap()
        associationToId["W7O"] = w7oId
        associationToId["W7W"] = w7wId
        val regions = list.asRegionDatabaseModel(associationToId)
        assertEquals(10, regions.filter { it.associationId == w7oId }.count(), "Incorrect number of summit regions")
        assertEquals(17, regions.filter { it.associationId == w7wId }.count(), "Incorrect number of summit regions")
    }

    @Test
    fun canMapToSummitModel() {
        val associationToId = list.asAssociationDatabaseModel().withIndex().map { (idx, it) -> it.code to idx.toLong() }.toMap()
        val idToAssociation = associationToId.entries.associate { (k, v) -> v to k }
        val regions = list.asRegionDatabaseModel(associationToId)
        val regionToId = list.asRegionDatabaseModel(associationToId).withIndex().map { (idx, it) ->
            "${idToAssociation[it.associationId]}/${it.code}" to idx.toLong() }.toMap()
        val summits = list.asSummitDatabaseModel(regionToId)
        assertEquals(138, summits.filter { it.regionId == regionToId["W7O/WV"] }.count(), "Incorrect number of summits")
        assertEquals(169, summits.filter { it.regionId == regionToId["W7W/LC"] }.count(), "Incorrect number of summits")
    }
}
