package com.mpt.hotelbediax.network

import com.mpt.hotelbediax.models.Destination
import com.mpt.hotelbediax.models.DestinationResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE

import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {
    @GET("/destinations")
    suspend fun getAllDestinations(): Response<DestinationResponse>
    @DELETE("/destinations")
    suspend fun  deleteById(@Path("id")id: Int):Response<Unit>
    @PUT("/destinations")
    suspend fun update(@Body destination: Destination): Response<Unit>
    @POST("/destinations")
    suspend fun create(@Body destination: Destination): Response<Unit>

}