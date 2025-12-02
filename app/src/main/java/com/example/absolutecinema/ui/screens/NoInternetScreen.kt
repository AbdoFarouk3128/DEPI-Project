package com.example.absolutecinema.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.absolutecinema.R
import com.example.absolutecinema.ui.theme.darkBlue

@Composable
fun NoInternet(
    tryAgain:()->Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(darkBlue),
        Arrangement.Top,
        Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Red)
                .padding(top = 50.dp),
            Alignment.Center
        ) { Text("No Internet Connection", color = Color.White, fontSize = 24.sp) }
        Spacer(modifier = Modifier.height(350.dp))
        Image(
            painter = painterResource(R.drawable.no_internet),
            contentDescription = "No Internet",
            modifier = Modifier
                .height(100.dp)
                .width(100.dp)

        )

        Button(
            modifier = Modifier
                .padding(24.dp),
            shape = RoundedCornerShape(12.dp),
            onClick = tryAgain,
            colors = ButtonColors(
                containerColor = Color.Red,
                contentColor = Color.White,
                disabledContainerColor = Color.Red,
                disabledContentColor = Color.White,
            )
        ) {
            Row {
                Text("Try again")
                Icon(
                    imageVector = Icons.Default.Refresh,
                    tint = Color.White,
                    contentDescription = "try again",

                    )
            }
        }

    }
}


