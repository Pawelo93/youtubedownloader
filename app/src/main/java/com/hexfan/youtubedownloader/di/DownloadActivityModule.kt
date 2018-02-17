package com.hexfan.youtubedownloader.di

import com.evgenii.jsevaluator.JsEvaluator
import com.hexfan.youtubedownloader.DownloadActivity
import com.hexfan.youtubedownloader.api.DownloadApiService
import com.hexfan.youtubedownloader.api.YoutubeService
import com.hexfan.youtubedownloader.api.YoutubeApiService
import com.hexfan.youtubedownloader.interactors.DownloadInteractor
import com.hexfan.youtubedownloader.DownloadViewModel
import com.hexfan.youtubedownloader.youtube.YoutubeExtractor
import dagger.Module
import dagger.Provides

/**
 * Created by Pawel on 14.02.2018.
 */
@Module
class DownloadActivityModule {

    @Provides
    fun provideDownloadApiService(service: YoutubeApiService): YoutubeService {
        return DownloadApiService(service)
    }

    @Provides
    fun provideJsEvaluator(activity: DownloadActivity): JsEvaluator{
        return JsEvaluator(activity)
    }

    @Provides
    fun provideDownloadInteractor(activity: DownloadActivity, youtubeService: YoutubeService): DownloadInteractor {
        return DownloadInteractor(activity, youtubeService)
    }

    @Provides
    fun provideDownloadViewModelFactory(downloadInteractor: DownloadInteractor): DownloadViewModel.Factory{
        return DownloadViewModel.Factory(downloadInteractor)
    }
}