package org.northwinds.app.sotachaser.repository

import android.content.Context
import androidx.core.content.edit
import androidx.lifecycle.LiveData
import org.northwinds.app.sotachaser.R
import org.northwinds.app.sotachaser.SummitList
import org.northwinds.app.sotachaser.domain.models.Association
import org.northwinds.app.sotachaser.domain.models.Region
import org.northwinds.app.sotachaser.domain.models.Summit
import org.northwinds.app.sotachaser.room.SummitDao
import org.northwinds.app.sotachaser.util.asAssociationDatabaseModel
import org.northwinds.app.sotachaser.util.asRegionDatabaseModel
import org.northwinds.app.sotachaser.util.asSummitDatabaseModel

interface SummitsRepository {
    suspend fun checkForRefresh()
    suspend fun refreshAssociations()
    suspend fun updateAssociation(code: String)
    suspend fun updateRegion(association: String, region: String)

    fun getAssociations(): LiveData<List<Association>>
    fun getAssociationByCode(code: String): LiveData<Association>
    fun getRegionsInAssociationName(associationId: String): LiveData<List<Region>>
    //fun getRegionByCode(code: String): LiveData<Region>
    fun getRegionByCode(association: Association, code: String): LiveData<Region>
    fun getSummits(associationId: String,  region: String): LiveData<List<Summit>>
    //fun getSummitByCode(code: String): LiveData<Summit>

    //fun loadDatabase(dao: SummitDao, summitList: SummitList)
}
