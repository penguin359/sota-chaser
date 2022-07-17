package org.northwinds.app.sotachaser.util

import org.northwinds.app.sotachaser.SummitList
import org.northwinds.app.sotachaser.room.model.AssociationEntity
import org.northwinds.app.sotachaser.room.model.RegionEntity
import org.northwinds.app.sotachaser.room.model.SummitEntity

fun SummitList.asAssociationDatabaseModel(): List<AssociationEntity> {
    return associations.map { association ->
        AssociationEntity(0, association.key, association.value)
    }
}

fun SummitList.asRegionDatabaseModel(associationToId: Map<String, Long>): List<RegionEntity> {
    return regions.map { (key, value) ->
        val (assoc, code) = key.split("/")
        RegionEntity(0, associationToId[assoc]!!, code, value)
    }
}

fun SummitList.asSummitDatabaseModel(regionToId: Map<String, Long>): List<SummitEntity> {
    return summitsByRegion.flatMap { (assoc, value) -> value.flatMap { (region, summits) -> summits.map { summit ->
        SummitEntity(0,
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
