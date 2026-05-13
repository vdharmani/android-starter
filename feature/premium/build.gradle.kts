import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.plugin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.vdharmani.starter.feature.premium"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
        // REPLACE with your own RevenueCat Android public API key when wiring
        // a real backend. The placeholder is enough for the template to build;
        // purchase calls will obviously fail until a real key is in place.
        buildConfigField("String", "REVENUECAT_KEY", "\"goog_PLACEHOLDER_REVENUECAT_KEY\"")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
}

kotlin {
    compilerOptions { jvmTarget = JvmTarget.JVM_17 }
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:ui"))

    // subscription-android — built earlier in this repo set. `api` because
    // :app needs SubscriptionManager / RevenueCatProvider to register the
    // provider in Application.onCreate.
    api("com.github.vdharmani.subscription-android:subscription-core:1.1.2")
    api("com.github.vdharmani.subscription-android:subscription-revenuecat:1.1.2")

    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    implementation(libs.compose.runtime)
    implementation(libs.compose.foundation)
    implementation(libs.compose.ui)
    implementation(libs.compose.material3)

    implementation(libs.hilt.android)
    implementation(libs.hilt.navigation.compose)
    ksp(libs.hilt.compiler)
}
