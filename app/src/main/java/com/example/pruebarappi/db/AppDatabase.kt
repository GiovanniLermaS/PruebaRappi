package com.example.pruebarappi.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.pruebarappi.db.dao.ResultServiceDao
import com.example.pruebarappi.db.model.ResultService

@Database(
    entities = [ResultService::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun resultServiceDao(): ResultServiceDao
}
