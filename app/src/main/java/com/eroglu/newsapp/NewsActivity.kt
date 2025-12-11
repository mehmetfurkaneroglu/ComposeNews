package com.eroglu.newsapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import coil.compose.AsyncImage
import com.eroglu.newsapp.data.database.ArticleDatabase
import com.eroglu.newsapp.data.model.Article
import com.eroglu.newsapp.domain.repository.NewsRepository
import com.eroglu.newsapp.presentation.ArticleDetailScreen
import com.eroglu.newsapp.presentation.NewsViewModel
import com.eroglu.newsapp.presentation.NewsViewModelProviderFactory
import com.eroglu.newsapp.util.Resource
import com.eroglu.newsapp.util.Screen
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class NewsActivity : ComponentActivity() {

    lateinit var viewModel: NewsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Veritabanını oluştur
        val database = ArticleDatabase(this)

        // 2. Repository'ye veritabanını ver
        val newsRepository = NewsRepository(database)

        val viewModelProviderFactory = NewsViewModelProviderFactory(newsRepository)
        viewModel = ViewModelProvider(this, viewModelProviderFactory).get(NewsViewModel::class.java)

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    // 1. Controller'ı oluştur
                    val navController = rememberNavController()

                    // 2. NavHost (Harita) Tanımla
                    NavHost(
                        navController = navController,
                        startDestination = Screen.NewsList.route // Başlangıç ekranı
                    ) {

                        // --- 1. EKRAN: HABER LİSTESİ ---
                        composable(route = Screen.NewsList.route) {
                            NewsListScreen(
                                viewModel = viewModel,
                                onArticleClick = { article ->
                                    // Tıklanınca ne olacak?
                                    // URL null değilse Detay sayfasına yönlendir
                                    article.url?.let { url ->
                                        // URL'i "Encode" ediyoruz ki navigasyon yolu bozulmasın
                                        val encodedUrl = URLEncoder.encode(
                                            url,
                                            StandardCharsets.UTF_8.toString()
                                        )
                                        navController.navigate(Screen.ArticleDetail.route + "/$encodedUrl")
                                    }
                                }
                            )
                        }

                        // --- 2. EKRAN: DETAY (WebView) ---
                        composable(
                            route = Screen.ArticleDetail.route + "/{articleUrl}",
                            arguments = listOf(
                                navArgument("articleUrl") { type = NavType.StringType }
                            )
                        ) { backStackEntry ->
                            // Parametreyi al
                            val url = backStackEntry.arguments?.getString("articleUrl") ?: ""
                            ArticleDetailScreen(url = url)
                        }
                    }
                }
            }
        }
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

@Composable
fun NewsList(
    articles: List<Article>,
    onArticleClick: (Article) -> Unit
) {
    LazyColumn(contentPadding = PaddingValues(10.dp)) {
        items(articles) { article ->
            ArticleItem(
                article = article,
                onArticleClick = onArticleClick
            )
        }
    }
}

@Composable
fun ArticleItem(
    article: Article,
    onArticleClick: (Article) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp)
            .clickable { onArticleClick(article) },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // 1. Resim Alanı (Coil AsyncImage)
            AsyncImage(
                model = article.urlToImage, // Resmin URL'i
                contentDescription = article.title, // Erişilebilirlik için açıklama
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp), // Resme sabit bir yükseklik verelim
                contentScale = ContentScale.Crop, // Resmi alana yay ve kırp
                // İsteğe bağlı: Yüklenirken veya hata durumunda gösterilecekler
                // placeholder = painterResource(R.drawable.ic_loading),
                // error = painterResource(R.drawable.ic_error)
            )

            Column(modifier = Modifier.padding(15.dp)) {
                // 2. Başlık
                Text(
                    text = article.title ?: "Başlık Yok",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.height(5.dp))
                // 3. Açıklama
                Text(
                    text = article.description ?: "Açıklama Yok",
                    fontSize = 14.sp,
                    maxLines = 3
                )
                Spacer(modifier = Modifier.height(5.dp))
                // 4. Kaynak
                Text(
                    text = article.source?.name ?: "Bilinmeyen Kaynak",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}

//@Composable
//@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
//fun PreviewArticleItem() {
//    val sampleArticle = Article(
//        author = "John Doe",
//        title = "Örnek Haber Başlığı",
//        description = "Bu bir örnek haber açıklamasıdır. Preview için hazırlanmıştır.",
//        url = "",
//        urlToImage = "",
//        publishedAt = "",
//        content = "",
//        source = null
//    )
//
//    MaterialTheme {
//        ArticleItem(article = sampleArticle)
//    }
//}
//
//@Composable
//@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
//fun PreviewNewsList() {
//    val sampleList = listOf(
//        Article(
//            author = "John Doe",
//            title = "Örnek Haber 1",
//            description = "Bu bir açıklamadır.",
//            url = "",
//            urlToImage = "",
//            publishedAt = "",
//            content = "",
//            source = null
//        ),
//        Article(
//            author = "Jane Roe",
//            title = "Örnek Haber 2",
//            description = "Bu ikinci açıklamadır.",
//            url = "",
//            urlToImage = "",
//            publishedAt = "",
//            content = "",
//            source = null
//        )
//    )
//
//    MaterialTheme {
//        NewsList(articles = sampleList)
//    }
//}
//
//@Composable
//@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
//fun PreviewNewsListScreen() {
//    // Fake bir Resource.Success veriyoruz
//    val sampleArticles = listOf(
//        Article(
//            author = "Test",
//            title = "Preview Haber",
//            description = "Preview açıklaması",
//            url = "",
//            urlToImage = "",
//            publishedAt = "",
//            content = "",
//            source = null
//        )
//    )
//
//    MaterialTheme {
//        NewsList(articles = sampleArticles)
//    }
//}
