package org.northwinds.app.sotachaser.room.model

import androidx.room.*
import org.northwinds.app.sotachaser.domain.models.Region
import org.northwinds.app.sotachaser.domain.models.Summit
import org.northwinds.app.sotachaser.room.SummitDao


@Entity(
    tableName = "summit",
    foreignKeys = [
        ForeignKey(entity = RegionEntity::class,
            parentColumns = ["id"],
            childColumns = ["region_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["region_id", "code"], unique = true)
    ]
)
data class SummitEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo(name = "region_id", index = true) val regionId: Long,
    @ColumnInfo(name = "code") val code: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "alt_m") val altM: Int,
    @ColumnInfo(name = "alt_ft") val altFt: Int,
    @ColumnInfo(name = "grid_ref1") val gridRef1: String?,
    @ColumnInfo(name = "grid_ref2") val gridRef2: String?,
    @ColumnInfo(name = "longitude") val longitude: Double,
    @ColumnInfo(name = "latitude") val latitude: Double,
    @ColumnInfo(name = "points") val points: Int,
    @ColumnInfo(name = "bonus_points") val bonusPoints: Int,
    @ColumnInfo(name = "valid_from") val validFrom: String?,
    @ColumnInfo(name = "valid_to") val validTo: String?,
    @ColumnInfo(name = "activation_count") val activationCount: Int,
    @ColumnInfo(name = "activation_date") val activationDate: String?,
    @ColumnInfo(name = "activation_call") val activationCall: String?,
    // TODO Implement locator
    @ColumnInfo(name = "my_activations") val myActivations: Int? = null,
    @ColumnInfo(name = "my_chases") val myChases: Int? = null,
    @ColumnInfo(name = "notes") val notes: String? = null,
    @ColumnInfo(name = "valid") val valid: Boolean? = null,
    @ColumnInfo(name = "restriction_mask") val restrictionMask: Boolean? = null,
    @ColumnInfo(name = "updated_at") val updatedAt: Long? = null,
)

fun org.northwinds.app.sotachaser.network.model.SummitEntity.asDatabaseModel(dao: SummitDao): SummitEntity {
    val a = (summitCode ?: "").split("/")[0]
    val r = (summitCode ?: "").split("/")[1].split("-")[0]
    val s = (summitCode ?: "").split("-")[1]
    val association = a?.let { dao.getAssociationByCode(it) }
    val region = association?.let { aid -> r?.let { dao.getRegionByCode(aid.id, it) } }
    val old = region?.let { rid -> s?.let { dao.getSummitByCode(rid.id, it) } }
    //val code = (shortCode?: "").split("-")[1]
    return SummitEntity(
        id = old?.id ?: 0,
        regionId = region?.id ?: 0,
        code = s,
        name = name ?: "",
        altM = altM,
        altFt = altFt,
        gridRef1 = gridRef1,
        gridRef2 = gridRef2,
        longitude = longitude,
        latitude = latitude,
        points = points,
        bonusPoints = 0,  // FIXME Api doesn't provide it???
        validFrom = validFrom,
        validTo = validTo,
        activationCount = activationCount ?: 0,
        activationDate = activationDate,
        activationCall = activationCall,
        myActivations = myActivations,
        myChases = myChases,
        notes = notes,
        valid = valid,
        restrictionMask = restrictionMask,
        //updatedAt = updatedAt,
    )
}

fun List<SummitEntity>.asDomainModel(): List<Summit> = map {
    Summit(
        id = it.id,
        regionId = it.regionId,
        code = it.code,
        name = it.name,
        altM = it.altM,
        altFt = it.altFt,
        gridRef1 = it.gridRef1,
        gridRef2 = it.gridRef2,
        longitude = it.longitude,
        latitude = it.latitude,
        points = it.points,
        bonusPoints = it.bonusPoints,
        validFrom = it.validFrom,
        validTo = it.validTo,
        activationCount = it.activationCount,
        activationDate = it.activationDate,
        activationCall = it.activationCall,
    )
}
