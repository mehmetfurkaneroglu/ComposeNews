package com.eroglu.newsapp.data.model

import kotlinx.serialization.Serializable
import androidx.room.Entity
import androidx.room.PrimaryKey

// Article Modeli: Bu ana haber nesnemiz
/**
 * Article (Haberin Kendisi)
 * NewsResponse içindeki articles listesinin her bir elemanı bir Article objesidir. Bu sınıf, bir haberin başlığı, açıklaması, resmi ve yayınlanma tarihi gibi detaylarını tutar.
 * title, description, urlToImage vb. alanlar buradadır.
 * Ayrıca her haberin bir kaynağı (Hürriyet, CNN, BBC vb.) olduğu için içinde source isimli bir nesne daha barındırır.
 */
@Entity(tableName = "articles")
@Serializable
data class Article(
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null, // Room için ID, API'den gelmiyor
    val author: String? = null,
    val content: String? = null,
    val description: String? = null,
    val publishedAt: String? = null,
    val source: Source? = null,
    val title: String? = null,
    val url: String? = null,
    val urlToImage: String? = null,
    var isFavorite: Boolean? = false
)