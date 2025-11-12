package com.example.absolutecinema.data.api

import Cinema
import Results
import Trailer
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CinemaCallable {
    companion object {
        const val API_KEY = "d997190299b8c60ad08ef02b0dc4c804"
    }

    @GET("movie/{id}")
    fun getDetails(
        @Path("id") id: String,
        @Query("api_key") apiKey: String = API_KEY,
        @Query("language") language: String = "en-US"
    ): Call<Results>

    @GET("movie/popular")
    fun getPopular(
        @Query("api_key") apiKey: String = API_KEY,
        @Query("language") language: String = "en-US",
        @Query("page") page: Int = 1
    ): Call<Cinema>
    @GET("movie/{id}/videos?api_key=d997190299b8c60ad08ef02b0dc4c804")
    fun getVid(@Path("id") id:String):Call<Trailer>

    @GET("search/movie")
    fun searchForMovieByTitle(
        @Query("query") text: String,
        @Query("api_key") key : String = API_KEY
    ):
            Call<Cinema>

    @GET("discover/movie")
    fun discoverMovie(
        @Query("with_genres") genreId: String = "",
        @Query("year") year: Int? = null,
        @Query("api_key") key : String = API_KEY
    ):
            Call<Cinema>
}