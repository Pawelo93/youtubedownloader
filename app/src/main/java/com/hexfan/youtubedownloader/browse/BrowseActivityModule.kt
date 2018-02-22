package com.hexfan.youtubedownloader.browse

import android.content.SharedPreferences
import com.hexfan.youtubedownloader.api.DownloadApiService
import com.hexfan.youtubedownloader.api.YoutubeApiService
import com.hexfan.youtubedownloader.api.YoutubeService
import com.hexfan.youtubedownloader.download.DownloadActivity
import com.hexfan.youtubedownloader.download.DownloadActivityModule
import com.hexfan.youtubedownloader.interactors.DownloadInteractor
import com.hexfan.youtubedownloader.interactors.SettingsInteractor
import com.hexfan.youtubedownloader.settings.SettingsActivityModule
import dagger.Module
import dagger.Provides

/**
 * Created by Pawel on 18.02.2018.
 */
@Module(includes = arrayOf(SettingsActivityModule::class, DownloadActivityModule::class))
class BrowseActivityModule {

    @Provides
    fun provideBrowseViewModelFactory(settingsInteractor: SettingsInteractor,
                                      downloadInteractor: DownloadInteractor): BrowseViewModel.Factory{
        return BrowseViewModel.Factory(settingsInteractor, downloadInteractor)
    }
}