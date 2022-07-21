package org.northwinds.app.sotachaser.room

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import org.northwinds.app.sotachaser.room.model.*

@Dao
interface SummitDao {
    @Insert
    fun insertAssociation(vararg users: AssociationEntity): List<Long>

    @Update
    fun updateAssociation(vararg users: AssociationEntity): Int

    fun upsertAssociation(vararg users: AssociationEntity): List<Long> {
        val ids = users.map {
            //val old = getAssociationByCode(it.code)
            //if(old != null) {
            //    val record = it.copy(id = old.id)
            //    updateAssociation(record)
            //    old.id
            //} else {
            //    insertAssociation(it)[0]
            //}
            if(it.id != 0L) {
                updateAssociation(it)
                it.id
            } else {
                insertAssociation(it)[0]
            }
        }
        return ids
    }

    @Query("SELECT * FROM association ORDER BY code")
    fun getAssociations(): LiveData<List<AssociationEntity>>

    @Query("SELECT * FROM association WHERE code = :code")
    fun getAssociationByCode(code: String): AssociationEntity?

    @Query("SELECT * FROM association WHERE code = :code")
    fun getAssociationByCode2(code: String): LiveData<AssociationEntity?>

    @Insert
    fun insertRegion(vararg users: RegionEntity): List<Long>

    @Update
    fun updateRegion(vararg users: RegionEntity): Int

    fun upsertRegion(vararg users: RegionEntity): List<Long> {
        val ids = users.map {
            if(it.id != 0L) {
                updateRegion(it)
                it.id
            } else {
                insertRegion(it)[0]
            }
        }
        return ids
    }

    @Query("SELECT * FROM region WHERE association_id = :associationId AND code = :code")
    fun getRegionByCode(associationId: Long, code: String): RegionEntity?

    @Query("SELECT * FROM region WHERE association_id = :associationId AND code = :code")
    fun getRegionByCode2(associationId: Long, code: String): LiveData<RegionEntity?>

    @Query("SELECT * FROM region WHERE association_id = :associationId")
    fun getRegionsInAssociation(associationId: Long): List<RegionEntity>

    @Query("SELECT region.* FROM region JOIN association ON (region.association_id = association.id) WHERE association.code = :associationId ORDER BY code")
    fun getRegionsInAssociationName(associationId: String): LiveData<List<RegionEntity>>

    @Insert
    fun insertSummit(vararg users: SummitEntity): List<Long>

    @Update
    fun updateSummit(vararg users: SummitEntity): Int

    fun upsertSummit(vararg users: SummitEntity): List<Long> {
        val ids = users.map {
            if(it.id != 0L) {
                updateSummit(it)
                it.id
            } else {
                insertSummit(it)[0]
            }
        }
        return ids
    }


    @Query("SELECT * FROM summit WHERE region_id = :regionId AND code = :code")
    fun getSummitByCode(regionId: Long, code: String): SummitEntity?

    @Query("SELECT * FROM summit WHERE region_id = :regionId ORDER BY code")
    fun getSummitsInRegion(regionId: Long): List<SummitEntity>

    //@MapInfo(keyColumn = "summitCode")
    @Query("SELECT summit.*, association.code || '/' || region.code || '-' || summit.code AS code FROM summit JOIN region ON (summit.region_id = region.id) JOIN association ON (region.association_id = association.id) WHERE association.code = :associationId AND region.code = :region ORDER BY code")
    fun getSummits(associationId: String,  region: String): LiveData<List<SummitEntity>>

    @Insert
    fun insertGpxTrack(vararg users: GpxTrackEntity): List<Long>

    @Query("SELECT * FROM gpx_track WHERE summit_id = :summitId ORDER BY callsign")
    fun getGpxTracks(summitId: Long): LiveData<List<GpxTrackEntity>>

    @Insert
    fun insertGpxPoint(vararg users: GpxPointEntity): List<Long>

    @Query("SELECT * FROM gpx_point WHERE gpx_track_id = :gpxTrackId ORDER BY 'index'")
    fun getGpxPoints(gpxTrackId: Long): LiveData<List<GpxPointEntity>>

    @Query("DELETE FROM association")
    fun clear()
}
    //@Query("SELECT * FROM user WHERE first_name LIKE :first AND " +
    //        "last_name LIKE :last LIMIT 1")
    //fun findByName(first: String, last: String): User
    //
    //@Delete
    //fun delete(user: User)
