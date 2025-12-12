package com.eroglu.newsapp.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.eroglu.newsapp.NewsActivity
import com.eroglu.newsapp.R
import com.eroglu.newsapp.data.database.ArticleDatabase
import com.eroglu.newsapp.domain.repository.NewsRepository

class NewsWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            Log.d("NewsWorker", "Arka plan işçisi çalışıyor...")

            // 1. Repository'yi Manuel Oluştur
            // Senin ArticleDatabase sınıfında invoke metodu olduğunu varsayıyoruz.
            val database = ArticleDatabase(applicationContext)
            val repository = NewsRepository(database)

            // 2. API İsteği (DİKKAT: Metod ismini düzelttik ve try-catch içine aldık)
            // Ktorfit/Retrofit direkt nesne döndüğü için, hata olursa Exception fırlatır.
            val newsResponse = repository.getBreakingNewsFromApi("tr", 1)

            // 3. Veriyi Kontrol Et (isSuccessful veya body() YOK, direkt nesne var)
            val newsList = newsResponse.articles

            if (!newsList.isNullOrEmpty()) {
                val latestNews = newsList[0]

                val gercekBaslik = latestNews.title ?: "Yeni Haber"
                val gercekDetay = latestNews.description ?: "Haber detayı için dokunun."

                // Veritabanına kaydet
                repository.upsert(latestNews)

                // Bildirim Gönder
                sendNotification(gercekBaslik, gercekDetay)

                Log.d("NewsWorker", "Başarılı: $gercekBaslik")
                Result.success()
            } else {
                Log.d("NewsWorker", "Haber listesi boş.")
                Result.success()
            }

        } catch (e: Exception) {
            // İnternet yoksa veya API çöktüyse buraya düşer
            Log.e("NewsWorker", "Hata oluştu: ${e.localizedMessage}")
            // Hata durumunda işlemi sonra tekrar denemesi için 'retry' diyoruz
            Result.retry()
        }
    }

    private fun sendNotification(title: String, message: String) {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "breaking_news_channel"
        val channelName = "Son Dakika Haberleri"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(applicationContext, NewsActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val builder = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // İkonun varsa değiştir
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        notificationManager.notify(1, builder.build())
    }
}