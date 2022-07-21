package org.northwinds.app.sotachaser.room.model

import androidx.room.*
import org.northwinds.app.sotachaser.domain.models.GpxPoint
import org.northwinds.app.sotachaser.domain.models.GpxTrack
import org.northwinds.app.sotachaser.network.model.GpxPointDto
import org.northwinds.app.sotachaser.network.model.GpxTrackDto
import org.northwinds.app.sotachaser.room.SummitDao

@Entity(
    tableName = "gpx_point",
    foreignKeys = [
        ForeignKey(entity = GpxTrackEntity::class,
            parentColumns = ["id"],
            childColumns = ["gpx_track_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["gpx_track_id", "index"], unique = true)
    ]
)
data class GpxPointEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo(name = "gpx_track_id") val gpxTrackId: Long,
    @ColumnInfo(name = "index") val index: Int,
    @ColumnInfo(name = "latitude") val latitude: Double,
    @ColumnInfo(name = "longitude") val longitude: Double,
    @ColumnInfo(name = "altitude") val altitude: Double,
    @ColumnInfo(name = "distance") val distance: Double,
)

fun GpxPointDto.asDatabaseModel(gpxTrackId: Long) = GpxPointEntity(
    id = 0,
    gpxTrackId = gpxTrackId,
    index = ptIndex.toInt(),
    latitude = latitude.toDouble(),
    longitude = longitude.toDouble(),
    altitude = altitude.toDouble(),
    distance = distance.toDouble(),
)

fun List<GpxPointDto>.asDatabaseModel(gpxTrackId: Long): List<GpxPointEntity> = map {
    it.asDatabaseModel(gpxTrackId)
}

fun GpxPointEntity.asDomainModel() = GpxPoint(
    id = id,
    gpxTrackId = gpxTrackId,
    index = index,
    latitude = latitude,
    longitude = longitude,
    altitude = altitude,
    distance = distance,
)

fun List<GpxPointEntity>.asDomainModel() = map {
    it.asDomainModel()
}
