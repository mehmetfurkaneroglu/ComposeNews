package com.eroglu.newsapp.data.repository

import com.eroglu.newsapp.data.api.RetrofitInstance
import com.eroglu.newsapp.data.model.NewsResponse
import retrofit2.Response

class NewsRepository {
    // Son dakika haberlerini getir
    suspend fun getBreakingNews(countryCode: String, pageNumber: Int): Response<NewsResponse> {
        return RetrofitInstance.api.getBreakingNews(countryCode, pageNumber)
    }

    // Haber araması yap
    suspend fun searchNews(searchQuery: String, pageNumber: Int): Response<NewsResponse> {
        return RetrofitInstance.api.searchForNews(searchQuery, pageNumber)
    }
}