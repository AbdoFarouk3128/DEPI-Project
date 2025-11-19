package com.example.absolutecinema.data.api

import com.google.gson.annotations.SerializedName

data class Cinema(
    val results: ArrayList<Results>,
    val keywords:ArrayList<Keywords>
)

data class Results(
//    @SerializedName("backdrop_path")
//    val background:String,

    @SerializedName("poster_path")
    val poster: String,
    val id: String,
    @SerializedName("backdrop_path")
    val background: String,
    val title: String,
    @SerializedName("release_date")
    val date: String,
    var isWatched: Boolean = false,
    val adult: Boolean,
//    @SerializedName("vote_average")
//    val rating:String,
)

data class Trailer(
    val results: ArrayList<Links>
)


data class Links(
    @SerializedName("key")
    val key: String
)
data class Keywords(
    val id:String
)