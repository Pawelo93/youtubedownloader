package com.hexfan.youtubedownloader.settings

import android.content.SharedPreferences
import com.hexfan.youtubedownloader.api.DownloadApiService
import com.hexfan.youtubedownloader.api.YoutubeApiService
import com.hexfan.youtubedownloader.api.YoutubeService
import com.hexfan.youtubedownloader.download.DownloadActivity
import com.hexfan.youtubedownloader.download.DownloadViewModel
import com.hexfan.youtubedownloader.interactors.DownloadInteractor
import com.hexfan.youtubedownloader.interactors.SettingsInteractor
import dagger.Module
import dagger.Provides

/**
 * Created by Pawel on 18.02.2018.
 */
@Module
class SettingsActivityModule {

//    @Provides
//    fun provideDownloadApiService(service: YoutubeApiService): YoutubeService {
//        return DownloadApiService(service)
//    }
//
    @Provides
    fun provideSettingsInteractor(sharedPreferences: SharedPreferences): SettingsInteractor {
        return SettingsInteractor(sharedPreferences)
    }

    @Provides
    fun provideSettingsViewModelFactory(settingsInteractor: SettingsInteractor): SettingsViewModel.Factory{
        return SettingsViewModel.Factory(settingsInteractor)
    }
}