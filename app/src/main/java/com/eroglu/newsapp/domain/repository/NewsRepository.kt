package com.eroglu.newsapp.domain.repository

import com.eroglu.newsapp.data.api.KtorfitInstance
import com.eroglu.newsapp.data.database.ArticleDatabase
import com.eroglu.newsapp.data.model.Article
import com.eroglu.newsapp.data.model.NewsResponse
import retrofit2.Response

class NewsRepository(
    private val db: ArticleDatabase // Veritabanını parametre olarak alıyoruz
) {
    // API'den veri çek
    suspend fun getBreakingNewsFromApi(countryCode: String, pageNumber: Int) =
        KtorfitInstance.api.getBreakingNews(countryCode, pageNumber)

    suspend fun searchNewsFromApi(searchQuery: String, pageNumber: Int) =
        KtorfitInstance.api.searchForNews(searchQuery, pageNumber)

    // Veritabanı İşlemleri
    suspend fun upsert(article: Article) = db.getArticleDao().upsert(article)

    // Kayıtlı haberleri getir
    fun getSavedNews() = db.getArticleDao().getAllArticles()

    suspend fun deleteArticle(article: Article) = db.getArticleDao().deleteArticle(article)
}