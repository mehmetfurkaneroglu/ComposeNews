package com.eroglu.newsapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "articles")

data class Article(
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null, // Room için ID, API'den gelmiyor
    val author: String?,
    val content: String?,
    val description: String?,
    val publishedAt: String?,
    val source: Source?,
    val title: String?,
    val url: String?,
    val urlToImage: String?
) : Serializable