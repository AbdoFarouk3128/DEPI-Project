package com.example.absolutecinema.data.helpers

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat.startActivity
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.example.absolutecinema.ui.screens.MovieDetails
import kotlinx.serialization.Serializable


fun shareMovie(context: Context,url:String) {

    val textToShare = buildString {
        append("Check out this movie!")

            append("\n\n$url")

    }

    val sendIntent = Intent(Intent.ACTION_SEND).apply {
        putExtra(Intent.EXTRA_TEXT, textToShare)
        type = "text/plain"
    }

    val shareIntent = Intent.createChooser(sendIntent, null)
    context.startActivity(shareIntent)
}