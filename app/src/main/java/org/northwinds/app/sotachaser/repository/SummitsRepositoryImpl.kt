package org.northwinds.app.sotachaser.repository

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.core.content.edit
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import org.northwinds.app.sotachaser.SummitList
import org.northwinds.app.sotachaser.domain.models.Association
import org.northwinds.app.sotachaser.domain.models.Region
import org.northwinds.app.sotachaser.domain.models.Summit
import org.northwinds.app.sotachaser.network.SotaApiService
import org.northwinds.app.sotachaser.network.SummitData
import org.northwinds.app.sotachaser.room.*
import org.northwinds.app.sotachaser.room.model.asDatabaseModel
import org.northwinds.app.sotachaser.room.model.asDomainModel
import org.northwinds.app.sotachaser.util.asAssociationDatabaseModel
import org.northwinds.app.sotachaser.util.asRegionDatabaseModel
import org.northwinds.app.sotachaser.util.asSummitDatabaseModel
import java.io.IOException
import java.util.concurrent.ExecutorService
import javax.inject.Inject

class SummitsRepositoryImpl @Inject constructor(private val context: Application, private val dao: SummitDao, private val client: OkHttpClient, private val api: SotaApiService, private val executor: ExecutorService) : SummitsRepository {
    private var hasRefreshed = false

    override suspend fun checkForRefresh() {
        if(hasRefreshed)
            return
        withContext(executor.asCoroutineDispatcher()) {
            val prefs = context.getSharedPreferences("database", Context.MODE_PRIVATE)
            if(!prefs.getBoolean("database_loaded", false)) {
                Log.v(TAG, "Downloading summit list")
                val list = try {
                    SummitData(client).getSummitData()
                } catch (ex: IOException) {
                    // TODO Can't Toast on non-UI thread
                    //Toast.makeText(context, "Network error downloading summits", Toast.LENGTH_LONG).show()
                    Log.e(TAG, "Network Error downloading summits", ex)
                    return@withContext
                } catch (ex: IllegalStateException) {
                    Log.e(TAG, "Illegal state while downloading summits", ex)
                    return@withContext
                }

                Log.v(TAG, "Loading database with summit list")
                synchronized(this) {
                    loadDatabase(dao, list)
                }
                prefs.edit { putBoolean("database_loaded", true) }
                hasRefreshed = true
            }
        }
    }

    override suspend fun refreshAssociations() {
        withContext(executor.asCoroutineDispatcher()) {
            val results = api.getAssociations()
            synchronized(this) {
                results.forEach { associationEntity ->
                    dao.upsertAssociation(associationEntity.asDatabaseModel(dao))
                }
            }
        }
    }

    override suspend fun updateAssociation(code: String) {
        withContext(executor.asCoroutineDispatcher()) {
            val result = api.getAssociation(code)
            synchronized(this) {
                dao.upsertAssociation(result.asDatabaseModel(dao))
                result.regions?.forEach {
                    dao.upsertRegion(it.asDatabaseModel(dao))
                }
            }
        }
    }

    override suspend fun updateRegion(association: String, region: String) {
        withContext(executor.asCoroutineDispatcher()) {
            val result = api.getRegion(association, region)
            synchronized(this) {
                dao.upsertRegion(result.region.asDatabaseModel(dao))
                result.summits?.forEach {
                    dao.upsertSummit(it.asDatabaseModel(dao))
                }
            }
        }
    }

    override fun getAssociations(): LiveData<List<Association>> {
        return Transformations.map(dao.getAssociations()) {
                it.asDomainModel()
        }
    }

    override fun getAssociationByCode(code: String): LiveData<Association?> {
        return Transformations.map(dao.getAssociationByCode2(code)) {
            it?.asDomainModel()
        }
    }

    override fun getRegionsInAssociationName(associationId: String): LiveData<List<Region>> {
        return Transformations.map(dao.getRegionsInAssociationName(associationId)) {
            it.asDomainModel()
        }
    }

    override fun getRegionByCode(association: Association, code: String): LiveData<Region?> {
        return Transformations.map(dao.getRegionByCode2(association.id, code)) {
            it?.asDomainModel()
        }
    }

    override fun getSummits(associationId: String, region: String): LiveData<List<Summit>> {
        return Transformations.map(dao.getSummits(associationId, region)) {
            it.asDomainModel()
        }
    }

    companion object {
        const val TAG = "SOTAChaser-SummitRepository"
        fun loadDatabase(dao: SummitDao, summitList: SummitList) {
            dao.clear()
            val items = summitList.asAssociationDatabaseModel(dao)
            val aids = dao.upsertAssociation(*items.toTypedArray())
            val associationToId = items.map { it.code }.zip(aids).toMap()
            val idToAssociation = associationToId.entries.associate { (k, v) -> v to k }
            val regions = summitList.asRegionDatabaseModel(dao, associationToId)
            val rids = dao.upsertRegion(*regions.toTypedArray())
            val regionToId = regions.map {
                "${idToAssociation[it.associationId]}/${it.code}"
            }.zip(rids).toMap()
            val summits = summitList.asSummitDatabaseModel(dao, regionToId)
            dao.upsertSummit(*summits.toTypedArray())
        }
    }
}
