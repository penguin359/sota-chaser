package org.northwinds.app.sotachaser.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import org.northwinds.app.sotachaser.domain.models.Region
import org.northwinds.app.sotachaser.domain.models.Summit


@Entity(
    tableName = "summit",
    foreignKeys = [
        ForeignKey(entity = RegionEntity::class,
            parentColumns = ["id"],
            childColumns = ["region_id"],
            onDelete = ForeignKey.CASCADE
        )
    ])
data class SummitEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo(name = "region_id") val regionId: Long,
    @ColumnInfo(name = "code") val code: String,
    @ColumnInfo(name = "name") val name: String,
    val altM: Int,
    val altFt: Int,
    val gridRef1: String?,
    val gridRef2: String?,
    val longitude: Double,
    val latitude: Double,
    val points: Int,
    val bonusPoints: Int,
    val validFrom: String?,
    val validTo: String?,
    val activationCount: Int,
    val activationDate: String?,
    val activationCall: String?,
)

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
