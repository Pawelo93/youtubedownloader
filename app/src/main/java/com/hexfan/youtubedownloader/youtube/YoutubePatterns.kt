package com.hexfan.youtubedownloader.youtube

import java.util.regex.Pattern

/**
 * Created by Pawel on 15.02.2018.
 */
class YoutubePatterns {

    companion object {

        val patYouTubePageLink = Pattern.compile("(http|https)://(www\\.|m.|)youtube\\.com/watch\\?v=(.+?)( |\\z|&)")
        val patYouTubeShortLink = Pattern.compile("(http|https)://(www\\.|)youtu.be/(.+?)( |\\z|&)")

        val patDashManifest1 = Pattern.compile("dashmpd=(.+?)(&|\\z)")
        val patDashManifest2 = Pattern.compile("\"dashmpd\":\"(.+?)\"")
        val patDashManifestEncSig = Pattern.compile("/s/([0-9A-F|\\.]{10,}?)(/|\\z)")

        val patTitle = Pattern.compile("title=(.*?)(&|\\z)")
        val patAuthor = Pattern.compile("author=(.+?)(&|\\z)")
        val patChannelId = Pattern.compile("ucid=(.+?)(&|\\z)")
        val patLength = Pattern.compile("length_seconds=(\\d+?)(&|\\z)")
        val patViewCount = Pattern.compile("view_count=(\\d+?)(&|\\z)")

        val patHlsvp = Pattern.compile("hlsvp=(.+?)(&|\\z)")
        val patHlsItag = Pattern.compile("/itag/(\\d+?)/")

        val patItag = Pattern.compile("itag=([0-9]+?)(&|,)")
        val patEncSig = Pattern.compile("s=([0-9A-F|\\.]{10,}?)(&|,|\")")
        val patUrl = Pattern.compile("url=(.+?)(&|,)")

        val patVariableFunction = Pattern.compile("([{; =])([a-zA-Z$][a-zA-Z0-9$]{0,2})\\.([a-zA-Z$][a-zA-Z0-9$]{0,2})\\(")
        val patFunction = Pattern.compile("([{; =])([a-zA-Z\$_][a-zA-Z0-9$]{0,2})\\(")
        val patDecryptionJsFile = Pattern.compile("jsbin\\\\/(player-(.+?).js)")
        val patSignatureDecFunction = Pattern.compile("\"signature\",(.{1,3}?)\\(.{1,10}?\\)")

    }
}