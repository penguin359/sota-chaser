package org.northwinds.app.sotachaser.network.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AlertDto(
    val id: Long,
    val userID: Long,
    val timeStamp: String?,  // 2022-07-10T02:11:34.455Z
    val dateActivated: String?,  // 2022-07-10T02:11:34.455Z
    val associationCode: String?,
    val summitCode: String?,
    val summitDetails: String?,
    val posterCallsign: String?,
    val activatingCallsign: String?,
    val activatorName: String?,
    val frequency: String?,
    val comments: String?,
)

@JsonClass(generateAdapter = true)
data class AlertPostEntityDto(
    val id: Long?,  // null if new alert, otherwise modify existing
    val userID: Long,
    val dateActivated: String,  // 2022-07-10T02:11:34.455Z
    val eta: String,  // ^([01]\d|2[0-3])([0-5]\d)$
    val associationCode: String,
    val summitCode: String,
    val posterCallsign: String?,
    val activatingCallsign: String,
    val activatingName: String?,
    val frequency: String,
    val comments: String?,
)

@JsonClass(generateAdapter = true)
data class SpotDto(
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
