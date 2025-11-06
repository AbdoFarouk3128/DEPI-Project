package com.example.absolutecinema.data

import Cinema
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface CinemaCallable {
    @GET("movie/{id}?language=en-US&api_key=d997190299b8c60ad08ef02b0dc4c804")
    fun getDetails(@Path("id") id:String): Call<Cinema>
    @GET("movie/popular?language=en-US&page=1&api_key=d997190299b8c60ad08ef02b0dc4c804")
    fun getPopular():Call<Cinema>
}