package org.northwinds.app.sotachaser.domain.models

data class Region(
    val id: Long,
    val associationId: Long,
    val code: String,
    val name: String,
    val manager: String? = null,
    val managerCallsign: String? = null,
    val notes: String? = null,
    val summitsCount: Int = 0,
    val maxLat: Double? = null,
    val maxLong: Double? = null,
    val minLat: Double? = null,
    val minLong: Double? = null,
)
