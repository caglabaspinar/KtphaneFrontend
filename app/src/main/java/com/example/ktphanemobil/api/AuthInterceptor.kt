package com.example.ktphanemobil.api

import android.content.Context
import android.content.Intent
import com.example.ktphanemobil.ui.activities.SplashActivity
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val context: Context) : Interceptor {

    private var redirecting = false

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val url = originalRequest.url().toString()

        val isAuthEndpoint =
            url.contains("login") ||
                    url.contains("register") ||
                    url.contains("forgot-password") ||
                    url.contains("reset-password")

        val requestToSend = if (isAuthEndpoint) {
            originalRequest
        } else {
            val prefs = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
            val token = prefs.getString("auth_token", null)

            if (!token.isNullOrBlank()) {
                originalRequest.newBuilder()
                    .header("Authorization", "Bearer $token")
                    .build()
            } else {
                originalRequest
            }
        }

        val response = chain.proceed(requestToSend)

        if (!isAuthEndpoint && response.code() == 401 && !redirecting) {
            redirecting = true

            context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
                .edit()
                .clear()
                .apply()

            val intent = Intent(context, SplashActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            context.startActivity(intent)
        }

        return response
    }
}