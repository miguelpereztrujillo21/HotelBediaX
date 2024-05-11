package com.mpt.hotelbediax.di

import android.app.Application
import androidx.room.Room
import com.mpt.hotelbediax.dao.AppDatabase
import com.mpt.hotelbediax.dao.DestinationDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(app: Application): AppDatabase {
        return Room.databaseBuilder(app, AppDatabase::class.java, "hotelbedia_database").build()
    }

    @Provides
    fun provideDestinationDao(database: AppDatabase): DestinationDao {
        return database.destinationDao()
    }
}