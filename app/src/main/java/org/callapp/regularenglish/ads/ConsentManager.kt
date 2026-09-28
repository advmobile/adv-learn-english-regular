package org.callapp.regularenglish.ads

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform

/** UMP consent flow. Consent is refreshed at every app launch before ad requests. */
object ConsentManager {
    private const val TAG = "UMP"

    fun gatherConsent(activity: Activity, onComplete: (Boolean) -> Unit) {
        val information = UserMessagingPlatform.getConsentInformation(activity)
        information.requestConsentInfoUpdate(
            activity,
            ConsentRequestParameters.Builder().build(),
            {
                UserMessagingPlatform.loadAndShowConsentFormIfRequired(activity) { error ->
                    if (error != null) Log.w(TAG, "Consent form: ${error.message}")
                    onComplete(information.canRequestAds())
                }
            },
            { error ->
                Log.w(TAG, "Consent update: ${error.message}")
                onComplete(information.canRequestAds())
            }
        )
    }

    fun showPrivacyChoices(activity: Activity) {
        UserMessagingPlatform.showPrivacyOptionsForm(activity) { error ->
            if (error != null) Log.w(TAG, "Privacy choices: ${error.message}")
        }
    }

    fun isPrivacyChoicesRequired(context: Context): Boolean =
        UserMessagingPlatform.getConsentInformation(context).privacyOptionsRequirementStatus ==
            ConsentInformation.PrivacyOptionsRequirementStatus.REQUIRED
}
