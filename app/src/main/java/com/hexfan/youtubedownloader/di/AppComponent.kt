package com.hexfan.youtubedownloader.di

import com.hexfan.youtubedownloader.MainApplication
import dagger.BindsInstance
import dagger.Component
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

/**
 * Created by Pawel on 14.02.2018.
 */
@Singleton
@Component(modules = arrayOf(AppModule::class, ActivityBuilder::class, AndroidSupportInjectionModule::class))
interface AppComponent {

    @Component.Builder
    interface Builder{
        @BindsInstance
        fun application(application: MainApplication): Builder

        fun build(): AppComponent
    }

    fun inject(application: MainApplication)
}