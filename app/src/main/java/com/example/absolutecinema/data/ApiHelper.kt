package com.example.absolutecinema.data

import Cinema
import Results
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


fun getMovies(onResult: (List<Results>) -> Unit) {
    val retrofit = Retrofit.Builder()
        .baseUrl("https://api.themoviedb.org/3/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    val c= retrofit.create(CinemaCallable::class.java)
    c.getPopular().enqueue(object : Callback<Cinema> {
        override fun onResponse(call: Call<Cinema>, response: Response<Cinema>) {
            val cinema = response.body()
            val movies = cinema?.results!!
            onResult(movies)

        }



        override fun onFailure(call: Call<Cinema>, t: Throwable) {

        }

    })
}