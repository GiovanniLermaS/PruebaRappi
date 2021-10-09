package com.example.pruebarappi.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class ResultService(
    @PrimaryKey(autoGenerate = false) val id: Int?,
    @ColumnInfo(name = "backdrop_path") val backdrop_path: String?,
    @ColumnInfo(name = "original_title") val original_title: String?,
    @ColumnInfo(name = "overview") val overview: String?,
    @ColumnInfo(name = "poster_path") val poster_path: String?,
    @ColumnInfo(name = "title") val title: String?
) : Serializable