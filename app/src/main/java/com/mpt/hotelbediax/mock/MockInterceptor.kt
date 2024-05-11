package com.mpt.hotelbediax.mock

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mpt.hotelbediax.BuildConfig
import com.mpt.hotelbediax.models.Destination
import com.mpt.hotelbediax.models.DestinationResponse
import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody
import java.io.IOException

class MockInterceptor(private val context: Context) : Interceptor {
    private val gson = Gson()
    private var destinations: MutableList<Destination>? = loadInitialDestinations()
    override fun intercept(chain: Interceptor.Chain): Response {
        if (BuildConfig.DEBUG) {
            val uri = chain.request().url().uri().toString()
            val json = when {
                uri.contains("destinations") -> loadJsonFromAsset("mock_destinations")
                else -> ""
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
        val listType = object : TypeToken<List<Destination>>() {}.type
        return gson.fromJson(json, DestinationResponse::class.java).results?.toMutableList()
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
