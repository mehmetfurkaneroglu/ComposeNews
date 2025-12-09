package com.eroglu.newsapp.presentation

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eroglu.newsapp.data.model.NewsResponse
import com.eroglu.newsapp.data.repository.NewsRepository
import com.eroglu.newsapp.util.Resource
import kotlinx.coroutines.launch
import retrofit2.Response

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
            val response = newsRepository.getBreakingNews(countryCode, breakingNewsPage)
            breakingNews.value = handleBreakingNewsResponse(response)
        } catch (e: Exception) {
            // Hatayı Logcat'e yazdırıyoruz
            Log.e("NewsViewModel", "HATA OLUŞTU: ${e.message}")
            e.printStackTrace()
            breakingNews.value = Resource.Error("Bir hata oluştu: ${e.message}")
        }
    }

    fun searchNews(searchQuery: String) = viewModelScope.launch {
        searchNews.value = Resource.Loading()
        try {
            val response = newsRepository.searchNews(searchQuery, searchNewsPage)
            searchNews.value = handleSearchNewsResponse(response)
        } catch (e: Exception) {
            searchNews.value = Resource.Error("Bir hata oluştu: ${e.message}")
        }
    }

    // API Cevabını İşleyen Yardımcı Fonksiyon
    private fun handleBreakingNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    private fun handleSearchNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message())
    }
}