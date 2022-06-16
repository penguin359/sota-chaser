package org.northwinds.app.sotachaser.room

import androidx.room.*
import androidx.room.ForeignKey.CASCADE
import org.northwinds.app.sotachaser.domain.models.Association
import org.northwinds.app.sotachaser.domain.models.Region

@Entity(
    tableName = "association",
    indices = [
        Index(value = ["code"], unique = true)
    ])
data class AssociationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo(name = "code") val code: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "manager") val manager: String? = null,
    @ColumnInfo(name = "manager_callsign") val managerCallsign: String? = null,
    @ColumnInfo(name = "active_from") val activeFrom: String? = null,
    @ColumnInfo(name = "dxcc") val dxcc: String? = null,
    @ColumnInfo(name = "regions_count") val regionsCount: Int? = null,
    @ColumnInfo(name = "summits_count") val summitsCount: Int? = null,
    @ColumnInfo(name = "max_lat") val maxLat: Double? = null,
    @ColumnInfo(name = "max_long") val maxLong: Double? = null,
    @ColumnInfo(name = "min_lat") val minLat: Double? = null,
    @ColumnInfo(name = "min_long") val minLong: Double? = null,
    @ColumnInfo(name = "updated_at") val updatedAt: Long? = null,
)

fun List<AssociationEntity>.asDomainModel(): List<Association> = map {
    Association(
        id = it.id,
        code = it.code,
        name = it.code,
    )
}
