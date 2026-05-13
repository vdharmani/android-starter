package com.vdharmani.starter.feature.legal

import androidx.compose.runtime.Composable

/** Junior swaps these URLs for the project's own hosted pages. */
private const val TERMS_URL = "https://policies.google.com/terms"
private const val PRIVACY_URL = "https://policies.google.com/privacy"

@Composable
fun TermsScreen(onBack: () -> Unit) =
    LegalWebView(title = "Terms of Service", url = TERMS_URL, onBack = onBack)

@Composable
fun PrivacyScreen(onBack: () -> Unit) =
    LegalWebView(title = "Privacy Policy", url = PRIVACY_URL, onBack = onBack)
