package org.northwinds.app.sotachaser.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [
    Association::class,
    Region::class,
    Summit::class],
    version = 1)
abstract class SummitDatabase : RoomDatabase() {
    abstract fun summitDao(): SummitDao
}
