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

    @Query("SELECT * FROM destination LIMIT :limit OFFSET :offset")
    suspend fun getDestinationsInRange(offset: Int, limit: Int): List<Destination>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDestination(destination: Destination)
    @Update
    suspend fun updateDestination(destination: Destination)

    @Query("DELETE FROM destination WHERE id = :id")
    suspend fun deleteDestination(id: Int)

    @Query("SELECT * FROM destination WHERE name LIKE :name || '%'")
    suspend fun getDestinationsByName(name: String): List<Destination>

    @Query("SELECT * FROM Destination ORDER BY lastModify ASC")
    suspend fun getDestinationsOrderedByDateASC(): List<Destination>
    @Query("SELECT * FROM Destination ORDER BY lastModify DESC")
    suspend fun getDestinationsOrderedByDateDESC(): List<Destination>

    @Query("SELECT * FROM destination WHERE type = :type")
    suspend fun getDestinationsByType(type: String): List<Destination>
}