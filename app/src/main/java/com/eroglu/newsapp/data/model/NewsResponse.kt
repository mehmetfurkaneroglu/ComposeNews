package com.eroglu.newsapp.data.model

import kotlinx.serialization.Serializable

// NewsResponse Modeli: API'den dönen ana cevabı karşılamak için
/**
 * NewsResponse (En Dış Katman)
 * API'ye bir istek attığında (örneğin "son dakika haberlerini getir"), sana tek bir haber değil, bir paket döner. Bu paketin içinde sadece haberler değil, işlemin durumu ve toplam kaç haber olduğu bilgisi de vardır.
 * NewsResponse sınıfı bu ana paketi karşılar:
 * status: İsteğin başarılı olup olmadığını belirtir (örn: "ok").
 * totalResults: Toplam kaç haber bulunduğunu söyler.
 * articles: Asıl haberlerin bulunduğu listedir.
 */
@Serializable
data class NewsResponse(
    val articles: MutableList<Article>,
    val status: String,
    val totalResults: Int
)

/*

{
    "status": "ok",           // -> NewsResponse
    "totalResults": 38,       // -> NewsResponse
    "articles": [             // -> NewsResponse içindeki List<Article>
    {
        "title": "Haber Başlığı",  // -> Article
        "description": "Detaylar...", // -> Article
        "source": {                // -> Article içindeki Source nesnesi
        "id": "cnn",           // -> Source
        "name": "CNN"          // -> Source
    }
    },
    ...
    ]
}

*/