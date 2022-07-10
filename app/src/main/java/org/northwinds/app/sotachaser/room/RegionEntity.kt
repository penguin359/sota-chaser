package org.northwinds.app.sotachaser.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import org.northwinds.app.sotachaser.domain.models.Region


@Entity(
    tableName = "region",
    foreignKeys = [
        ForeignKey(entity = AssociationEntity::class,
            parentColumns = ["id"],
            childColumns = ["association_id"],
            onDelete = ForeignKey.CASCADE
        )
    ])
data class RegionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo(name = "association_id") val associationId: Long,
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

fun List<RegionEntity>.asDomainModel(): List<Region> = map {
    Region(
        id = it.id,
        associationId = it.associationId,
        code = it.code,
        name = it.name,
    )
}
