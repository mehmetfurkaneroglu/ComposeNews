package com.eroglu.newsapp.data.api

import android.R.attr.level
import com.eroglu.newsapp.util.Constants.BASE_URL

import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

/**
 * Singleton Nedir?
 * Normalde bir sınıftan new (veya Kotlin'de direkt constructor çağrısı) ile her seferinde yeni bir kopya oluşturabilirsin.
 * Ancak Singleton deseninde, o sınıftan sadece bir tane üretilir ve uygulamanın her yerinden aynı nesneye erişilir.
 * Kotlin'de object anahtar kelimesini kullandığında, dil otomatik olarak o sınıfı Singleton yapar.
 * Yani sen istemesen de KtorfitInstance'tan ikinci bir tane oluşturamazsın.
 * Singleton kullanılmasının temel sebebi Performans ve Kaynak Yönetimidir.
 * Özetle; Singleton, "Bu nesne çok yer kaplıyor veya ayarları karmaşık, bir tane üretelim ve herkes o ortak nesneyi kullansın" demek için kullanılır.
 */
object KtorfitInstance {

    // 1. Ktor HttpClient (Retrofit'teki OkHttp client gibi)
    private val client = HttpClient(OkHttp) {
        // JSON Dönüştürücü Ayarı (Gson yerine bu)
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true // API'den bilmediğimiz bir alan gelirse çökme
                isLenient = true
                prettyPrint = true
            })
        }

        // Loglama (İsteğe bağlı, debug için)
        install(Logging) {
            level = LogLevel.BODY
        }
    }

    // 2. Ktorfit Builder
    private val ktorfit = Ktorfit.Builder()
        .baseUrl(BASE_URL)
        .httpClient(client)
        .build()

    // 3. API Oluşturma
    // Not: createNewsAPI() fonksiyonu KSP tarafından otomatik üretilir.
    // Eğer kod kırmızı yanarsa Projeyi Build/Rebuild yapmalısın.
    val api: NewsAPI by lazy {
        ktorfit.createNewsAPI()
    }
}