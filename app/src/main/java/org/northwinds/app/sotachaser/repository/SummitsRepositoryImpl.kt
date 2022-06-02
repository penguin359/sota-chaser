package org.northwinds.app.sotachaser.repository

import android.app.Application
import org.northwinds.app.sotachaser.SummitList
import org.northwinds.app.sotachaser.domain.models.Association
import org.northwinds.app.sotachaser.domain.models.Region
import org.northwinds.app.sotachaser.domain.models.Summit
import org.northwinds.app.sotachaser.room.*
import org.northwinds.app.sotachaser.util.asAssociationDatabaseModel
import org.northwinds.app.sotachaser.util.asRegionDatabaseModel
import org.northwinds.app.sotachaser.util.asSummitDatabaseModel
import javax.inject.Inject

class SummitsRepositoryImpl @Inject constructor(private val context: Application, private val dao: SummitDao) : SummitsRepository {
    override fun getAssociations(): List<Association> {
        return dao.getAssociations().asDomainModel()
    }

    override fun getRegionsInAssociationName(associationId: String): List<Region> {
        return dao.getRegionsInAssociationName(associationId).asDomainModel()
    }

    override fun getSummits(associationId: String, region: String): List<Summit> {
        return dao.getSummits(associationId, region).asDomainModel()
    }

    companion object {
        fun loadDatabase(dao: SummitDao, summitList: SummitList) {
            dao.clear()
            val items = summitList.asAssociationDatabaseModel()
            val aids = dao.insertAssociation(*items.toTypedArray())
            val associationToId = items.map { it.code }.zip(aids).toMap()
            val idToAssociation = associationToId.entries.associate { (k, v) -> v to k }
            val regions = summitList.asRegionDatabaseModel(associationToId)
            val rids = dao.insertRegion(*regions.toTypedArray())
            val regionToId = regions.map {
                "${idToAssociation[it.associationId]}/${it.code}"
            }.zip(rids).toMap()
            val summits = summitList.asSummitDatabaseModel(regionToId)
            dao.insertSummit(*summits.toTypedArray())
        }
    }
}
