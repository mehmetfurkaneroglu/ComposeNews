package com.eroglu.newsapp.data.model

import kotlinx.serialization.Serializable

@Serializable
data class NewsResponse(
    val articles: MutableList<Article>,
    val status: String,
    val totalResults: Int
)