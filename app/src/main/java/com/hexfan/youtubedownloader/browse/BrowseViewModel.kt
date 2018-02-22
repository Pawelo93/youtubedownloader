package com.hexfan.youtubedownloader.browse

import android.app.DownloadManager
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.content.Context
import android.database.sqlite.SQLiteException
import android.net.Uri
import com.hexfan.youtubedownloader.api.Item
import com.hexfan.youtubedownloader.background
import com.hexfan.youtubedownloader.interactors.DownloadInteractor
import com.hexfan.youtubedownloader.interactors.SettingsInteractor
import com.hexfan.youtubedownloader.youtube.ExtractResult
import com.hexfan.youtubedownloader.youtube.VideoMeta
import com.hexfan.youtubedownloader.youtube.YtFile
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.exceptions.OnErrorNotImplementedException
import timber.log.Timber
import java.io.File
import javax.inject.Inject

/**
 * Created by Pawel on 18.02.2018.
 */
class BrowseViewModel @Inject constructor(val settingsInteractor: SettingsInteractor,
                                          val downloadInteractor: DownloadInteractor) : ViewModel() {

    var playlist = MutableLiveData<List<Item>>()
    var itemStatus = MutableLiveData<SimpleItem>()

    fun subscribePlaylistId(): MutableLiveData<String> {
        val playlistId = MutableLiveData<String>()
        settingsInteractor.loadPlaylistId()
                .background()
                .subscribe {
                    playlistId.value = it
                }

        return playlistId
    }

    fun loadPlaylist(playlistId: String, videoFile: File) {
        downloadInteractor.loadPlaylist(playlistId)
                .background()
                .flatMap {
                    Observable.fromIterable(it)
                            .checkIfDownloaded(videoFile)
                            .toList()
                }
                .subscribe({
                    playlist.value = it
                }, {
                    Timber.e(it)
                })
    }

    fun download(videoId: String, manager: DownloadManager): Observable<Long> {
        return downloadInteractor.downloadFromId(videoId)
                .background()
                .map {
                    val ytFile: YtFile = it.ytFiles?.get(17)!!

                    val videoTitle = it.videoMeta!!.title
                    var fileName = if (videoTitle.length > 55) {
                        videoTitle.substring(0, 55) + "." + ytFile.format.ext
                    } else {
                        videoTitle + "." + ytFile.format.ext
                    }
                    fileName = fileName.replace(("\\\\|>|<|\"|\\||\\*|\\?|%|:|#|/").toRegex(), "")

                    val uri = Uri.parse(ytFile.url)
                    val request = DownloadManager.Request(uri)
                    request.setTitle(it.videoMeta.title)

                    request.allowScanningByMediaScanner()
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    request.setDestinationInExternalPublicDir("YoutubeDownloader/video", fileName)

                    manager.enqueue(request)
                }
    }

    fun subscribeProgress(id: Long, manager: DownloadManager): Observable<Int> {
        var downloading = true
        return Observable.create<Int> {
            while (downloading) {
                val q = DownloadManager.Query()
                q.setFilterById(id)
                try {
                    val cursor = manager.query(q)

                    if (cursor == null || !cursor.moveToFirst() || cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) ==
                            DownloadManager.STATUS_SUCCESSFUL) {
                        downloading = false
                        if(cursor != null)
                            cursor.close()
                        it.onComplete()
                        break
                    }

                    val bytesTotal = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
                    val bytesDownloaded = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))

                    it.onNext((bytesDownloaded * 100) / bytesTotal)

                    try {
                        Thread.sleep(200)
                    } catch (e: InterruptedException) {
                        Timber.e("Thread interrupted")
                    }

                } catch (e: SQLiteException) {

                }
            }
        }.background()
                .distinct()
    }

    fun Observable<Item>.checkIfDownloaded(videoFile: File): Observable<Item> {
        return map { item ->

            if (videoFile.listFiles() != null && videoFile.listFiles().any {
                        val title = item.snippet.title.replace(("\\\\|>|<|\"|\\||\\*|\\?|%|:|#|/").toRegex(), "")
                        it.nameWithoutExtension == title
                    })
                item.copy(progress = Item.DOWNLOADED)
            else
                item.copy(progress = Item.NOT_DOWNLOADED)
        }
    }

    fun loadVideo(ytUrl: String): Observable<ExtractResult> {
        return downloadInteractor.downloadFromLink(ytUrl)
    }


    data class MapStruc(val ytFile: YtFile, val videoMeta: VideoMeta, val fileName: String)

    data class SimpleItem(val videoId: String, val title: String, val progress: Int)

    fun downloadPlaylist(playlist: List<Item>, manager: DownloadManager) {


//        Observable.fromIterable(playlist)
//                .background()
//                .filter { it.progress == Item.NOT_DOWNLOADED }
//                .flatMap {
//                    Timber.e("Im in map")
//                    downloadInteractor.downloadFromId(it.contentDetails.videoId)
//                }
//                .filter { it.ytFiles != null && it.videoMeta != null }
//                .map {
//                    val ytFile: YtFile = it.ytFiles?.get(17)!!
//
//                    val videoTitle = it.videoMeta!!.title
//                    var filename = if (videoTitle.length > 55) {
//                        videoTitle.substring(0, 55) + "." + ytFile.format.ext
//                    } else {
//                        videoTitle + "." + ytFile.format.ext
//                    }
//                    filename = filename.replace(("\\\\|>|<|\"|\\||\\*|\\?|%|:|#|/").toRegex(), "")
//
//                    MapStruc(ytFile, it.videoMeta, filename)
//
//                }
//                .map {
//                    val uri = Uri.parse(it.ytFile.url)
//                    val request = DownloadManager.Request(uri)
//                    request.setTitle(it.videoMeta.title)
//
//                    request.allowScanningByMediaScanner()
//                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
//                    request.setDestinationInExternalPublicDir("YoutubeDownloader/video", it.fileName)
//
//                    Pair(it, manager.enqueue(request))
//                }
//                .downloadIndicator(manager)
//                .firstElement()
//
//                .subscribe ({
//                    itemStatus.value = it
//                }, {
//                    Timber.e("Im in throw $it")
//                })

    }

//    fun Observable<Pair<MapStruc, Long>>.downloadIndicator(manager: DownloadManager): Observable<SimpleItem> {
//        var downloading = true
//
//        return flatMap { pair ->
//            Observable.create<SimpleItem> {
//                while (downloading) {
//                    val q = DownloadManager.Query()
//                    q.setFilterById(pair.second)
//                    val cursor = manager.query(q)
//
//                    if (!cursor.moveToFirst() || cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) ==
//                            DownloadManager.STATUS_SUCCESSFUL) {
//                        downloading = false
//                        cursor.close()
//                        it.onComplete()
//                        break
//                    }
//
//                    val bytesTotal = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
//                    val bytesDownloaded = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
//
//                    it.onNext(SimpleItem(pair.first.videoMeta.videoId,
//                            pair.first.videoMeta.title,
//                            (bytesDownloaded * 100) / bytesTotal))
//                    try {
//                        Thread.sleep(200)
//                    } catch (e: InterruptedException) {
//                        Timber.e("Thread interrupted")
//                    }
//                }
//            }.background()
//                    .distinct()
//        }
//    }

    class Factory @Inject constructor(private val settingsInteractor: SettingsInteractor,
                                      private val downloadInteractor: DownloadInteractor) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return BrowseViewModel(settingsInteractor, downloadInteractor) as T
        }
    }
}