package com.mpt.hotelbediax.network

import com.mpt.hotelbediax.models.Destination
import com.mpt.hotelbediax.models.DestinationResponse
import retrofit2.Response

interface DestinationRepository {
    suspend fun getAllDestinations(): DestinationResponse

    suspend fun deleteById(id: Int): Response<Unit>

    suspend fun update(destination: Destination): Response<Unit>

    suspend fun create(destination: Destination): Response<Unit>
}