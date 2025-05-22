package com.example.newsapp.network


import android.content.Context
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class NetworkConnectionInterceptor(
    private val context: Context
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        if (!NetworkConfig.isInternetConnected(context)) {
            throw IOException()
        }
        return chain.proceed(chain.request())
    }
}
