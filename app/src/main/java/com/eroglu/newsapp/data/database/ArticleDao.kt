package com.eroglu.newsapp.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.eroglu.newsapp.data.model.Article
import kotlinx.coroutines.flow.Flow

@Dao
interface ArticleDao {

    // Ekle veya Güncelle (Conflict olursa yenisiyle değiştir)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(article: Article): Long

    // Tüm haberleri getir
    @Query("SELECT * FROM articles")
    fun getAllArticles(): Flow<List<Article>> // Flow: Veri değişirse anlık haber verir

    // Haberi sil (Favorilerden çıkarma vs için)
    @Delete
    suspend fun deleteArticle(article: Article)

    // Cache temizliği için her şeyi sil (İleride lazım olabilir)
    @Query("DELETE FROM articles")
    suspend fun deleteAllArticles()
}