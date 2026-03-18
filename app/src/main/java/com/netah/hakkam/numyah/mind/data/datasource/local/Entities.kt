package com.netah.hakkam.numyah.mind.data.datasource.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    indices = [
        Index("userId")
    ]
)
data class PostTable (
    val userId : Long?,
    @PrimaryKey
    @ColumnInfo(name = "photoId")
    val id: Long?,
    val title: String?,
    val body: String?
)