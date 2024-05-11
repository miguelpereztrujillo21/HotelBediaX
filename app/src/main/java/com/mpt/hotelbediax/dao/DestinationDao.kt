package com.mpt.hotelbediax.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mpt.hotelbediax.models.Destination

@Dao
interface DestinationDao {
    @Query("SELECT * FROM destination")
    suspend fun getAllDestinations(): List<Destination>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDestination(destination: Destination)

    @Query("DELETE FROM destination")
    suspend fun deleteAllDestinations()
}