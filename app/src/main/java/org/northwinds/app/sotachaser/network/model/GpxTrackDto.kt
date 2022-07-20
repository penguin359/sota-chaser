package org.northwinds.app.sotachaser.network.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GpxPointDto(
    @Json(name = "pt_index") val ptIndex: String,
    val latitude: String,
    val longitude: String,
    val altitude: String,
    val distance: String,
)

@JsonClass(generateAdapter = true)
data class GpxTrackDto(
    @Json(name = "hdr_id") val hdrId: String,
    @Json(name = "callsign") val callsign: String,
    @Json(name = "track_notes") val trackNotes: String,
    @Json(name = "track_title") val trackTitle: String,
    @Json(name = "points") val points: List<GpxPointDto>,
)
