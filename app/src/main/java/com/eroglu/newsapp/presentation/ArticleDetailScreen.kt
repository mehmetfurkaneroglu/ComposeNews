package com.eroglu.newsapp.presentation

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun ArticleDetailScreen(url: String) {
    // Android'in klasik WebView'ını Compose içinde kullanmak için AndroidView kullanıyoruz
    AndroidView(
        factory = { context ->
            WebView(context).apply {
                webViewClient = WebViewClient() // Linklerin tarayıcıda değil, uygulama içinde açılmasını sağlar
                settings.javaScriptEnabled = true // Bazı siteler için JS gerekebilir
                loadUrl(url)
            }
        },
        update = {webView ->
            webView.loadUrl(url)
        }
    )
}