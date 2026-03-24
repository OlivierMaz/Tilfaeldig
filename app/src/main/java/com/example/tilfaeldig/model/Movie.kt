package com.example.tilfaeldig.model

import com.google.gson.annotations.SerializedName

data class Movie(
    val id: String,
    val title: String,
    val name: String,
    val release_date: String?,
    val first_air_date: String?,
    val overview: String,
    @SerializedName("vote_average")
    val voteAverage: Double?,
    @SerializedName("poster_path")
    val posterpath: String?
)