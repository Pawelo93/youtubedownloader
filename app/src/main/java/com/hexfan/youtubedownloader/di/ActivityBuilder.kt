package com.hexfan.youtubedownloader.di

import com.hexfan.youtubedownloader.DownloadActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * Created by Pawel on 14.02.2018.
 */
@Module
abstract class ActivityBuilder {

//    @ContributesAndroidInjector(modules = arrayOf(MainActivityModule::class))
//    abstract fun bindMainActivity(): BrowseActivity

    @ContributesAndroidInjector(modules = arrayOf(DownloadActivityModule::class))
    abstract fun bindDownloadActivity(): DownloadActivity
}