package com.eroglu.newsapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.eroglu.newsapp.data.database.ArticleDatabase
import com.eroglu.newsapp.data.model.Article
import com.eroglu.newsapp.domain.repository.NewsRepository
import com.eroglu.newsapp.presentation.ArticleDetailScreen
import com.eroglu.newsapp.presentation.NewsViewModel
import com.eroglu.newsapp.presentation.NewsViewModelProviderFactory
import com.eroglu.newsapp.presentation.SavedNewsScreen
import com.eroglu.newsapp.presentation.components.NewsList
import com.eroglu.newsapp.util.Resource
import com.eroglu.newsapp.util.Screen
import com.eroglu.newsapp.worker.NewsWorker
import java.util.concurrent.TimeUnit

class NewsActivity : ComponentActivity() {

    lateinit var viewModel: NewsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Veritabanı ve Repository Hazırlığı
        val database = ArticleDatabase(this)
        val newsRepository = NewsRepository(database)
        val viewModelProviderFactory = NewsViewModelProviderFactory(newsRepository)
        viewModel = ViewModelProvider(this, viewModelProviderFactory).get(NewsViewModel::class.java)

        // --- DÜZELTME 1: WORKMANAGER'I BURADA BAŞLATIYORUZ ---
        // Uygulama her açıldığında bu satır çalışacak ve
        // eğer daha önce zamanlanmamışsa işçiyi sıraya koyacak.
        setupPeriodicWork()
        // -----------------------------------------------------

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    NavHost(
                        navController = navController,
                        startDestination = Screen.NewsList.route
                    ) {
                        // ... Screen tanımlamaların (Aynen kalıyor) ...
                        composable(route = Screen.NewsList.route) {
                            Column {
                                Button(
                                    onClick = { navController.navigate(Screen.SavedNews.route) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp)
                                ) {
                                    Text("Kaydedilen Haberleri Gör")
                                }
                                NewsListScreen(
                                    viewModel = viewModel,
                                    onArticleClick = { article ->
                                        viewModel.currentArticle = article
                                        navController.navigate(Screen.ArticleDetail.route)
                                    }
                                )
                            }
                        }

                        composable(route = Screen.ArticleDetail.route) {
                            ArticleDetailScreen(viewModel = viewModel)
                        }

                        composable(route = Screen.SavedNews.route) {
                            SavedNewsScreen(
                                viewModel = viewModel,
                                onArticleClick = { article ->
                                    viewModel.currentArticle = article
                                    navController.navigate(Screen.ArticleDetail.route)
                                }
                            )
                        }
                    }
                }
            }
        }
    } // onCreate BURADA BİTİYOR

    // --- DÜZELTME 2: FONKSİYON onCreate'in DIŞINDA ---
    private fun setupPeriodicWork() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            .build()

        // Not: <NewsWorker> kısmı şu an kırmızı yanabilir,
        // çünkü NewsWorker sınıfını henüz oluşturmadık.
        val newsWorkRequest = PeriodicWorkRequestBuilder<NewsWorker>(
            30, TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            "NewsUpdateWork",
            ExistingPeriodicWorkPolicy.KEEP,
            newsWorkRequest
        )
    }
}

@Composable
fun NewsListScreen(
    viewModel: NewsViewModel,
    onArticleClick: (Article) -> Unit // Tıklama fonksiyonu parametre olarak eklendi
) {
    // ViewModel'daki state'i (durumu) dinliyoruz
    val result = viewModel.breakingNews.value

    Box(modifier = Modifier.fillMaxSize()) {
        when (result) {
            is Resource.Loading -> {
                // Yükleniyor...
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            is Resource.Success -> {
                // Başarılı! Listeyi göster
                val articles = result.data?.articles ?: emptyList()
                // Listeye tıklama özelliğini gönderiyoruz
                NewsList(articles = articles, onArticleClick = onArticleClick)
            }

            is Resource.Error -> {
                // Hata! Mesajı göster
                Text(
                    text = result.message ?: "Bilinmeyen Hata",
                    color = Color.Red,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}