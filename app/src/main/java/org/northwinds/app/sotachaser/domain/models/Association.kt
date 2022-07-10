package org.northwinds.app.sotachaser.domain.models

data class Association(
    val id: Long,
    val code: String,
    val name: String,
    val manager: String? = null,
    val managerCallsign: String? = null,
    val activeFrom: String? = null,
    val dxcc: String? = null,
    val regionsCount: Int = 0,
    val summitsCount: Int = 0,
    val maxLat: Double? = null,
    val maxLong: Double? = null,
    val minLat: Double? = null,
    val minLong: Double? = null,
)
