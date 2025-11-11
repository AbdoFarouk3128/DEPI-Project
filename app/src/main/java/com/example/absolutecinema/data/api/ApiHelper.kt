package com.example.absolutecinema.data.api

import Cinema
import Results
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// --- Unchanged Functions ---
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
            // It's better to handle failures, e.g., by returning an empty list
            onResult(emptyList())
        }
    })
}

fun getDetails(movieId:String,onResult: (Results?) -> Unit){
    val retrofit = Retrofit.Builder()
        .baseUrl("https://api.themoviedb.org/3/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    val c= retrofit.create(CinemaCallable::class.java)
    c.getDetails(movieId).enqueue(object : Callback<Results> {
        override fun onResponse(call: Call<Results>, response: Response<Results>) {
            val cinema = response.body()
            onResult(cinema)
        }

        override fun onFailure(call: Call<Results>, t: Throwable) {
            onResult(null)
        }
    })
}

// --- Updated getMovieDetails Function ---

// Reusable Retrofit instance for MovieCallable
private val movieRetrofit = Retrofit.Builder()
    .baseUrl("https://api.themoviedb.org/3/")
    .addConverterFactory(GsonConverterFactory.create())
    .build()

private val movieCallable = movieRetrofit.create(MovieCallable::class.java)

fun getMovieDetails(movieId: String, onResult: (MovieDetails?) -> Unit) {
    movieCallable.getMovieDetails(movieId).enqueue(object : Callback<MovieDetails> {
        override fun onResponse(call: Call<MovieDetails>, response: Response<MovieDetails>) {
            val movieDetails = response.body()
            if (movieDetails != null) {
                // First, get recommendations
                movieCallable.getRecommendations(movieId).enqueue(object : Callback<MovieRecommendationsResponse> {
                    override fun onResponse(call: Call<MovieRecommendationsResponse>, response: Response<MovieRecommendationsResponse>) {
                        movieDetails.recommendations = response.body()?.results ?: emptyList()

                        // Now, get credits
                        movieCallable.getCredits(movieId).enqueue(object: Callback<Credits> {
                            override fun onResponse(call: Call<Credits>, response: Response<Credits>) {
                                movieDetails.credits = response.body()

                                // ✅ NOW GET VIDEOS/TRAILERS
                                movieCallable.getVideos(movieId).enqueue(object: Callback<VideosResponse> {
                                    override fun onResponse(call: Call<VideosResponse>, response: Response<VideosResponse>) {
                                        movieDetails.videos = response.body()
                                        onResult(movieDetails) // Return complete data with videos
                                    }

                                    override fun onFailure(call: Call<VideosResponse>, t: Throwable) {
                                        onResult(movieDetails) // Still return data even if videos fail
                                    }
                                })
                            }

                            override fun onFailure(call: Call<Credits>, t: Throwable) {
                                onResult(movieDetails) // Still return data even if credits fail
                            }
                        })
                    }

                    override fun onFailure(call: Call<MovieRecommendationsResponse>, t: Throwable) {
                        onResult(movieDetails) // Still return data if recommendations fail
                    }
                })
            } else {
                onResult(null)
            }
        }

        override fun onFailure(call: Call<MovieDetails>, t: Throwable) {
            onResult(null)
        }
    })
}