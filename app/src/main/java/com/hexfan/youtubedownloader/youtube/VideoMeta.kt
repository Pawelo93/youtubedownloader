package com.hexfan.youtubedownloader.youtube

/**
 * Created by Pawel on 14.02.2018.
 */
data class VideoMeta(
        val videoId: String,
        val title: String,
        val author: String,
        val channelId: String,
        val videoLength: Long,
        val viewCount: Long,
        val isLiveStream: Boolean){

    companion object {
        val IMAGE_BASE_URL = "http://i.ytimg.com/vi/"
    }

    // 120 x 90
    val thumbUrl: String
        get() = IMAGE_BASE_URL + videoId + "/default.jpg"

    // 320 x 180
    val mqThumbUrl: String
        get() = IMAGE_BASE_URL + videoId + "/mqdefault.jpg"

    // 480 x 360
    val hqThumbUrl: String
        get() = IMAGE_BASE_URL + videoId + "/hqdefault.jpg"

    // 640 x 480
    val sdThumbUrl: String
        get() = IMAGE_BASE_URL + videoId + "/sddefault.jpg"

    // Max Res
    val maxThumbUrl: String
        get() = IMAGE_BASE_URL + videoId + "/maxresdefault.jpg"
}