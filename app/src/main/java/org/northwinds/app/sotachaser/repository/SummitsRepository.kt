package org.northwinds.app.sotachaser.repository

import org.northwinds.app.sotachaser.SummitList
import org.northwinds.app.sotachaser.domain.models.Association
import org.northwinds.app.sotachaser.domain.models.Region
import org.northwinds.app.sotachaser.domain.models.Summit
import org.northwinds.app.sotachaser.room.SummitDao
import org.northwinds.app.sotachaser.util.asAssociationDatabaseModel
import org.northwinds.app.sotachaser.util.asRegionDatabaseModel
import org.northwinds.app.sotachaser.util.asSummitDatabaseModel

interface SummitsRepository {
    fun getAssociations(): List<Association>
    fun getRegionsInAssociationName(associationId: String): List<Region>
    fun getSummits(associationId: String,  region: String): List<Summit>

    //fun loadDatabase(dao: SummitDao, summitList: SummitList)
}
