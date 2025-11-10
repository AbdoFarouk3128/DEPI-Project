package com.example.absolutecinema.ui.componants

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarOutline
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun RatingBar(
    modifier: Modifier = Modifier,
    rating: Int = 0,
    stars: Int = 5,
    movieId:String,
    onRatingChanged: (Int) -> Unit,
    starsColor: Color = Color.Yellow,
    saveRating:(String,Int)-> Unit,
) {
    var lastClickedIndex by remember { mutableIntStateOf(-1) }

    Row {
        for (index in 1..stars){
            Icon(
                imageVector =
                    if (index <= rating) {
                        Icons.Rounded.Star
                    } else {
                            Icons.Rounded.StarOutline
                    },
                contentDescription = null,
                tint = starsColor,
                modifier = modifier
                    .clickable {
                        if (index == lastClickedIndex && index == rating) {

                            onRatingChanged(index - 1)
                            lastClickedIndex = -1
                        } else {
                            onRatingChanged(index)
                            lastClickedIndex = index
                        }
                        saveRating(movieId,index)
                         }

            )
        }
    }
}