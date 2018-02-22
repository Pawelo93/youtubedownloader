package com.hexfan.youtubedownloader.download

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.hexfan.youtubedownloader.interactors.DownloadInteractor
import com.hexfan.youtubedownloader.youtube.ExtractResult
import io.reactivex.Observable
import javax.inject.Inject

/**
 * Created by Pawel on 14.02.2018.
 */
class DownloadViewModel @Inject constructor(val downloadInteractor: DownloadInteractor)  : ViewModel() {

    fun loadVideo(ytUrl: String): Observable<ExtractResult>{
        return downloadInteractor.downloadFromLink(ytUrl)
    }

    open class Factory @Inject constructor(private val downloadInteractor: DownloadInteractor) : ViewModelProvider.Factory{
        open override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return DownloadViewModel(downloadInteractor) as T
        }

    }
}