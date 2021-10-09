package com.example.pruebarappi.db.module

import android.content.Context
import androidx.room.Room
import com.example.pruebarappi.db.AppDatabase
import com.example.pruebarappi.db.dao.ResultServiceDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java, "result-service"
        ).build()
    }

    @Provides
    fun provideObjectDao(database: AppDatabase): ResultServiceDao {
        return database.resultServiceDao()
    }
}