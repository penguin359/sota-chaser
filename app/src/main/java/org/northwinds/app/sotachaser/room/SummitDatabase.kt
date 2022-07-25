package org.northwinds.app.sotachaser.room

import androidx.room.Database
import androidx.room.RoomDatabase
import org.northwinds.app.sotachaser.room.model.*

@Database(entities = [
    AssociationEntity::class,
    RegionEntity::class,
    SummitEntity::class,
    GpxTrackEntity::class,
    GpxPointEntity::class],
    version = 6)
abstract class SummitDatabase : RoomDatabase() {
    abstract fun summitDao(): SummitDao
}
