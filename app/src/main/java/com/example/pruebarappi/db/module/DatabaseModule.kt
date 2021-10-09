package com.example.pruebarappi.db.module

import android.content.Context
import androidx.room.Room
import com.example.pruebarappi.db.AppDatabase
import com.example.pruebarappi.db.dao.ResultServiceDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.internal.managers.ApplicationComponentManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponentManager::class)
object DatabaseModule {

    lateinit var database: AppDatabase

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        database = Room.databaseBuilder(
            context,
            AppDatabase::class.java, "result-service"
        ).build()
        return database
    }

    @Provides
    fun provideObjectDao(database: AppDatabase): ResultServiceDao {
        return database.resultServiceDao()
    }

}