package org.northwinds.app.sotachaser.util

import org.northwinds.app.sotachaser.SummitList
import org.northwinds.app.sotachaser.room.Association
import org.northwinds.app.sotachaser.room.Region
import org.northwinds.app.sotachaser.room.Summit

fun SummitList.asAssociationDatabaseModel(): List<Association> {
    return associations.map { association ->
        Association(0, association.key, association.value)
    }
}

fun SummitList.asRegionDatabaseModel(associationToId: Map<String, Long>): List<Region> {
    return regions.map { (key, value) ->
        val (assoc, code) = key.split("/")
        Region(0, associationToId[assoc]!!, code, value)
    }
}

fun SummitList.asSummitDatabaseModel(regionToId: Map<String, Long>): List<Summit> {
    //Room.inMemoryDatabaseBuilder(null, null)
    //val assocToId = items.map { it.code }.zip(aids).toMap()
    //val idToAssoc = assocToId.entries.associateBy({ it.value }) { it.key }
    //val rids = dao.insertRegion(*items2.toTypedArray())
    //val regionToId = items2.map { "${idToAssoc[it.associationId]}/${it.code}" }.zip(rids).toMap()
    return summitsByRegion.flatMap { (assoc, value) -> value.flatMap { (region, summits) -> summits.map { summit ->
        Summit(0,
            regionToId["${assoc}/${region}"]!!,
            summit.summitCode.split('-')[1],
            summit.summitName,
            summit.altM,
            summit.altFt,
            summit.gridRef1,
            summit.gridRef2,
            summit.longitude,
            summit.latitude,
            summit.points,
            summit.bonusPoints,
            summit.validFrom,
            summit.validTo,
            summit.activationCount,
            summit.activationDate,
            summit.activationCall,
        )
    } } }
}
