package com.eroglu.newsapp.data.model

import kotlinx.serialization.Serializable

/**
 * Source (Haberin Kaynağı)
 * API yapısında haberin kaynağı sadece bir isim ("BBC News") olarak değil, kendi içinde id ve name barındıran ayrı bir obje olarak gelir.
 * id: Kaynağın benzersiz kimliği (örn: "bbc-news").
 * name: Kaynağın görünen adı (örn: "BBC News").
 */
@Serializable
data class Source(
    val id: String?,
    val name: String
)