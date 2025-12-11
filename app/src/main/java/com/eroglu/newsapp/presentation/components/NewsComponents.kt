package com.eroglu.newsapp.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.eroglu.newsapp.data.model.Article

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