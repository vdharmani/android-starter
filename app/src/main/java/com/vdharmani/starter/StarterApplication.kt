package com.vdharmani.starter

import android.app.Application
import com.vdharmani.starter.feature.premium.BuildConfig
import com.vdharmani.subscription.SubscriptionManager
import com.vdharmani.subscription.revenuecat.RevenueCatProvider
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class StarterApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Subscription provider — RevenueCat under the hood. Junior swaps
        // REVENUECAT_KEY in feature/premium/build.gradle.kts to ship a real
        // key. The placeholder value lets the template build but purchases
        // will fail until the key is real.
        SubscriptionManager.initialize(
            RevenueCatProvider(this, BuildConfig.REVENUECAT_KEY),
        )
    }
}
