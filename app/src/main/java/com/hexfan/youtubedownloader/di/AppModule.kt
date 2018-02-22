package com.hexfan.youtubedownloader.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.hexfan.youtubedownloader.MainApplication
import com.hexfan.youtubedownloader.api.AuthorizingInterceptor
import com.hexfan.youtubedownloader.api.YoutubeApiService
import com.snatik.storage.Storage
import com.squareup.picasso.Picasso
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
    @Singleton
    fun provideApplicationContext(application: Application): Context {
        return application
    }

    @Provides
    @Singleton
    fun provideSharedPreferences(application: MainApplication): SharedPreferences{
        return PreferenceManager.getDefaultSharedPreferences(application)
    }

    @Provides
    @Singleton
    fun providePicasso(application: MainApplication): Picasso{
        return Picasso.with(application)
    }

    @Provides
    @Singleton
    fun provideStorage(application: MainApplication): Storage{
        return Storage(application)
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