package org.callapp.regularenglish

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class RegularEnglishApplication : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}
