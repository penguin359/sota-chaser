package org.northwinds.app.sotachaser.network.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AlertList(val alerts: List<AlertEntity>)

@JsonClass(generateAdapter = true)
data class AlertEntity(
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
data class AlertPostEntity(
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
