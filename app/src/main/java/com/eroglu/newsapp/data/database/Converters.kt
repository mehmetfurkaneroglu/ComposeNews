package com.eroglu.newsapp.data.database

import androidx.room.TypeConverter
import com.eroglu.newsapp.data.model.Source

class Converters {
    @TypeConverter
    fun fromSource(source: Source): String {
        return source.name // Sadece kaynağın ismini kaydetmek yeterli
    }

    @TypeConverter
    fun toSource(name: String): Source {
        return Source(id = name, name = name) // String'den tekrar Source oluştur
    }
}