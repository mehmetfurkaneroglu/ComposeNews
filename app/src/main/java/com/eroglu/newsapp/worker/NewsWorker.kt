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

class NewsWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        // ... Önceki API ve Veritabanı kodların burada duruyor ...

        // ÖRNEK SENARYO:
        // Diyelim ki API'den veriyi çektin ve haber başlığı şu geldi:
        val haberBasligi = "Son Dakika: Android Geliştiricileri İçin Yeni Güncelleme!"
        val haberDetayi = "Google yeni yapay zeka araçlarını duyurdu..."

        // Ve sihirli dokunuş: BİLDİRİMİ GÖNDER
        sendNotification(haberBasligi, haberDetayi)

        return Result.success()
    }

    // --- İŞTE YENİ EKLEYECEĞİMİZ FONKSİYON ---
    private fun sendNotification(title: String, message: String) {
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "breaking_news_channel"
        val channelName = "Son Dakika Haberleri"

        // 1. Bildirim Kanalı Oluştur (Android 8.0 ve üzeri için ZORUNLU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH // Önemli! Sesli ve titreşimli uyarı için.
            )
            notificationManager.createNotificationChannel(channel)
        }

        // 2. Bildirime tıklayınca nereye gidecek? (MainActivity'ye)
        // Kendi MainActivity ismin neyse onu yaz
        val intent = Intent(applicationContext, NewsActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            // Android 12+ güvenliği için FLAG_IMMUTABLE şart
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // 3. Bildirimi Tasarla
        val builder = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // PROJENDEKİ BİR İKONU SEÇ (Zorunlu)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent) // Tıklama aksiyonu
            .setAutoCancel(true) // Tıklayınca bildirim kaybolsun

        // 4. Göster! (ID her seferinde farklı olursa bildirimler üst üste birikir, 1 dersen günceller)
        notificationManager.notify(1, builder.build())
    }
}