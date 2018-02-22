package com.hexfan.youtubedownloader.interactors

import android.content.Context
import android.util.SparseArray
import com.hexfan.youtubedownloader.api.Item
import com.hexfan.youtubedownloader.api.YoutubeService
import com.hexfan.youtubedownloader.youtube.*
import io.reactivex.Observable

import io.reactivex.Single
import io.reactivex.subjects.PublishSubject
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

/**
 * Created by Pawel on 14.02.2018.
 */
class DownloadInteractor @Inject constructor(private val context: Context,
                                             private val youtubeService: YoutubeService) {


    fun downloadFromId(videoId: String): Observable<ExtractResult> {
        return downloadFromLink("https://youtu.be/$videoId")
    }

    fun downloadFromLink(youtubeLink: String): Observable<ExtractResult>{
        val subject = PublishSubject.create<ExtractResult>()

        object : OldYouTubeExtractor(context) {

            public override fun onExtractionComplete(ytFiles: SparseArray<YtFile>?, vMeta: VideoMeta) {
                subject.onNext(ExtractResult(ytFiles, vMeta))
            }
        }.extract(youtubeLink, true, false)

        return subject
    }

    fun loadPlaylist(playlistId: String): Single<List<Item>>{
        return youtubeService.getPlaylist(playlistId)
                .map { it.items }
                .map { parseDate(it) }
    }

    private fun parseDate(list: List<Item>) : List<Item>{
        val originalFormat = SimpleDateFormat("YYYY-MM-DD", Locale.ENGLISH)
        val targetFormat = SimpleDateFormat("dd.MM.yyyy")

        for (item in list) {
            val date = originalFormat.parse(item.contentDetails.videoPublishedAt.split("T")[0])
            item.contentDetails.videoPublishedAt = targetFormat.format(date)  // 20120821
        }
        return list
    }
}