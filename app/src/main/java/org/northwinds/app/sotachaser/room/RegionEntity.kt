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
    @ColumnInfo(name = "name") val name: String
)

fun List<RegionEntity>.asDomainModel(): List<Region> = map {
    Region(
        id = it.id,
        associationId = it.associationId,
        code = it.code,
        name = it.code,
    )
}
