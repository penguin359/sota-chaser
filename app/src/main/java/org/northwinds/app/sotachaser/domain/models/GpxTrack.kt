package org.northwinds.app.sotachaser.domain.models

import java.io.Serializable

data class GpxTrack(
    val id: Long,
    val summitId: Long,
    val callsign: String,
    val trackNotes: String,
    val trackTitle: String,
) : Serializable
