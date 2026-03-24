package com.example.tilfaeldig.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    private const val BASE_URL = "https://api.themoviedb.org/3/"
    private const val TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiJmNzg3ZTQwOTk2YzMzYmVlNjU4NWQ4ZDdlYzk5NDMzOCIsIm5iZiI6MTc2NzY2ODI4OS40MjU5OTk5LCJzdWIiOiI2OTVjN2E0MTE4ZDQ2OGNkNjk5Zjg1OWIiLCJzY29wZXMiOlsiYXBpX3JlYWQiXSwidmVyc2lvbiI6MX0.jq9H3lAzd3G8G9gyWss7rFC2NxCRn_tXrxVchZinmlQ"

    private val client = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $TOKEN")
                .addHeader("Accept", "application/json")
                .build()
            chain.proceed(request)
        }
        .build()

    val api: TmdbApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TmdbApi::class.java)
    }
}
