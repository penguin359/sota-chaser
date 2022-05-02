package org.northwinds.app.sotachaser.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey


@Entity(foreignKeys = [
        ForeignKey(entity = Association::class,
            parentColumns = ["uid"],
            childColumns = ["association_id"],
            onDelete = ForeignKey.CASCADE
        )
    ])
data class Region(
    @PrimaryKey(autoGenerate = true) val uid: Long,
    @ColumnInfo(name = "association_id") val associationId: Long,
    @ColumnInfo(name = "code") val code: String,
    @ColumnInfo(name = "name") val name: String
)
