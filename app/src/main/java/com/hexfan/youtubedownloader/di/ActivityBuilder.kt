package com.hexfan.youtubedownloader.di

import com.hexfan.youtubedownloader.browse.BrowseActivity
import com.hexfan.youtubedownloader.browse.BrowseActivityModule
import com.hexfan.youtubedownloader.download.DownloadActivity
import com.hexfan.youtubedownloader.download.DownloadActivityModule
import com.hexfan.youtubedownloader.settings.SettingsActivity
import com.hexfan.youtubedownloader.settings.SettingsActivityModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * Created by Pawel on 14.02.2018.
 */
@Module
abstract class ActivityBuilder {

    @ContributesAndroidInjector(modules = arrayOf(DownloadActivityModule::class))
    abstract fun bindDownloadActivity(): DownloadActivity

    @ContributesAndroidInjector(modules = arrayOf(SettingsActivityModule::class))
    abstract fun bindSettingsActivity(): SettingsActivity

    @ContributesAndroidInjector(modules = arrayOf(BrowseActivityModule::class))
    abstract fun bindBrowseActivity(): BrowseActivity
}