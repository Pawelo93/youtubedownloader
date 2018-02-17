package com.hexfan.youtubedownloader.di

import android.app.Application
import android.content.Context
import com.hexfan.youtubedownloader.api.AuthorizingInterceptor
import com.hexfan.youtubedownloader.api.YoutubeApiService
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import javax.inject.Named
import javax.inject.Singleton

/**
 * Created by Pawel on 14.02.2018.
 */
@Module
class AppModule {

    @Provides
    fun applicationContext(application: Application): Context {
        return application
    }

    @Provides
    @Singleton
    fun authorizingInterceptor(): AuthorizingInterceptor {
        return AuthorizingInterceptor()
    }

    @Provides
    @Singleton
    fun retrofitWithoutAuthorization(httpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
                .baseUrl("https://www.googleapis.com/youtube/v3/")
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(httpClient)
                .build()
    }

    @Provides
    @Singleton
    fun youtubeApiServiceProvider(retrofit: Retrofit): YoutubeApiService {
        return retrofit.create(YoutubeApiService::class.java)
    }

    @Provides
    @Singleton
    fun loggingInterceptor(): HttpLoggingInterceptor {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BASIC
        return interceptor
    }

//    @Provides
//    @Named("withoutAuthorization")
//    @Singleton
//    fun httpClient(loggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
//        return OkHttpClient.Builder()
//                .addInterceptor(loggingInterceptor)
//                .build()
//    }

    @Provides
    @Singleton
    fun httpClientWithInterceptor(authorizingInterceptor: AuthorizingInterceptor,
                                  loggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
                .addInterceptor(authorizingInterceptor)
                .addInterceptor(loggingInterceptor)
                .build()
    }
}