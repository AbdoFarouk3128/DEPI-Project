package com.example.absolutecinema.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.absolutecinema.data.api.Genre
import com.example.absolutecinema.data.api.GenreResponse
import com.example.absolutecinema.data.api.CinemaCallable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory

class GenreViewModel : ViewModel() {

    private val _genres = MutableStateFlow<List<Genre>>(emptyList())
    val genres: StateFlow<List<Genre>> get() = _genres

    // Retrofit instance
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.themoviedb.org/3/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val cinemaCallable = retrofit.create(CinemaCallable::class.java)

    fun loadGenres() {
        viewModelScope.launch {
            cinemaCallable.getMovieGenres().enqueue(object : Callback<GenreResponse> {
                override fun onResponse(call: Call<GenreResponse>, response: Response<GenreResponse>) {
                    val list = response.body()?.genres ?: emptyList()
                    _genres.value = list
                }

                override fun onFailure(call: Call<GenreResponse>, t: Throwable) {
                    _genres.value = emptyList()
                }
            })
        }
    }
}
