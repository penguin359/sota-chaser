package org.northwinds.app.sotachaser.network.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AssociationList(val associations: List<AssociationEntity>)

@JsonClass(generateAdapter = true)
data class AssociationEntity(
    val associationCode: String?,
    val associationName: String?,
    val manager: String?,
    val associationManagerCallsign: String?,
    val activeFrom: String?,  // 2022-07-10T02:22:00.461Z
    val dxcc: String?,
    val regionsCount: Int,
    val summitsCount: Int,
    val maxLat: Double?,
    val maxLong: Double?,
    val minLat: Double?,
    val minLong: Double?,
)

@JsonClass(generateAdapter = true)
data class AssociationWithRegionsEntity(
    val associationCode: String?,
    val associationName: String?,
    val manager: String?,
    val associationManagerCallsign: String?,
    val activeFrom: String?,  // 2022-07-10T02:22:00.461Z
    val dxcc: String?,
    val regionsCount: Int,
    val summitsCount: Int,
    val maxLat: Double?,
    val maxLong: Double?,
    val minLat: Double?,
    val minLong: Double?,
    val regions: List<RegionEntity>?,
    val arm: List<ArmEntity>?,
)

@JsonClass(generateAdapter = true)
data class ArmEntity(
    val associationPrefix: String?,
    val fileName: String?,
    val language: String?,
    val versionNumber: Double?,
    val lastUpdated: String,  // 2022-07-10T02:22:00.461Z
)

@JsonClass(generateAdapter = true)
data class RegionEntity(
    val associationCode: String?,
    val regionCode: String?,
    val regionManagerCallsign: String?,
    val regionName: String?,
    val manager: String?,
    val summits: Int,
    val notes: String?,
    val maxLat: Double?,
    val maxLong: Double?,
    val minLat: Double?,
    val minLong: Double?,
)

@JsonClass(generateAdapter = true)
data class RegionWithSummitsEntity(
    val region: RegionEntity,
    val summits: List<SummitEntity>?,
)

@JsonClass(generateAdapter = true)
data class RestrictionEntity(
    val code: Int,
    val type: String?,
)

@JsonClass(generateAdapter = true)
data class RestrictionDetailEntity(
    val id: Int,
    val content: String?,
    val title: String?,
)

@JsonClass(generateAdapter = true)
data class SummitEntity(
    val summitCode: String?,
    val name: String?,
    val shortCode: String?,
    val altM: Int,
    val altFt: Int,
    val gridRef1: String?,
    val gridRef2: String?,
    val notes: String?,
    val validFrom: String,  // 2022-07-10T02:22:00.461Z
    val validTo: String,  // 2022-07-10T02:22:00.461Z
    val activationCount: Int?,
    val myActivations: Int?,
    val activationDate: String?,  // 2022-07-10T02:22:00.461Z
    val myChases: Int?,
    val activationCall: String?,
    val longitude: Double,
    val latitude: Double,
    val locator: String?,
    val points: Int,
    val regionCode: String?,
    val regionName: String?,
    val associationCode: String?,
    val associationName: String?,
    val valid: Boolean,
    val restrictionMask: Boolean,
    val restrictionList: List<RestrictionEntity>?,
)

@JsonClass(generateAdapter = true)
data class SpotEntity(
    val id: Long,
    val userID: Long,
    val timeStamp: String,  // 2022-07-10T02:22:00.461Z
    val comments: String?,
    val callsign: String?,
    val associationCode: String,
    val summitCode: String,
    val activatorCallsign: String,  // max len: 12
    val activatorName: String?,
    val frequency: String,
    val mode: String,
    val summitDetails: String?,
    val highlightColor: String?,
)
