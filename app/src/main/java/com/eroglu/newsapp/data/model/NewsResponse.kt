package com.eroglu.newsapp.data.model

data class NewsResponse(
    val articles: MutableList<Article>,
    val status: String,
    val totalResult: Int
)