package com.hexfan.youtubedownloader.youtube

import android.util.SparseArray
import timber.log.Timber

/**
 * Created by Pawel on 16.02.2018.
 */
class You2 {

    val LOGGING = true

    private var useHttp = false
    var includeWebM = false
    var parseDashManifest = false

    val FORMAT_MAP = SparseArray<Format>()
    private val DASH_PARSE_RETRIES = 5

    init {
        // http://en.wikipedia.org/wiki/YouTube#Quality_and_formats

        // Video and Audio
        FORMAT_MAP.put(17, Format(17, "3gp", 144, Format.VCodec.MPEG4, Format.ACodec.AAC, 24, false))
        FORMAT_MAP.put(36, Format(36, "3gp", 240, Format.VCodec.MPEG4, Format.ACodec.AAC, 32, false))
        FORMAT_MAP.put(5, Format(5, "flv", 240, Format.VCodec.H263, Format.ACodec.MP3, 64, false))
        FORMAT_MAP.put(43, Format(43, "webm", 360, Format.VCodec.VP8, Format.ACodec.VORBIS, 128, false))
        FORMAT_MAP.put(18, Format(18, "mp4", 360, Format.VCodec.H264, Format.ACodec.AAC, 96, false))
        FORMAT_MAP.put(22, Format(22, "mp4", 720, Format.VCodec.H264, Format.ACodec.AAC, 192, false))

        // Dash Video
//        FORMAT_MAP.put(160, Format(160, "mp4", 144, Format.VCodec.H264, Format.ACodec.NONE, true))
//        FORMAT_MAP.put(133, Format(133, "mp4", 240, Format.VCodec.H264, Format.ACodec.NONE, true))
//        FORMAT_MAP.put(134, Format(134, "mp4", 360, Format.VCodec.H264, Format.ACodec.NONE, true))
//        FORMAT_MAP.put(135, Format(135, "mp4", 480, Format.VCodec.H264, Format.ACodec.NONE, true))
//        FORMAT_MAP.put(136, Format(136, "mp4", 720, Format.VCodec.H264, Format.ACodec.NONE, true))
//        FORMAT_MAP.put(137, Format(137, "mp4", 1080, Format.VCodec.H264, Format.ACodec.NONE, true))
//        FORMAT_MAP.put(264, Format(264, "mp4", 1440, Format.VCodec.H264, Format.ACodec.NONE, true))
//        FORMAT_MAP.put(266, Format(266, "mp4", 2160, Format.VCodec.H264, Format.ACodec.NONE, true))
//
//        FORMAT_MAP.put(298, Format(298, "mp4", 720, Format.VCodec.H264, 60, Format.ACodec.NONE, true))
//        FORMAT_MAP.put(299, Format(299, "mp4", 1080, Format.VCodec.H264, 60, Format.ACodec.NONE, true))
//
//        // Dash Audio
//        FORMAT_MAP.put(140, Format(140, "m4a", Format.VCodec.NONE, Format.ACodec.AAC, 128, true))
//        FORMAT_MAP.put(141, Format(141, "m4a", Format.VCodec.NONE, Format.ACodec.AAC, 256, true))
//
//        // WEBM Dash Video
//        FORMAT_MAP.put(278, Format(278, "webm", 144, Format.VCodec.VP9, Format.ACodec.NONE, true))
//        FORMAT_MAP.put(242, Format(242, "webm", 240, Format.VCodec.VP9, Format.ACodec.NONE, true))
//        FORMAT_MAP.put(243, Format(243, "webm", 360, Format.VCodec.VP9, Format.ACodec.NONE, true))
//        FORMAT_MAP.put(244, Format(244, "webm", 480, Format.VCodec.VP9, Format.ACodec.NONE, true))
//        FORMAT_MAP.put(247, Format(247, "webm", 720, Format.VCodec.VP9, Format.ACodec.NONE, true))
//        FORMAT_MAP.put(248, Format(248, "webm", 1080, Format.VCodec.VP9, Format.ACodec.NONE, true))
//        FORMAT_MAP.put(271, Format(271, "webm", 1440, Format.VCodec.VP9, Format.ACodec.NONE, true))
//        FORMAT_MAP.put(313, Format(313, "webm", 2160, Format.VCodec.VP9, Format.ACodec.NONE, true))
//
//        FORMAT_MAP.put(302, Format(302, "webm", 720, Format.VCodec.VP9, 60, Format.ACodec.NONE, true))
//        FORMAT_MAP.put(308, Format(308, "webm", 1440, Format.VCodec.VP9, 60, Format.ACodec.NONE, true))
//        FORMAT_MAP.put(303, Format(303, "webm", 1080, Format.VCodec.VP9, 60, Format.ACodec.NONE, true))
//        FORMAT_MAP.put(315, Format(315, "webm", 2160, Format.VCodec.VP9, 60, Format.ACodec.NONE, true))
//
//        // WEBM Dash Audio
//        FORMAT_MAP.put(171, Format(171, "webm", Format.VCodec.NONE, Format.ACodec.VORBIS, 128, true))
//
//        FORMAT_MAP.put(249, Format(249, "webm", Format.VCodec.NONE, Format.ACodec.OPUS, 48, true))
//        FORMAT_MAP.put(250, Format(250, "webm", Format.VCodec.NONE, Format.ACodec.OPUS, 64, true))
//        FORMAT_MAP.put(251, Format(251, "webm", Format.VCodec.NONE, Format.ACodec.OPUS, 160, true))
//
//        // HLS Live Stream
//        FORMAT_MAP.put(91, Format(91, "mp4", 144, Format.VCodec.H264, Format.ACodec.AAC, 48, false, true))
//        FORMAT_MAP.put(92, Format(92, "mp4", 240, Format.VCodec.H264, Format.ACodec.AAC, 48, false, true))
//        FORMAT_MAP.put(93, Format(93, "mp4", 360, Format.VCodec.H264, Format.ACodec.AAC, 128, false, true))
//        FORMAT_MAP.put(94, Format(94, "mp4", 480, Format.VCodec.H264, Format.ACodec.AAC, 128, false, true))
//        FORMAT_MAP.put(95, Format(95, "mp4", 720, Format.VCodec.H264, Format.ACodec.AAC, 256, false, true))
//        FORMAT_MAP.put(96, Format(96, "mp4", 1080, Format.VCodec.H264, Format.ACodec.AAC, 256, false, true))

    }

    fun getVideoId(ytUrl: String): String{
        var videoId: String? = null
        var mat = YoutubePatterns.patYouTubePageLink.matcher(ytUrl)
        if (mat.find()) {
            videoId = mat.group(3)
        } else {
            mat = YoutubePatterns.patYouTubeShortLink.matcher(ytUrl)
            if (mat.find()) {
                videoId = mat.group(3)
            } else if (ytUrl.matches("\\p{Graph}+?".toRegex())) {
                videoId = ytUrl
            }
        }

        if(videoId == null) {
            Timber.e("Wrong YouTube link format link: $ytUrl")
        }

        // todo fix this line
        return videoId!!
    }
}