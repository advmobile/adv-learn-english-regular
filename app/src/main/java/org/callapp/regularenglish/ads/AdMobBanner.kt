package org.callapp.regularenglish.ads

import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

@Composable
fun AdMobBanner(modifier: Modifier = Modifier) {
    AndroidView(modifier = modifier, factory = { context ->
        AdView(context).apply {
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            adUnitId = AdMobConfig.BANNER_AD_UNIT_ID
            setAdSize(AdSize.BANNER)
            loadAd(AdRequest.Builder().build())
        }
    })
}
