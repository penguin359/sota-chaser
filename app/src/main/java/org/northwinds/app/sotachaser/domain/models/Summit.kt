package org.northwinds.app.sotachaser.domain.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey


data class Summit(
    val id: Long,
    val regionId: Long,
    val code: String,
    val name: String,
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
