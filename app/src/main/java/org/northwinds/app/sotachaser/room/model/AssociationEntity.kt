package org.northwinds.app.sotachaser.room.model

import androidx.room.*
import org.northwinds.app.sotachaser.domain.models.Association
import org.northwinds.app.sotachaser.network.model.AssociationWithRegionsDto
import org.northwinds.app.sotachaser.room.SummitDao

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
    @ColumnInfo(name = "favorite", defaultValue = "FALSE") val favorite: Boolean = false,
    @ColumnInfo(name = "updated_at") val updatedAt: Long? = null,
)

fun AssociationWithRegionsDto.asDatabaseModel(dao: SummitDao): AssociationEntity {
    val old = associationCode?.let { dao.getAssociationByCode(it) }
    return AssociationEntity(
        id = old?.id ?: 0,
        code = associationCode ?: "",
        name = associationName ?: "",
        manager = manager,
        managerCallsign = associationManagerCallsign,
        activeFrom = activeFrom,
        dxcc = dxcc,
        regionsCount = regionsCount,
        summitsCount = summitsCount,
        maxLat = maxLat,
        maxLong = maxLong,
        minLat = minLat,
        minLong = minLong,
    )
}

fun AssociationEntity.asDomainModel() = Association(
    id = id,
    code = code,
    name = name,
    manager = manager,
    managerCallsign = managerCallsign,
    activeFrom = activeFrom,
    dxcc = dxcc,
    regionsCount = regionsCount ?: 0,
    summitsCount = summitsCount ?: 0,
    maxLat = maxLat,
    maxLong = maxLong,
    minLat = minLat,
    minLong = minLong,
)

fun List<AssociationEntity>.asDomainModel() = map {
    it.asDomainModel()
}

data class AssociationCsvEntity(
    val id: Long,
    @ColumnInfo(name = "code") val code: String,
    @ColumnInfo(name = "name") val name: String,
)

//fun Entry<String, String>.asAssociationDatabaseModel(dao: SummitDao): AssociationCsvEntity {
//    val old = dao.getAssociationByCode(first)
//    return AssociationCsvEntity(
//        id = old?.id ?: 0,
//        code = first,
//        name = second,
//    )
//}
//
//fun Map<String, String>.asAssociationDatabaseModel(dao: SummitDao) = map {
//    it.asAssociationDatabaseModel(dao)
//}
