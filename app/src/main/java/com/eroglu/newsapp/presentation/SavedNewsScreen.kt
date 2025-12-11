package com.eroglu.newsapp.presentation

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.eroglu.newsapp.data.model.Article
import com.eroglu.newsapp.presentation.components.ArticleItem
import com.eroglu.newsapp.presentation.components.NewsList
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedNewsScreen(
    viewModel: NewsViewModel,
    onArticleClick: (Article) -> Unit
) {
    // Veritabanından gelen veriyi dinliyoruz
    val savedArticles = viewModel.getSavedNews().collectAsState(initial = emptyList()).value

    // Snackbar durumunu yönetmek için (Geri Al butonu için)
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) {
        if (savedArticles.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Henüz kaydedilmiş haber yok.")
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                // items fonksiyonuna 'key' veriyoruz ki Compose listeyi karıştırmasın
                items(items = savedArticles, key = { it.url ?: it.hashCode() }) { article ->

                    // Kaydırma durumunu yöneten State
                    val dismissState = rememberSwipeToDismissBoxState(
                        confirmValueChange = {
                            if (it == SwipeToDismissBoxValue.EndToStart) {
                                // 1. Haberi Sil
                                viewModel.deleteArticle(article)

                                // 2. Snackbar Göster (Geri Al seçeneği ile)
                                scope.launch {
                                    val result = snackbarHostState.showSnackbar(
                                        message = "Haber silindi",
                                        actionLabel = "Geri Al",
                                        duration = SnackbarDuration.Short
                                    )
                                    if (result == SnackbarResult.ActionPerformed) {
                                        // Geri Al'a basılırsa haberi tekrar kaydet
                                        viewModel.saveArticle(article)
                                    }
                                }
                                true // İşlem onaylandı
                            } else {
                                false
                            }
                        }
                    )

                    // Kaydırılabilir Kutu Bileşeni
                    SwipeToDismissBox(
                        state = dismissState,
                        backgroundContent = {
                            // Arka plan rengi (Kırmızı)
                            val color = if (dismissState.targetValue == SwipeToDismissBoxValue.EndToStart) {
                                Color.Red
                            } else {
                                Color.Transparent
                            }

                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(color)
                                    .padding(horizontal = 20.dp),
                                contentAlignment = Alignment.CenterEnd
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Sil",
                                    tint = Color.White
                                )
                            }
                        },
                        // İçerik (Haber Kartı)
                        content = {
                            // MainActivity içindeki ArticleItem'ı çağırıyoruz
                            // Eğer import hatası alırsan buraya kendi ArticleItem kodunu yapıştırabilirsin.
                            ArticleItem(
                                article = article,
                                onArticleClick = onArticleClick
                            )
                        },
                        // Sadece sağdan sola kaydırmaya izin ver
                        enableDismissFromStartToEnd = false,
                        enableDismissFromEndToStart = true
                    )
                }
            }
        }
    }
}