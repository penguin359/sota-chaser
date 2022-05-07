package org.northwinds.app.sotachaser.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.MapInfo
import androidx.room.Query

@Dao
interface SummitDao {
    @Query("SELECT * FROM association")
    fun getAssociations(): List<Association>

    @Query("SELECT * FROM association WHERE code = :code")
    fun getAssociationByCode(code: String): Association?

    @Insert
    fun insertAssociation(vararg users: Association): List<Long>

    @Insert
    fun insertRegion(vararg users: Region): List<Long>

    @Insert
    fun insertSummit(vararg users: Summit): List<Long>

    @Query("SELECT * FROM region WHERE association_id = :associationId AND code = :code")
    fun getRegionByCode(associationId: Long, code: String): Region?

    @Query("SELECT * FROM summit WHERE region_id = :regionId AND code = :code")
    fun getSummitByCode(regionId: Long, code: String): Summit?

    @Query("SELECT * FROM region WHERE association_id = :associationId")
    fun getRegionsInAssociation(associationId: Long): List<Region>

    @Query("SELECT region.* FROM region JOIN association ON (region.association_id = association.id) WHERE association.code = :associationId")
    fun getRegionsInAssociationName(associationId: String): List<Region>

    @Query("SELECT * FROM summit WHERE region_id = :regionId")
    fun getSummitsInRegion(regionId: Long): List<Summit>

    //@MapInfo(keyColumn = "summitCode")
    @Query("SELECT summit.*, association.code || '/' || region.code || '-' || summit.code AS code FROM summit JOIN region ON (summit.region_id = region.id) JOIN association ON (region.association_id = association.id) WHERE association.code = :associationId AND region.code = :region")
    fun getSummits(associationId: String,  region: String): List<Summit>

    @Query("DELETE FROM association")
    fun clear()
}
    //@Query("SELECT * FROM user WHERE first_name LIKE :first AND " +
    //        "last_name LIKE :last LIMIT 1")
    //fun findByName(first: String, last: String): User
    //
    //@Delete
    //fun delete(user: User)
