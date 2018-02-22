package com.hexfan.youtubedownloader

import android.icu.text.StringPrepParseException
import com.snatik.storage.Storage
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject

/**
 * Created by Pawel on 18.02.2018.
 */

fun <T> Observable<T>.background(): Observable<T>{
    return this.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
}

fun <T> Single<T>.background(): Single<T>{
    return this.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
}

fun <T> PublishSubject<T>.allowSubscribe(body: (result: T) -> Unit){
    this.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                body(it)
            }
}

fun Storage.createMyDirectory(){
    if(!isDirectoryExists(videoPath()))
        createDirectory(videoPath())
}

fun Storage.videoPathAbsolute(): String{
    return "${internalFilesDirectory}/YoutubeDownloader/video"
}

fun videoPath(): String{
    return "YoutubeDownloader/video"
}