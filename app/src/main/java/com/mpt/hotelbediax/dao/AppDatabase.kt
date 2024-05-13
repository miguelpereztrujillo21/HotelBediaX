package com.mpt.hotelbediax.dao

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mpt.hotelbediax.models.Destination

@Database(entities = [Destination::class], version = 3)
abstract class AppDatabase : RoomDatabase() {
    abstract fun destinationDao(): DestinationDao
}