package com.example.absolutecinema.data.onboarding

import android.content.Context
import androidx.annotation.DrawableRes
import com.example.absolutecinema.R
import androidx.core.content.edit

sealed class OnBoardingModel(
    @DrawableRes val image:Int,
    val title:String,
    val desc:String,
){
    data object FirstPage: OnBoardingModel(
        image = R.drawable.login_rafiki,
        title = "Stay Logged In, Stay You",
        desc = "Sign in from any device and keep all your saved movies with you. Your privacy stays safe, and your experience stays yours.",
    )
    data object SecondPage: OnBoardingModel(
        image = R.drawable.search_rafiki,
        title = "Find Movies Your Way",
        desc = "Search by name, genre, or even multiple genres at once. Discover exactly what you’re in the mood for in seconds.",
    )
    data object ThirdPage: OnBoardingModel(
        image = R.drawable.movie_night,
        title = "Your Next Movie Night Starts Here",
        desc = "Explore new films, see ratings, and let us help you pick the perfect movie for tonight. Discovering feels fun again.",
    )
    data object FourthPage: OnBoardingModel(
        image = R.drawable.bookmarks_cuate,
        title = "Save What You Love",
        desc = "Keep your favorites in one place, rate them, mark them watched, or add them to your watchlist. ready anytime you are.",
    )
}
fun isOnBoardingComplete(context: Context){
    val prefs= context.getSharedPreferences("onboard",Context.MODE_PRIVATE)
    val isFirstTime = prefs.edit { putBoolean("first_time", true) }
    return isFirstTime

}
