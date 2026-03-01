package com.example.ktphanemobil.api

import android.content.Context
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val context: Context) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()


        val path = originalRequest.url().encodedPath().lowercase()

        val isAuthEndpoint = path.contains("login") || path.contains("register")

        if (isAuthEndpoint) {
            return chain.proceed(originalRequest)
        }

        val prefs = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val token = prefs.getString("auth_token", null)

        val newRequest = if (!token.isNullOrBlank()) {
            originalRequest.newBuilder()
                .header("Authorization", "Bearer $token") // addHeader yerine header
                .build()
        } else {
            originalRequest
        }

        return chain.proceed(newRequest)
    }
}

// OkHttp interceptor sınıfıdır; login ve register dışındaki tüm API isteklerine
// SharedPreferences’ta saklanan JWT token’ı Authorization header olarak otomatik ekler.


