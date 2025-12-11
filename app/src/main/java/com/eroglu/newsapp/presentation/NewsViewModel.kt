package com.eroglu.newsapp.presentation

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eroglu.newsapp.data.model.Article
import com.eroglu.newsapp.data.model.NewsResponse
import com.eroglu.newsapp.domain.repository.NewsRepository
import com.eroglu.newsapp.util.Resource
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
            response.articles?.let { articles ->
                for(article in articles) {
                    newsRepository.upsert(article)
                }
            }

            // 3. UI'a başarılı olduğunu bildir
            breakingNews.value = Resource.Success(response)

        } catch (e: Exception) {
            breakingNews.value = Resource.Error("Hata: ${e.message}")
            e.printStackTrace()
        }
    }

    // Ayrıca favorilere eklemek için bir fonksiyon yazalım (Kullanıcı butona basınca çağıracak)
    fun saveArticle(article: Article) = viewModelScope.launch {
        newsRepository.upsert(article)
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