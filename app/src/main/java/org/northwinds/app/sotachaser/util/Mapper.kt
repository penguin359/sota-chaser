package org.northwinds.app.sotachaser.util

import org.northwinds.app.sotachaser.SummitList
import org.northwinds.app.sotachaser.room.SummitDao
import org.northwinds.app.sotachaser.room.model.*

fun SummitList.asAssociationDatabaseModel(dao: SummitDao): List<AssociationCsvEntity> {
    return associations.map { association ->
        val old = dao.getAssociationByCode(association.key)
        AssociationCsvEntity(old?.id ?: 0, association.key, association.value)
    }
}

fun SummitList.asRegionDatabaseModel(dao: SummitDao, associationToId: Map<String, Long>): List<RegionCsvEntity> {
    return regions.map { (key, value) ->
        val (assoc, code) = key.split("/")
        val association = dao.getAssociationByCode(assoc)
        val old = association?.let { aid -> dao.getRegionByCode(aid.id, code) }
        RegionCsvEntity(old?.id ?: 0, associationToId[assoc]!!, code, value)
    }
}

fun SummitList.asSummitDatabaseModel(dao: SummitDao, regionToId: Map<String, Long>): List<SummitCsvEntity> {
    return summitsByRegion.flatMap { (assoc, value) -> value.flatMap { (region, summits) -> summits.map { summit ->
        val old = dao.getSummitByCode(regionToId["${assoc}/${region}"]!!, summit.summitCode.split('-')[1])
        SummitCsvEntity(old?.id ?: 0,
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
