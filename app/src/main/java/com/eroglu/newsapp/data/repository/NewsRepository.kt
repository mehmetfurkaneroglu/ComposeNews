package com.eroglu.newsapp.data.repository

import com.eroglu.newsapp.data.api.KtorfitInstance
import com.eroglu.newsapp.data.model.NewsResponse
import retrofit2.Response

class NewsRepository {
    // Son dakika haberlerini getir
    suspend fun getBreakingNews(countryCode: String, pageNumber: Int) =
        KtorfitInstance.api.getBreakingNews(countryCode, pageNumber)

    // Haber araması yap
    suspend fun searchNews(searchQuery: String, pageNumber: Int) =
        KtorfitInstance.api.searchForNews(searchQuery, pageNumber)
}