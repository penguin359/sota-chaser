package org.northwinds.app.sotachaser.repository

import android.app.Application
import android.content.Context
import androidx.core.content.edit
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.northwinds.app.sotachaser.R
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
    private var hasRefreshed = false

    override suspend fun checkForRefresh() {
        if(hasRefreshed)
            return
        withContext(Dispatchers.IO) {
            val prefs = context.getSharedPreferences("database", Context.MODE_PRIVATE)
            if(!prefs.getBoolean("database_loaded", false)) {
                val input = context.resources.openRawResource(R.raw.summitslist)
                val list = SummitList(input)

                SummitsRepositoryImpl.loadDatabase(dao, list)
                prefs.edit { putBoolean("database_loaded", true) }
                hasRefreshed = true
            }
        }
    }

    override fun getAssociations(): LiveData<List<Association>> {
        return Transformations.map(dao.getAssociations()) {
            it.asDomainModel()
        }
    }

    override fun getRegionsInAssociationName(associationId: String): LiveData<List<Region>> {
        return Transformations.map(dao.getRegionsInAssociationName(associationId)) {
            it.asDomainModel()
        }
    }

    override fun getSummits(associationId: String, region: String): LiveData<List<Summit>> {
        return Transformations.map(dao.getSummits(associationId, region)) {
            it.asDomainModel()
        }
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
