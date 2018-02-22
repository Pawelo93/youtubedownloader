package com.hexfan.youtubedownloader.download

import android.content.Context
import com.hexfan.youtubedownloader.MainApplication
import com.hexfan.youtubedownloader.api.DownloadApiService
import com.hexfan.youtubedownloader.api.YoutubeService
import com.hexfan.youtubedownloader.api.YoutubeApiService
import com.hexfan.youtubedownloader.interactors.DownloadInteractor
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
    fun provideDownloadInteractor(context: MainApplication, youtubeService: YoutubeService): DownloadInteractor {
        return DownloadInteractor(context, youtubeService)
    }

    @Provides
    fun provideDownloadViewModelFactory(downloadInteractor: DownloadInteractor): DownloadViewModel.Factory{
        return DownloadViewModel.Factory(downloadInteractor)
    }
}