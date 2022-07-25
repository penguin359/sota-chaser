package org.northwinds.app.sotachaser.room.model

import androidx.room.*
import org.northwinds.app.sotachaser.domain.models.Region
import org.northwinds.app.sotachaser.domain.models.Summit
import org.northwinds.app.sotachaser.network.model.SummitDto
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
    @ColumnInfo(name = "locator") val locator: String?,
    @ColumnInfo(name = "points") val points: Int,
    @ColumnInfo(name = "bonus_points", defaultValue = "0") val bonusPoints: Int,
    @ColumnInfo(name = "valid_from") val validFrom: String?,
    @ColumnInfo(name = "valid_to") val validTo: String?,
    @ColumnInfo(name = "activation_count", defaultValue = "0") val activationCount: Int,
    @ColumnInfo(name = "activation_date") val activationDate: String?,
    @ColumnInfo(name = "activation_call") val activationCall: String?,
    @ColumnInfo(name = "my_activations") val myActivations: Int? = null,
    @ColumnInfo(name = "my_chases") val myChases: Int? = null,
    @ColumnInfo(name = "notes") val notes: String? = null,
    @ColumnInfo(name = "valid") val valid: Boolean? = null,
    @ColumnInfo(name = "restriction_mask") val restrictionMask: Boolean? = null,
    @ColumnInfo(name = "updated_at") val updatedAt: Long? = null,
)

//fun SummitDto.asDatabaseModel(dao: SummitDao): SummitEntity {
//    val a = (summitCode ?: "").split("/")[0]
//    val r = (summitCode ?: "").split("/")[1].split("-")[0]
//    val s = (summitCode ?: "").split("-")[1]
//    val association = a?.let { dao.getAssociationByCode(it) }
//    val region = association?.let { aid -> r?.let { dao.getRegionByCode(aid.id, it) } }
//    val old = region?.let { rid -> s?.let { dao.getSummitByCode(rid.id, it) } }
//    //val code = (shortCode?: "").split("-")[1]
//    return SummitEntity(
//        id = old?.id ?: 0,
//        regionId = region?.id ?: 0,
//        code = s,
//        name = name ?: "",
//        altM = altM,
//        altFt = altFt,
//        gridRef1 = gridRef1,
//        gridRef2 = gridRef2,
//        longitude = longitude,
//        latitude = latitude,
//        points = points,
//        bonusPoints = 0,  // FIXME Api doesn't provide it???
//        validFrom = validFrom,
//        validTo = validTo,
//        activationCount = activationCount ?: 0,
//        activationDate = activationDate,
//        activationCall = activationCall,
//        myActivations = myActivations,
//        myChases = myChases,
//        notes = notes,
//        valid = valid,
//        restrictionMask = restrictionMask,
//        //updatedAt = updatedAt,
//    )
//}

fun SummitEntity.asDomainModel() = Summit(
    id = id,
    regionId = regionId,
    code = code,
    name = name,
    altM = altM,
    altFt = altFt,
    gridRef1 = gridRef1,
    gridRef2 = gridRef2,
    longitude = longitude,
    latitude = latitude,
    points = points,
    bonusPoints = bonusPoints,
    validFrom = validFrom,
    validTo = validTo,
    activationCount = activationCount,
    activationDate = activationDate,
    activationCall = activationCall,
    valid = valid,
)

fun List<SummitEntity>.asDomainModel() = map {
    it.asDomainModel()
}

data class SummitCsvEntity(
    val id: Long,
    @ColumnInfo(name = "region_id") val regionId: Long,
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
)

data class SummitJsonEntity(
    val id: Long,
    @ColumnInfo(name = "region_id") val regionId: Long,
    @ColumnInfo(name = "code") val code: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "alt_m") val altM: Int,
    @ColumnInfo(name = "alt_ft") val altFt: Int,
    @ColumnInfo(name = "grid_ref1") val gridRef1: String?,
    @ColumnInfo(name = "grid_ref2") val gridRef2: String?,
    @ColumnInfo(name = "longitude") val longitude: Double,
    @ColumnInfo(name = "latitude") val latitude: Double,
    @ColumnInfo(name = "points") val points: Int,
    @ColumnInfo(name = "valid_from") val validFrom: String?,
    @ColumnInfo(name = "valid_to") val validTo: String?,
    @ColumnInfo(name = "activation_count") val activationCount: Int,
    @ColumnInfo(name = "activation_date") val activationDate: String?,
    @ColumnInfo(name = "activation_call") val activationCall: String?,
    @ColumnInfo(name = "my_activations") val myActivations: Int?,
    @ColumnInfo(name = "my_chases") val myChases: Int?,
    @ColumnInfo(name = "notes") val notes: String?,
)

fun SummitDto.asDatabaseModel(dao: SummitDao): SummitJsonEntity {
    val a = (summitCode ?: "").split("/")[0]
    val r = (summitCode ?: "").split("/")[1].split("-")[0]
    val s = (summitCode ?: "").split("-")[1]
    val association = a.let { dao.getAssociationByCode(it) }
    val region = association?.let { aid -> dao.getRegionByCode(aid.id, r) }
    val old = region?.let { rid -> dao.getSummitByCode(rid.id, s) }
    //val code = (shortCode?: "").split("-")[1]
    return SummitJsonEntity(
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
        validFrom = validFrom,
        validTo = validTo,
        activationCount = activationCount ?: 0,
        activationDate = activationDate,
        activationCall = activationCall,
        myActivations = myActivations,
        myChases = myChases,
        notes = notes,
    )
}

data class SummitSingleJsonEntity(
    val id: Long,
    @ColumnInfo(name = "region_id") val regionId: Long,
    @ColumnInfo(name = "code") val code: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "alt_m") val altM: Int,
    @ColumnInfo(name = "alt_ft") val altFt: Int,
    @ColumnInfo(name = "grid_ref1") val gridRef1: String?,
    @ColumnInfo(name = "grid_ref2") val gridRef2: String?,
    @ColumnInfo(name = "longitude") val longitude: Double,
    @ColumnInfo(name = "latitude") val latitude: Double,
    @ColumnInfo(name = "points") val points: Int,
    @ColumnInfo(name = "valid_from") val validFrom: String?,
    @ColumnInfo(name = "valid_to") val validTo: String?,
    @ColumnInfo(name = "locator") val locator: String?,
    @ColumnInfo(name = "notes") val notes: String?,
    @ColumnInfo(name = "valid") val valid: Boolean?,
    @ColumnInfo(name = "restriction_mask") val restrictionMask: Boolean?,
)

fun SummitDto.asSummitDatabaseModel(dao: SummitDao): SummitSingleJsonEntity {
    val a = (summitCode ?: "").split("/")[0]
    val r = (summitCode ?: "").split("/")[1].split("-")[0]
    val s = (summitCode ?: "").split("-")[1]
    val association = a.let { dao.getAssociationByCode(it) }
    val region = association?.let { aid -> dao.getRegionByCode(aid.id, r) }
    val old = region?.let { rid -> dao.getSummitByCode(rid.id, s) }
    return SummitSingleJsonEntity(
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
        validFrom = validFrom,
        validTo = validTo,
        locator = locator,
        notes = notes,
        valid = valid,
        restrictionMask = restrictionMask,
    )
}
