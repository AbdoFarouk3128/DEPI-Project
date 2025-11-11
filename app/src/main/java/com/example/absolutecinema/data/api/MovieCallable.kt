package com.example.absolutecinema.data.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface MovieCallable {
    @GET("movie/{id}?language=en-US&api_key=d997190299b8c60ad08ef02b0dc4c804")
    fun getMovieDetails(@Path("id") id: String): Call<MovieDetails>

    @GET("movie/{id}/recommendations?language=en-US&page=1&api_key=d997190299b8c60ad08ef02b0dc4c804")
    fun getRecommendations(@Path("id") id: String): Call<MovieRecommendationsResponse>

    @GET("movie/{id}/credits?language=en-US&api_key=d997190299b8c60ad08ef02b0dc4c804")
    fun getCredits(@Path("id") id: String): Call<Credits>

    // ✅ ADD THIS - Videos/Trailers endpoint
    @GET("movie/{id}/videos?language=en-US&api_key=d997190299b8c60ad08ef02b0dc4c804")
    fun getVideos(@Path("id") id: String): Call<VideosResponse>
}