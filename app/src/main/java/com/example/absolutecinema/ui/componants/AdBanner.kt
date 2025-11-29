package com.example.absolutecinema.ui.componants

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

@Composable
fun BannerAd(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val ad = remember {
        AdView(context).apply {
            setAdSize(AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, 360))
            adUnitId = "ca-app-pub-3940256099942544/9214589741"
            val adRequest = AdRequest.Builder().build()
            loadAd(adRequest)
        }
    }


    AndroidView(
        factory = { ad },
        modifier = Modifier.fillMaxWidth()
    )
}