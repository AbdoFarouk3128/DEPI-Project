package com.example.absolutecinema.ui.componants

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun RatingBar(
    modifier: Modifier = Modifier,
    rating: Int = 0,
    stars: Int = 5,
    movieId: String,
    onRatingChanged: (Int) -> Unit,
    starsColor: Color = Color.Red,
    saveRating: (String, Int) -> Unit,
) {
    Row {
        for (index in 1..stars) {
            Icon(
                imageVector = if (index <= rating) {
                    Icons.Rounded.Star
                } else {
                    Icons.Rounded.StarOutline
                },
                contentDescription = null,
                tint = starsColor,
                modifier = modifier.clickable {
                    val newRating = if (index == rating) {
                        // Tap the same star again to unrate (set to 0)
                        0
                    } else {
                        // Set new rating
                        index
                    }
                    onRatingChanged(newRating)
                    saveRating(movieId, newRating)
                }
            )
        }
    }
}

// RatingStatisticsBar.kt
@Composable
fun RatingStatisticsBar(
    statistics: RatingStatistics?,
    modifier: Modifier = Modifier
) {
    val stats = statistics ?: RatingStatistics()
    // Handle null or empty statistics


    // Show statistics when there are ratings
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Average Rating Display
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = String.format("%.1f", stats.averageRating),
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Rounded.Star,
                        contentDescription = null,
                        tint = Color.Red,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${stats.totalRatings} rating${if (stats.totalRatings != 1) "s" else ""}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }

            // Star rating preview
            Row {
                repeat(5) { index ->
                    Icon(
                        imageVector = if (index < stats.averageRating.toInt()) {
                            Icons.Rounded.Star
                        } else {
                            Icons.Rounded.StarOutline
                        },
                        contentDescription = null,
                        tint = Color.Red,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Rating Distribution Bars
        (5 downTo 1).forEach { starCount ->
            val count = stats.ratingDistribution[starCount] ?: 0
            val percentage = if (stats.totalRatings > 0) {
                (count.toFloat() / stats.totalRatings.toFloat())
            } else 0f

            RatingDistributionRow(
                stars = starCount,
                count = count,
                percentage = percentage
            )
        }
    }
}

@Composable
fun RatingDistributionRow(
    stars: Int,
    count: Int,
    percentage: Float
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Star count
        Text(
            text = "$stars",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.width(20.dp),
            color = Color.White
        )

        Icon(
            imageVector = Icons.Rounded.Star,
            contentDescription = null,
            tint = Color.Red,
            modifier = Modifier.size(16.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        // Progress bar
        Box(
            modifier = Modifier
                .weight(1f)
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color.LightGray)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(percentage)
                    .background(Color.Red)
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Count
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.bodySmall,
            color = Color.White,
            modifier = Modifier.width(40.dp),
            textAlign = TextAlign.End
        )
    }
}

data class RatingStatistics(
    val averageRating: Float = 0f,
    val totalRatings: Int = 0,
    val ratingDistribution: Map<Int, Int> = emptyMap() // star -> count
)