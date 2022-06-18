package org.northwinds.app.sotachaser.network.model

import com.squareup.moshi.JsonClass
import org.northwinds.app.sotachaser.domain.models.Association

@JsonClass(generateAdapter = true)
data class AssociationList(val associations: List<AssociationEntity>)

@JsonClass(generateAdapter = true)
data class AssociationEntity(
    val associationCode: String,
    val associationName: String,
    val manager: String?,
    val associationManagerCallsign: String?,
    val activeFrom: String?,
    val dxcc: String?,
    val regionsCount: Int,
    val summitsCount: Int,
    val maxLat: Double?,
    val maxLong: Double?,
    val minLat: Double?,
    val minLong: Double?,
)
