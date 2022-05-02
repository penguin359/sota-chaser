package org.northwinds.app.sotachaser.room

import androidx.room.*
import androidx.room.ForeignKey.CASCADE

@Entity(indices = [
    Index(value = ["code"], unique = true)
])
data class Association(
    @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo(name = "code") val code: String,
    @ColumnInfo(name = "name") val name: String
)
