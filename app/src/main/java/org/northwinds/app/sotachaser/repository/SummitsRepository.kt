package org.northwinds.app.sotachaser.repository

import android.content.Context
import androidx.core.content.edit
import androidx.lifecycle.LiveData
import org.northwinds.app.sotachaser.R
import org.northwinds.app.sotachaser.SummitList
import org.northwinds.app.sotachaser.domain.models.*
import org.northwinds.app.sotachaser.room.SummitDao
import org.northwinds.app.sotachaser.room.model.GpxTrackEntity
import org.northwinds.app.sotachaser.util.asAssociationDatabaseModel
import org.northwinds.app.sotachaser.util.asRegionDatabaseModel
import org.northwinds.app.sotachaser.util.asSummitDatabaseModel

interface SummitsRepository {
    suspend fun refreshAssociations()
    suspend fun updateAssociation(code: String)
    suspend fun updateRegion(association: String, region: String)
    suspend fun updateSummit(association: String, region: String, summit: String)
    suspend fun updateGpxTracks(summit: Summit)

    fun getAssociations(): LiveData<List<Association>>
    fun getAssociationByCode(code: String): LiveData<Association?>
    fun getRegionsInAssociationName(associationId: String): LiveData<List<Region>>
    fun getRegionByCode(association: Association, code: String): LiveData<Region?>
    fun getSummits(associationId: String,  region: String): LiveData<List<Summit>>
    fun getSummitByCode(association: String, region: String, summit: String): LiveData<Summit?>
    fun getGpxTracks(summit: Summit): LiveData<List<GpxTrack>>
    fun getGpxPoints(gpxTrack: GpxTrack): LiveData<List<GpxPoint>>
}
