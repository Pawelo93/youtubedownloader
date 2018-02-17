package com.hexfan.youtubedownloader

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.content.Context
import com.evgenii.jsevaluator.interfaces.JsCallback
import com.hexfan.youtubedownloader.interactors.DownloadInteractor
import com.hexfan.youtubedownloader.youtube.ExtractResult
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Pawel on 14.02.2018.
 */
class DownloadViewModel @Inject constructor(val downloadInteractor: DownloadInteractor)  : ViewModel() {

    fun loadVideo(ytUrl: String): Observable<ExtractResult>{
        return downloadInteractor.execute(ytUrl)
    }
    
    open class Factory @Inject constructor(private val downloadInteractor: DownloadInteractor) : ViewModelProvider.Factory{
        open override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return DownloadViewModel(downloadInteractor) as T
        }

    }
}