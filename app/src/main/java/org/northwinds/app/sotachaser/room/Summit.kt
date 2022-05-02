package org.northwinds.app.sotachaser.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey


@Entity(foreignKeys = [
        ForeignKey(entity = Region::class,
            parentColumns = ["id"],
            childColumns = ["region_id"],
            onDelete = ForeignKey.CASCADE
        )
    ])
data class Summit(
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
