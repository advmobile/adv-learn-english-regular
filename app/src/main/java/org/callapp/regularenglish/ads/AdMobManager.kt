package org.callapp.regularenglish.ads

import android.app.Activity
import android.app.Application
import android.util.Log
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import org.callapp.regularenglish.BuildConfig

object AdMobManager {
    private const val TAG = "AdMob"
    private var interstitialAd: InterstitialAd? = null
    private var initialized = false

    fun initialize(application: Application) {
        if (!BuildConfig.SHOW_ADS) return
        if (initialized) return
        initialized = true
        if (BuildConfig.DEBUG) {
            val testDeviceId = BuildConfig.ADMOB_TEST_DEVICE_ID.trim()
            if (testDeviceId.isNotEmpty()) {
                MobileAds.setRequestConfiguration(
                    RequestConfiguration.Builder().setTestDeviceIds(listOf(testDeviceId)).build()
                )
                Log.i(TAG, "Debug Test Device configured.")
            } else {
                Log.w(TAG, "No ADMOB_TEST_DEVICE_ID configured. Make one ad request, copy the test-device ID printed by the Google Mobile Ads SDK, add ADMOB_TEST_DEVICE_ID=<ID> to gradle.properties, then rebuild. Do not click an ad before configuring it.")
            }
        }
        MobileAds.initialize(application) { loadInterstitial(application) }
    }

    fun loadInterstitial(application: Application) {
        if (!BuildConfig.SHOW_ADS) return
        if (interstitialAd != null) return
        InterstitialAd.load(application, AdMobConfig.INTERSTITIAL_AD_UNIT_ID, AdRequest.Builder().build(), object : InterstitialAdLoadCallback() {
            override fun onAdLoaded(ad: InterstitialAd) { interstitialAd = ad }
            override fun onAdFailedToLoad(error: com.google.android.gms.ads.LoadAdError) { Log.w(TAG, "Interstitial failed: ${error.code}") }
        })
    }

    fun showInterstitial(activity: Activity?, onDismiss: () -> Unit) {
        if (!BuildConfig.SHOW_ADS) { onDismiss(); return }
        val ad = interstitialAd
        if (activity == null || ad == null) { onDismiss(); return }
        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() { interstitialAd = null; loadInterstitial(activity.application); onDismiss() }
            override fun onAdFailedToShowFullScreenContent(error: com.google.android.gms.ads.AdError) { interstitialAd = null; loadInterstitial(activity.application); onDismiss() }
        }
        ad.show(activity)
    }

    /** Shows an interstitial only before each fifth user-initiated screen transition. */
    fun continueAfterScreenAction(activity: Activity?, navigate: () -> Unit) {
        if (!BuildConfig.SHOW_ADS) { navigate(); return }
        if (activity == null) { navigate(); return }
        val preferences = activity.getSharedPreferences("admob_navigation", Activity.MODE_PRIVATE)
        val actions = preferences.getInt("screen_actions", 0) + 1
        if (actions < 5) {
            preferences.edit().putInt("screen_actions", actions).apply()
            navigate()
        } else {
            preferences.edit().putInt("screen_actions", 0).apply()
            showInterstitial(activity, navigate)
        }
    }
}
