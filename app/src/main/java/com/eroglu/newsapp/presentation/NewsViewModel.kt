package com.eroglu.newsapp.presentation

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eroglu.newsapp.data.model.Article
import com.eroglu.newsapp.data.model.NewsResponse
import com.eroglu.newsapp.domain.repository.NewsRepository
import com.eroglu.newsapp.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class NewsViewModel(
    private val newsRepository: NewsRepository
) : ViewModel() {

    // Compose'un gözlemleyeceği state. Başlangıçta 'Loading' durumunda.
    val breakingNews = mutableStateOf<Resource<NewsResponse>>(Resource.Loading())
    var breakingNewsPage = 1

    val searchNews = mutableStateOf<Resource<NewsResponse>>(Resource.Loading())
    var searchNewsPage = 1

    init {
        // Uygulama açılır açılmaz haberleri çek
        getBreakingNews("us")
    }

    fun getBreakingNews(countryCode: String) = viewModelScope.launch {
        breakingNews.value = Resource.Loading()
        try {
            // 1. API'den veriyi çek
            val response = newsRepository.getBreakingNewsFromApi(countryCode, breakingNewsPage)

            // 2. Gelen her haberi Room veritabanına kaydet (Cache mantığı)
            // Not: response.articles nullable olabilir, kontrol ediyoruz
            // TODO: Tüm haberleri database ekledim fakat kaydedilenler ile çakıştı, ONDAN KAPATTIM
//            response.articles?.let { articles ->
//                for(article in articles) {
//                    newsRepository.upsert(article)
//                }
//            }

            // 3. UI'a başarılı olduğunu bildir
            breakingNews.value = Resource.Success(response)

        } catch (e: Exception) {
            breakingNews.value = Resource.Error("Hata: ${e.message}")
            e.printStackTrace()
        }
    }

    // 1. Seçilen Haberi Tutacak Değişken (Detay sayfası için)
    var currentArticle: Article? = null

    // 2. Kaydedilen Haberleri Getiren Flow (Room'dan canlı veri)
    fun getSavedNews(): Flow<List<Article>> {
        return newsRepository.getSavedNews()
    }

    // 3. Haberi Kaydetme Fonksiyonu
    fun saveArticle(article: Article) = viewModelScope.launch {
        newsRepository.upsert(article)
    }

    // 4. Haberi Silme Fonksiyonu
    fun deleteArticle(article: Article) = viewModelScope.launch {
        newsRepository.deleteArticle(article)
    }

    fun searchNews(searchQuery: String) = viewModelScope.launch {
        searchNews.value = Resource.Loading()
        try {
            val response = newsRepository.searchNewsFromApi(searchQuery, searchNewsPage)
            searchNews.value = Resource.Success(response)
        } catch (e: Exception) {
            searchNews.value = Resource.Error("Bir hata oluştu: ${e.message}")
            e.printStackTrace()
        }
    }
}