package com.mpt.hotelbediax.mock

import android.content.Context
import com.google.gson.Gson
import com.mpt.hotelbediax.BuildConfig
import com.mpt.hotelbediax.models.Destination
import com.mpt.hotelbediax.models.DestinationResponse
import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody
import java.io.BufferedReader
import java.io.IOException
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets

class MockInterceptor(private val context: Context) : Interceptor {
    private val gson = Gson()
    private var destinations: MutableList<Destination>? = loadInitialDestinations()
    override fun intercept(chain: Interceptor.Chain): Response {
        if (BuildConfig.DEBUG) {
            val request = chain.request()
            val json = if (request.url().uri().toString().contains("destinations")) {
                destinationResponses(request)
            } else {
                ""
            }

            return Response.Builder()
                .code(200)
                .message(json)
                .request(chain.request())
                .protocol(Protocol.HTTP_1_0)
                .body(
                    ResponseBody.create(
                        MediaType.parse("application/json"),
                        json.toByteArray()
                    )
                )
                .addHeader("content-type", "application/json")
                .build()
        } else {
            return chain.proceed(chain.request())
        }
    }

    private fun loadInitialDestinations(): MutableList<Destination>? {
        val json = loadJsonFromAsset("mock_destinations")
        return gson.fromJson(json, DestinationResponse::class.java).results?.toMutableList()
    }

    private fun destinationResponses(request: Request): String {
        val uri = request.url().uri().toString()
        val method = request.method()
        return when(method) {
            "GET" -> gson.toJson(DestinationResponse().apply { results = destinations as ArrayList<Destination>? })
            "DELETE" -> {
                val id = "\\d+$".toRegex().find(uri)?.value?.toInt()
                id?.let {
                    destinations?.removeAll { it.id == id }
                }
                gson.toJson(DestinationResponse().apply { results = destinations as ArrayList<Destination>? })
            }
            "POST" -> {
                val requestBody = request.body()
                val buffer = okio.Buffer()
                requestBody?.writeTo(buffer)
                val json = buffer.readUtf8()
                val destination = gson.fromJson(json, Destination::class.java)
                destinations?.add(destination)
                gson.toJson(DestinationResponse().apply { results = destinations as ArrayList<Destination>? })
            }
            else -> ""
        }
    }
    private fun loadJsonFromAsset(path: String): String {
        val json: String?
        try {
            val inputStream = context.assets.open("$path.json")
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            json = String(buffer, Charsets.UTF_8)
        } catch (ex: IOException) {
            ex.printStackTrace()
            return ""
        }
        return json
    }
}
