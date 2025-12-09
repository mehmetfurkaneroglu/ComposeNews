package com.eroglu.newsapp.util

// Sealed class kullanarak durumları kısıtlıyoruz, böylece 'when' bloklarında kontrol kolaylaşıyor.
sealed class Resource<T>(val data: T? = null, val message: String? = null) {
    class Success<T>(data: T) : Resource<T>(data)
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)
    class Loading<T> : Resource<T>()
}