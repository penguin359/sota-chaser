package org.northwinds.app.sotachaser.domain.models

data class GpxPoint(
    val id: Long,
    val gpxTrackId: Long,
    val index: Int,
    val latitude: Double,
    val longitude: Double,
    val altitude: Double,
    val distance: Double,
)
