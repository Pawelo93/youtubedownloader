package com.hexfan.youtubedownloader.api

import com.hexfan.youtubedownloader.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Created by Pawel on 15.02.2018.
 */
class AuthorizingInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val originalUrl = originalRequest.url()

        val newUrl = originalUrl.newBuilder()
                .addQueryParameter("key", BuildConfig.API_KEY)
                .build()

        val builder = originalRequest.newBuilder()
                .url(newUrl)

        return chain.proceed(builder.build())
    }
}