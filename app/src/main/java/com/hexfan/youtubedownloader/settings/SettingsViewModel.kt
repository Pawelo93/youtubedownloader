package com.hexfan.youtubedownloader.settings

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.hexfan.youtubedownloader.background
import com.hexfan.youtubedownloader.interactors.SettingsInteractor
import javax.inject.Inject

/**
 * Created by Pawel on 18.02.2018.
 */
class SettingsViewModel @Inject constructor(val settingsInteractor: SettingsInteractor) : ViewModel() {

//    val idValue = PublishSubject.create<String>()
    var idValue = MutableLiveData<String>()

//    fun subscribeId(body: (idValue: String) -> Unit) {
//        idValue.allowSubscribe(body)
//    }

    fun loadId() {
        settingsInteractor.loadPlaylistId()
                .background()
                .subscribe {
                    idValue.value = it
                }
    }

    fun saveId(id: String) {
        settingsInteractor.savePlaylistId(id)
                .background()
                .subscribe {

                }
    }

    class Factory @Inject constructor(private val settingsInteractor: SettingsInteractor) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return SettingsViewModel(settingsInteractor) as T
        }
    }
}