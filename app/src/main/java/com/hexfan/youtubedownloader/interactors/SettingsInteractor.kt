package com.hexfan.youtubedownloader.interactors

import android.annotation.SuppressLint
import android.content.SharedPreferences
import io.reactivex.Observable
import javax.inject.Inject

/**
 * Created by Pawel on 18.02.2018.
 */
class SettingsInteractor @Inject constructor(val prefs: SharedPreferences) {

    companion object {
        val KEY_PLAYLIST_ID = "playlist_id"
    }

    fun loadPlaylistId(): Observable<String> {
        return Observable.fromCallable<String> {
            prefs.getString(KEY_PLAYLIST_ID, "")
        }
    }

    @SuppressLint("ApplySharedPref")
    fun savePlaylistId(id: String): Observable<Boolean> {
        return Observable.fromCallable {
            prefs.edit().putString(KEY_PLAYLIST_ID, id).commit()
        }
    }
}