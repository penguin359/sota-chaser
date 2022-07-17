package org.northwinds.app.sotachaser.room

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import org.northwinds.app.sotachaser.room.model.AssociationEntity
import org.northwinds.app.sotachaser.room.model.RegionEntity
import org.northwinds.app.sotachaser.room.model.SummitEntity

@Dao
interface SummitDao {
    @Query("SELECT * FROM association ORDER BY code")
    fun getAssociations(): LiveData<List<AssociationEntity>>

    @Query("SELECT * FROM association WHERE code = :code")
    fun getAssociationByCode(code: String): AssociationEntity?

    @Query("SELECT * FROM association WHERE code = :code")
    fun getAssociationByCode2(code: String): LiveData<AssociationEntity?>

    @Insert
    fun insertAssociation(vararg users: AssociationEntity): List<Long>

    @Update
    //@Insert(onConflict = OnConflictStrategy.REPLACE)
    fun updateAssociation(vararg users: AssociationEntity): Int

    fun upsertAssociation(vararg users: AssociationEntity): List<Long> {
        val ids = users.map {
            val old = getAssociationByCode(it.code)
            if(old != null) {
                val record = it.copy(id = old.id)
                updateAssociation(record)
                old.id
            } else {
                insertAssociation(it)[0]
            }
        }
        return ids
    }

    @Insert
    fun insertRegion(vararg users: RegionEntity): List<Long>

    //@Update
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun updateRegion(vararg users: RegionEntity)

    @Insert
    fun insertSummit(vararg users: SummitEntity): List<Long>

    @Query("SELECT * FROM region WHERE association_id = :associationId AND code = :code")
    fun getRegionByCode(associationId: Long, code: String): RegionEntity?

    @Query("SELECT * FROM region WHERE association_id = :associationId AND code = :code")
    fun getRegionByCode2(associationId: Long, code: String): LiveData<RegionEntity?>

    @Query("SELECT * FROM summit WHERE region_id = :regionId AND code = :code")
    fun getSummitByCode(regionId: Long, code: String): SummitEntity?

    @Query("SELECT * FROM region WHERE association_id = :associationId")
    fun getRegionsInAssociation(associationId: Long): List<RegionEntity>

    @Query("SELECT region.* FROM region JOIN association ON (region.association_id = association.id) WHERE association.code = :associationId ORDER BY code")
    fun getRegionsInAssociationName(associationId: String): LiveData<List<RegionEntity>>

    @Query("SELECT * FROM summit WHERE region_id = :regionId ORDER BY code")
    fun getSummitsInRegion(regionId: Long): List<SummitEntity>

    //@MapInfo(keyColumn = "summitCode")
    @Query("SELECT summit.*, association.code || '/' || region.code || '-' || summit.code AS code FROM summit JOIN region ON (summit.region_id = region.id) JOIN association ON (region.association_id = association.id) WHERE association.code = :associationId AND region.code = :region ORDER BY code")
    fun getSummits(associationId: String,  region: String): LiveData<List<SummitEntity>>

    @Query("DELETE FROM association")
    fun clear()
}
    //@Query("SELECT * FROM user WHERE first_name LIKE :first AND " +
    //        "last_name LIKE :last LIMIT 1")
    //fun findByName(first: String, last: String): User
    //
    //@Delete
    //fun delete(user: User)
