import com.google.gson.annotations.SerializedName

data class Cinema(
    val results: ArrayList<Results>
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
    var isWatched: Boolean = false
//    @SerializedName("vote_average")
//    val rating:String,
)
