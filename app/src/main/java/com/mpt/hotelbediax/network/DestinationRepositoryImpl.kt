package com.mpt.hotelbediax.network

import com.mpt.hotelbediax.models.Destination
import com.mpt.hotelbediax.models.DestinationResponse
import retrofit2.Response
import javax.inject.Inject

class DestinationRepositoryImpl @Inject constructor(private val apiService: ApiService) : DestinationRepository {

    override suspend fun getAllDestinations(): DestinationResponse {
        val response = apiService.getAllDestinations()
        if (response.isSuccessful) {
            return response.body() ?: DestinationResponse()
        } else {
            throw Exception("Error fetching destinations: ${response.errorBody()?.string()}")
        }
    }

    override suspend fun deleteById(id: Int): Response<Unit> {
        val response = apiService.deleteById(id)
        return throwErrorOrReturn(response)
    }

    override suspend fun update(destination: Destination): Response<Unit> {
        val response = apiService.update(destination)
        return throwErrorOrReturn(response)
    }

    override suspend fun create(destination: Destination): Response<Unit> {
        val response = apiService.create(destination)
        return throwErrorOrReturn(response)
    }

    private fun throwErrorOrReturn(response: Response<Unit>): Response<Unit> {
        if (!response.isSuccessful) {
            throw Exception("Error creating destination: ${response.errorBody()?.string()}")
        }else{
            return response
        }
    }
}