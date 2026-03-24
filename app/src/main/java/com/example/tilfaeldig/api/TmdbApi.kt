package com.example.tilfaeldig.api


import com.example.tilfaeldig.model.MovieResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface TmdbApi {

    @GET("discover/movie")
    fun discoverMovies(
        @Query("page") page: Int,
        @Query("with_genres") genreId: Int? = null,
        @Query("release_date") year: Int? = null,
        @Query("primary_release_date.gte") yearFrom: String? = null,
        @Query("primary_release_date.lte") yearTo: String? = null,
        @Query("vote_average.gte") minVote: Float? = null,
        @Query("with_original_language") language: String? = null,
    ): Call<MovieResponse>

    @GET("discover/tv")
    fun discoverTv(
        @Query("page") page: Int,
        @Query("with_original_language") language: String? = null,
        @Query("first_air_date_year") year: Int? = null
    ): Call<MovieResponse>
}
