package com.hexfan.youtubedownloader.youtube

import android.util.SparseArray
import com.hexfan.youtubedownloader.youtube.VideoMeta
import com.hexfan.youtubedownloader.youtube.YtFile

/**
 * Created by Pawel on 14.02.2018.
 */
data class ExtractResult(val ytFiles: SparseArray<YtFile>?, val videoMeta: VideoMeta?){

    lateinit var jsFile: String

    constructor(jsFile: String, videoMeta: VideoMeta) : this(SparseArray(), videoMeta){
        this.jsFile = jsFile
    }
}