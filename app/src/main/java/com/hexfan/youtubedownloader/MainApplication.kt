package com.hexfan.youtubedownloader

import android.app.Activity
import android.app.Application
import com.hexfan.youtubedownloader.di.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import timber.log.Timber
import timber.log.Timber.plant
import javax.inject.Inject

/**
 * Created by Pawel on 14.02.2018.
 */
class MainApplication: Application(), HasActivityInjector {

    @Inject
    protected lateinit var activityInjector: DispatchingAndroidInjector<Activity>

    override fun onCreate() {
        super.onCreate()

        DaggerAppComponent.builder()
                .application(this)
                .build()
                .inject(this)

        plant(Timber.DebugTree())
    }

    override fun activityInjector(): AndroidInjector<Activity> {
        return activityInjector
    }
}