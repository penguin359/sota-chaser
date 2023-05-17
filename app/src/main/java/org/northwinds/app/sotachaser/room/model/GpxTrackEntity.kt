package org.northwinds.app.sotachaser.room.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import org.northwinds.app.sotachaser.domain.models.Association
import org.northwinds.app.sotachaser.domain.models.GpxTrack
import org.northwinds.app.sotachaser.network.model.GpxTrackDto
import org.northwinds.app.sotachaser.network.model.RegionDto
import org.northwinds.app.sotachaser.room.SummitDao

@Entity(
    tableName = "gpx_track",
    foreignKeys = [
        ForeignKey(entity = SummitEntity::class,
            parentColumns = ["id"],
            childColumns = ["summit_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class GpxTrackEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo(name = "summit_id", index = true) val summitId: Long,
    @ColumnInfo(name = "callsign") val callsign: String,
    @ColumnInfo(name = "track_notes") val trackNotes: String,
    @ColumnInfo(name = "track_title") val trackTitle: String,
)

fun GpxTrackDto.asDatabaseModel(summitId: Long) = GpxTrackEntity(
    id = hdrId.toLong(),
    summitId = summitId,
    callsign = callsign,
    trackNotes = trackNotes,
    trackTitle = trackTitle,
)

fun List<GpxTrackDto>.asDatabaseModel(summitId: Long): List<GpxTrackEntity> = map {
    it.asDatabaseModel(summitId)
}

fun GpxTrackEntity.asDomainModel() = GpxTrack(
    id = id,
    summitId = summitId,
    callsign = callsign,
    trackNotes = trackNotes,
    trackTitle = trackTitle,
)

fun List<GpxTrackEntity>.asDomainModel() = map {
    it.asDomainModel()
}
