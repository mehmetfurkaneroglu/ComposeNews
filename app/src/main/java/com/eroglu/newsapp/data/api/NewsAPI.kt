package com.eroglu.newsapp.data.api

import com.eroglu.newsapp.data.model.NewsResponse
import com.eroglu.newsapp.util.Constants.API_KEY
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Query

interface NewsAPI {

    @GET("v2/top-headlines")
    suspend fun getBreakingNews(
        @Query("country")
        countryCode: String = "tr", // Türkiye haberleri varsayılan
        @Query("page")
        pageNumber: Int = 1,
        @Query("apiKey")
        apiKey: String = API_KEY
    ): NewsResponse
    // DİKKAT: Retrofit'teki 'Response<NewsResponse>' yerine direkt 'NewsResponse' dönebiliriz
    // veya HttpResponse dönebiliriz. Şimdilik direkt nesneyi alalım, hata yönetimini try-catch ile yaparız.

    @GET("v2/everything")
    suspend fun searchForNews(
        @Query("q")
        searchQuery: String,
        @Query("page")
        pageNumber: Int = 1,
        @Query("apiKey")
        apiKey: String = API_KEY
    ): NewsResponse
}