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
    @GET("/")
    suspend fun getAllDestinations(): Response<DestinationResponse>
    @DELETE("/delete/{id}")
    suspend fun  deleteById(@Path("id")id: Int):Response<Unit>
    @PUT("/update")
    suspend fun update(@Body destination: Destination): Response<Unit>
    @POST("/create")
    suspend fun create(@Body destination: Destination): Response<Unit>

}