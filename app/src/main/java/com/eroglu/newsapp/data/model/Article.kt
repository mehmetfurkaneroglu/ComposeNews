package com.eroglu.newsapp.data.model

import kotlinx.serialization.Serializable // Dikkat: Bu import olmalı
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "articles")
@Serializable
data class Article(
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null, // Room için
    val author: String? = null,
    val content: String? = null,
    val description: String? = null,
    val publishedAt: String? = null,
    val source: Source? = null,
    val title: String? = null,
    val url: String? = null,
    val urlToImage: String? = null
)