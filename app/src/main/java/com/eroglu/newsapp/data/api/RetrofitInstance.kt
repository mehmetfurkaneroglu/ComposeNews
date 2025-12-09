package com.eroglu.newsapp.data.api

import com.eroglu.newsapp.util.Constants.BASE_URL
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    // Logcat'te request/response gövdesini görmek için interceptor
    private val retrofit by lazy {
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()) // JSON'ı Kotlin nesnesine çevirir
            .client(client)
            .build()
    }

    // API interface'imizi burada oluşturuyoruz
    val api: NewsAPI by lazy {
        retrofit.create(NewsAPI::class.java)
    }
}