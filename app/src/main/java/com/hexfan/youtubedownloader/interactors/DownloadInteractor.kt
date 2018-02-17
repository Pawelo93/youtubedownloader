package com.hexfan.youtubedownloader.interactors

import android.content.Context
import android.util.SparseArray
import android.view.View
import com.evgenii.jsevaluator.JsEvaluator
import com.evgenii.jsevaluator.interfaces.JsCallback
import com.hexfan.youtubedownloader.api.YoutubeService
import com.hexfan.youtubedownloader.youtube.*

import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_download.*
import timber.log.Timber
import java.net.URLDecoder
import javax.inject.Inject

/**
 * Created by Pawel on 14.02.2018.
 */
class DownloadInteractor @Inject constructor(private val context: Context,
                                             private val youtubeService: YoutubeService) {



    fun execute(youtubeLink: String): PublishSubject<ExtractResult>{
        val subject = PublishSubject.create<ExtractResult>()

        object : OldYouTubeExtractor(context) {

            public override fun onExtractionComplete(ytFiles: SparseArray<YtFile>?, vMeta: VideoMeta) {
                subject.onNext(ExtractResult(ytFiles, vMeta))
            }
        }.extract(youtubeLink, true, false)

        return subject
    }
}