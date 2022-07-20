package org.northwinds.app.sotachaser.network.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AssociationDto(
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
data class AssociationWithRegionsDto(
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
    val regions: List<RegionDto>?,
    val arm: List<ArmDto>?,
)

@JsonClass(generateAdapter = true)
data class ArmDto(
    val associationPrefix: String?,
    val fileName: String?,
    val language: String?,
    val versionNumber: Double?,
    val lastUpdated: String,  // 2022-07-10T02:22:00.461Z
)

@JsonClass(generateAdapter = true)
data class RegionDto(
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
data class RegionWithSummitsDto(
    val region: RegionDto,
    val summits: List<SummitDto>?,
)

@JsonClass(generateAdapter = true)
data class RestrictionDto(
    val code: Int,
    val type: String?,
)

@JsonClass(generateAdapter = true)
data class RestrictionDetailDto(
    val id: Int,
    val content: String?,
    val title: String?,
)

@JsonClass(generateAdapter = true)
data class SummitDto(
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
    val restrictionList: List<RestrictionDto>?,
)
