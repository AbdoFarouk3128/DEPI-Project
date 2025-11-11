package com.example.absolutecinema.data

import com.google.gson.annotations.SerializedName

data class MovieRecommendationsResponse(
    val results: List<MoviesRelated>
)

data class MovieDetails(
    val id: String,
    @SerializedName("imdb_id") val imdbId: String?,
    @SerializedName("original_title") val originalTitle: String,
    val overview: String,
    val popularity: Double,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("backdrop_path") val backdropPath: String?,
    @SerializedName("release_date") val releaseDate: String,
    @SerializedName("production_companies") val productionCompanies: List<ProductionCompany>,
    @SerializedName("vote_average") val voteAverage: Double,
    val genres: List<Genre>,
    var recommendations: List<MoviesRelated> = emptyList(),
    var credits: Credits? = null,
    var videos: VideosResponse? = null // ✅ ADD THIS
)

data class Genre(
    val name: String
)

data class Credits(
    val cast: List<CastMember>
)

data class CastMember(
    val name: String,
    @SerializedName("profile_path") val profilePath: String?
)

data class ProductionCompany(
    val name: String,
    @SerializedName("logo_path") val logoPath: String?
)

data class MoviesRelated(
    @SerializedName("poster_path") val poster: String?,
    val id: String,
    val title: String
)

// ✅ ADD THESE - Video/Trailer data classes
data class VideosResponse(
    val id: Int,
    val results: List<Video>
)

data class Video(
    @SerializedName("iso_639_1") val iso6391: String,
    @SerializedName("iso_3166_1") val iso31661: String,
    val name: String,
    val key: String,
    val site: String,
    val size: Int,
    val type: String,
    val official: Boolean,
    @SerializedName("published_at") val publishedAt: String,
    val id: String
)