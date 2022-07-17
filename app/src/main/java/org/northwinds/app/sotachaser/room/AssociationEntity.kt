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
    @ColumnInfo(name = "name") val name: String
)

fun List<AssociationEntity>.asDomainModel(): List<Association> = map {
    Association(
        id = it.id,
        code = it.code,
        name = it.code,
    )
}
