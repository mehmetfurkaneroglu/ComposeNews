package com.eroglu.newsapp.presentation

import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun ArticleDetailScreen(
    viewModel: NewsViewModel // ViewModel'i buraya parametre olarak alıyoruz
) {
    val article = viewModel.currentArticle // ViewModel'dan seçili haberi alıyoruz
    val context = LocalContext.current

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    article?.let {
                        viewModel.saveArticle(it)
                        Toast.makeText(context, "Haber Kaydedildi! ❤️", Toast.LENGTH_SHORT).show()
                    }
                }
            ) {
                // Kalp İkonu
                Icon(imageVector = Icons.Default.Favorite, contentDescription = "Kaydet")
            }
        }
    ) { paddingValues ->
        // WebView Alanı
        article?.url?.let { url ->
            AndroidView(
                factory = { ctx ->
                    WebView(ctx).apply {
                        webViewClient = WebViewClient() // Linklerin tarayıcıda değil, uygulama içinde açılmasını sağlar
                        settings.javaScriptEnabled = true // Bazı siteler için JS gerekebilir
                        loadUrl(url)
                    }
                },
                modifier = Modifier.padding(paddingValues) // Scaffold padding'ini uygula
            )
        }
    }
}