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
    suspend fun updateAssociation(code: String)

    fun getAssociations(): LiveData<List<Association>>
    fun getAssociationByCode(code: String): LiveData<Association>
    fun getRegionsInAssociationName(associationId: String): LiveData<List<Region>>
    fun getSummits(associationId: String,  region: String): LiveData<List<Summit>>

    //fun loadDatabase(dao: SummitDao, summitList: SummitList)
}
