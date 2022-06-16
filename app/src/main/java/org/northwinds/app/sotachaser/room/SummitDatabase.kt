package org.northwinds.app.sotachaser.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [
    AssociationEntity::class,
    RegionEntity::class,
    SummitEntity::class],
    version = 3)
abstract class SummitDatabase : RoomDatabase() {
    abstract fun summitDao(): SummitDao
}
