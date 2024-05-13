package com.mpt.hotelbediax.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.mpt.hotelbediax.models.Destination

@Dao
interface DestinationDao {
    @Query("SELECT * FROM destination")
    suspend fun getAllDestinations(): List<Destination>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDestination(destination: Destination)

    @Query("DELETE FROM destination WHERE id = :id")
    suspend fun deleteDestination(id: Int)
    @Query("SELECT * FROM destination WHERE name LIKE :name || '%'")
    suspend fun getDestinationsByName(name: String): List<Destination>
    @Update
    suspend fun updateDestination(destination: Destination)
}