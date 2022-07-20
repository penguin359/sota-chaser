package org.northwinds.app.sotachaser.room.model

import androidx.room.*
import org.northwinds.app.sotachaser.domain.models.Region
import org.northwinds.app.sotachaser.network.model.RegionDto
import org.northwinds.app.sotachaser.room.SummitDao


@Entity(
    tableName = "region",
    foreignKeys = [
        ForeignKey(entity = AssociationEntity::class,
            parentColumns = ["id"],
            childColumns = ["association_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["association_id", "code"], unique = true)
    ]
)
data class RegionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo(name = "association_id", index = true) val associationId: Long,
    @ColumnInfo(name = "code") val code: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "manager") val manager: String? = null,
    @ColumnInfo(name = "manager_callsign") val managerCallsign: String? = null,
    @ColumnInfo(name = "notes") val notes: String? = null,
    @ColumnInfo(name = "summits_count") val summitsCount: Int? = null,
    @ColumnInfo(name = "max_lat") val maxLat: Double? = null,
    @ColumnInfo(name = "max_long") val maxLong: Double? = null,
    @ColumnInfo(name = "min_lat") val minLat: Double? = null,
    @ColumnInfo(name = "min_long") val minLong: Double? = null,
    @ColumnInfo(name = "updated_at") val updatedAt: Long? = null,
)

fun RegionDto.asDatabaseModel(dao: SummitDao): RegionEntity {
    val association = associationCode?.let { dao.getAssociationByCode(it) }
    val old = association?.let { aid -> regionCode?.let { dao.getRegionByCode(aid.id, it) } }
    return RegionEntity(
        id = old?.id ?: 0,
        associationId = association?.id ?: 0,
        code = regionCode ?: "",
        name = regionName ?: "",
        manager = manager,
        managerCallsign = regionManagerCallsign,
        notes = notes,
        //activeFrom = activeFrom,
        summitsCount = summits,
        maxLat = maxLat,
        maxLong = maxLong,
        minLat = minLat,
        minLong = minLong,
    )
}

fun RegionEntity.asDomainModel(): Region = Region(
    id = id,
    associationId = associationId,
    code = code,
    name = name,
    manager = manager,
    managerCallsign = managerCallsign,
    notes = notes,
    summitsCount = summitsCount ?: 0,
    maxLat = maxLat,
    maxLong = maxLong,
    minLat = minLat,
    minLong = minLong,
)

fun List<RegionEntity>.asDomainModel(): List<Region> = map {
    it.asDomainModel()
}
