package com.hexfan.youtubedownloader.youtube

import android.util.SparseArray
import com.evgenii.jsevaluator.JsEvaluator
import com.evgenii.jsevaluator.interfaces.JsCallback
import com.hexfan.youtubedownloader.api.YoutubeService
import com.hexfan.youtubedownloader.youtube.YoutubePatterns.Companion.patAuthor
import com.hexfan.youtubedownloader.youtube.YoutubePatterns.Companion.patChannelId
import com.hexfan.youtubedownloader.youtube.YoutubePatterns.Companion.patDashManifest1
import com.hexfan.youtubedownloader.youtube.YoutubePatterns.Companion.patDashManifest2
import com.hexfan.youtubedownloader.youtube.YoutubePatterns.Companion.patDashManifestEncSig
import com.hexfan.youtubedownloader.youtube.YoutubePatterns.Companion.patDecryptionJsFile
import com.hexfan.youtubedownloader.youtube.YoutubePatterns.Companion.patEncSig
import com.hexfan.youtubedownloader.youtube.YoutubePatterns.Companion.patHlsvp
import com.hexfan.youtubedownloader.youtube.YoutubePatterns.Companion.patItag
import com.hexfan.youtubedownloader.youtube.YoutubePatterns.Companion.patLength
import com.hexfan.youtubedownloader.youtube.YoutubePatterns.Companion.patTitle
import com.hexfan.youtubedownloader.youtube.YoutubePatterns.Companion.patUrl
import com.hexfan.youtubedownloader.youtube.YoutubePatterns.Companion.patViewCount
import com.hexfan.youtubedownloader.youtube.YoutubePatterns.Companion.patYouTubePageLink
import com.hexfan.youtubedownloader.youtube.YoutubePatterns.Companion.patYouTubeShortLink
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import timber.log.Timber
import java.io.IOException
import java.lang.Long.parseLong
import java.net.URLDecoder
import java.net.URLEncoder
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * Created by Pawel on 14.02.2018.
 */
class YoutubeExtractor {

    companion object {
        val FORMAT_MAP = SparseArray<Format>()
        val LOGGING = true

    }


    private var useHttp = false

//    private val lock = ReentrantLock()
//    private val jsExecuting = lock.newCondition()
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
        var mat = patYouTubePageLink.matcher(ytUrl)
        if (mat.find()) {
            videoId = mat.group(3)
        } else {
            mat = patYouTubeShortLink.matcher(ytUrl)
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

    fun getInfoUrl(videoId: String): String{
        var ytInfoUrl = if (useHttp) "http://" else "https://"
        ytInfoUrl += ("www.youtube.com/get_video_info?video_id=" + videoId + "&eurl="
                + URLEncoder.encode("https://youtube.googleapis.com/v/" + videoId, "UTF-8"))

        Timber.e("Link $ytInfoUrl")

        return ytInfoUrl
    }

    fun getYtFilesFromOutputMap(outputMap: String, matcher: Matcher, url: String): SparseArray<YtFile>{
        val ytFiles = SparseArray<YtFile>()

        if (matcher.find()) {
            for (line in url.lines()) {
                if (line.startsWith("https://") || line.startsWith("http://")) {
                    val mat = YoutubePatterns.patHlsItag.matcher(line)
                    if (mat.find()) {
                        val itag = Integer.parseInt(mat.group(1))
                        val newFile = YtFile(FORMAT_MAP.get(itag), line)
                        ytFiles.put(itag, newFile)
                    }
                }
            }


        }
        if (ytFiles.size() == 0) {
            if (LOGGING)
                Timber.e("OutputMap $outputMap")
            throw Exception("Error 1")
        }
        return ytFiles
    }

    class Signature{

        var parseDashManifest = false
        var includeWebM = false

        var dashMpdUrl: String? = null
        var encSignatures = SparseArray<String>()
        var curJsFileName: String? = null
        var decipherJsFileName: String? = null
        var decipherFunctions: String? = null
        var decipherFunctionName: String? = null

        val publisher = PublishSubject.create<ExtractResult>()


        val ytFiles = SparseArray<YtFile>()


        fun getAll(oldOutputMap: String,
                   videoId: String,
                   videoMeta: VideoMeta,
                   youtubeService: YoutubeService,
                   jsEvaluator: JsEvaluator){

//            getYtFilesWithSignature(oldOutputMap, videoId, youtubeService)
//                    .subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe {
//                        jsEvaluator.evaluate(it, JsCallback {
//                            signature ->
//
//                                val sigs = signature.split("\n".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
//                                var i = 0
//                                while (i < encSignatures.size() && i < sigs.size) {
//                                    val key = encSignatures.keyAt(i)
//                                    if (key == 0) {
//                                        dashMpdUrl = dashMpdUrl?.replace("/s/" + encSignatures.get(key), "/signature/" + sigs[i])
//                                    } else {
//                                        var url = ytFiles.get(key).url
//                                        url =  url + "&signature=" + sigs[i]
//                                        val newFile = YtFile(FORMAT_MAP.get(key), url)
//                                        ytFiles.put(key, newFile)
//                                    }
//                                    i++
//
//                                }
//
//                            publisher.onNext(ExtractResult(ytFiles, videoMeta))
//                        })
//                    }

        }

        fun getYtFilesWithSignature(oldOutputMap: String,
                                    videoId: String,
                                    youtubeService: YoutubeService): Flowable<String>{
            // Some videoFile are using a ciphered signature we need to get the
            // deciphering js-file from the youtubepage.
            var outputMap = oldOutputMap
            var mat: Matcher

            if (oldOutputMap == null || !oldOutputMap.contains("use_cipher_signature=False")) {
                fun1(outputMap, videoId, youtubeService)
            } else {
                if (parseDashManifest) {
                    mat = patDashManifest1.matcher(outputMap)
                    if (mat.find()) {
                        dashMpdUrl = URLDecoder.decode(mat.group(1), "UTF-8")
                    }
                }
                outputMap = URLDecoder.decode(outputMap, "UTF-8")
            }

            val streams = outputMap.split(",|url_encoded_fmt_stream_map|&adaptive_fmts=".toRegex())
            for (encStream in streams) {
                var encStream = encStream + ","
                if (!encStream.contains("itag%3D")) {
                    continue
                }
                val stream: String
                stream = URLDecoder.decode(encStream, "UTF-8")

                mat = patItag.matcher(stream)
                val itag: Int
                if (mat.find()) {
                    itag = Integer.parseInt(mat.group(1))
                    if (LOGGING)
                        Timber.d("Itag found:" + itag)
                    if (FORMAT_MAP.get(itag) == null) {
                        if (LOGGING)
                            Timber.d("Itag not in list:" + itag)
                        continue
                    } else if (!includeWebM && FORMAT_MAP.get(itag).ext == "webm") {
                        continue
                    }
                } else {
                    continue
                }

                if (curJsFileName != null) {
                    mat = patEncSig.matcher(stream)
                    if (mat.find()) {
                        encSignatures.append(itag, mat.group(1))
                    }
                }
                mat = patUrl.matcher(encStream)
                var url: String? = null
                if (mat.find()) {
                    url = mat.group(1)
                }

                if (url != null) {
                    val format = FORMAT_MAP.get(itag)
                    val finalUrl = URLDecoder.decode(url, "UTF-8")
                    val newVideo = YtFile(format, finalUrl)
                    ytFiles.put(itag, newVideo)
                }
            }

            //
            if (encSignatures != null) {
                if (LOGGING)
                    Timber.d("Decipher signatures")
                val signature = decipherSignature(decipherFunctionName, decipherJsFileName,
                        decipherFunctions, encSignatures, youtubeService)
                return Flowable.just(signature)
            }

            //////// todo later
//        if (parseDashManifest && dashMpdUrl != null) {
//            for (i in 0 until DASH_PARSE_RETRIES) {
//                try {
//                    // It sometimes fails to connect for no apparent reason. We just retry.
//                    parseDashManifest(dashMpdUrl, ytFiles)
//                    break
//                } catch (io: IOException) {
//                    Thread.sleep(5)
//                    if (LOGGING)
//                        Timber.d("Failed to parse dash manifest " + (i + 1))
//                }
//
//            }
//        }
//
//        if (ytFiles.size() == 0) {
//            if (LOGGING)
//                Timber.d(outputMap)
//            return SparseArray()
//        }


            return Flowable.empty()
        }

        private fun fun1(outputMap: String, videoId: String, youtubeService: YoutubeService){
            // Get the video directly from the youtubepage

            val response = youtubeService.getUrl("https://youtube.com/watch?v=" + videoId).blockingFirst()

            var outputMap = outputMap
            for (line in response.lines()) {
                if (line.contains("url_encoded_fmt_stream_map")) {
                    outputMap = line.replace("\\u0026", "&")
                    break
                }
            }


            var mat = patDecryptionJsFile.matcher(outputMap)
            if (mat.find()) {
                curJsFileName = mat.group(1).replace("\\/", "/")
                if (decipherJsFileName == null || decipherJsFileName != curJsFileName) {
                    decipherFunctions = null
                    decipherFunctionName = null
                }
                decipherJsFileName = curJsFileName
            }

            if (parseDashManifest) {
                mat = patDashManifest2.matcher(outputMap)
                if (mat.find()) {
                    dashMpdUrl = mat.group(1).replace("\\/", "/")
                    mat = patDashManifestEncSig.matcher(dashMpdUrl)
                    if (mat.find()) {
                        encSignatures.append(0, mat.group(1))
                    } else {
                        dashMpdUrl = null
                    }
                }
            }
        }

        @Throws(IOException::class)
        private fun decipherSignature(oldDecipherFunctionName: String?,
                                      decipherJsFileName: String?,
                                      oldDecipherFunctions: String?,
                                      encSignatures: SparseArray<String>,
                                      youtubeService: YoutubeService
        ): String? {


            var decipherFunctions: String? = null
            var decipherFunctionName: String? = null
            var jsFile: String? = null

            // Assume the functions don't change that much
            if (oldDecipherFunctionName == null || oldDecipherFunctions == null) {
                val decipherFunctUrl = "https://s.ytimg.com/yts/jsbin/" + decipherJsFileName

                // todo connect to api
                val result = youtubeService.getUrl(decipherFunctUrl).blockingFirst()
                val sb = StringBuilder("")
                for (line in result.lines()) {
                    sb.append(line)
                    sb.append(" ")
                }
                val javascriptFile = sb.toString()

                if (LOGGING)
                    Timber.d("Decipher FunctURL: " + decipherFunctUrl)
                var mat = YoutubePatterns.patSignatureDecFunction.matcher(javascriptFile!!)
                if (mat.find()) {
                    decipherFunctionName = mat.group(1)
                    if (LOGGING)
                        Timber.d("Decipher Functname: " + decipherFunctionName)

                    val patMainVariable = Pattern.compile("(var |\\s|,|;)" + decipherFunctionName.replace("$", "\\$") +
                            "(=function\\((.{1,3})\\)\\{)")

                    var mainDecipherFunct: String

                    mat = patMainVariable.matcher(javascriptFile)
                    if (mat.find()) {
                        mainDecipherFunct = "var " + decipherFunctionName + mat.group(2)
                    } else {
                        val patMainFunction = Pattern.compile("function " + decipherFunctionName.replace("$", "\\$") +
                                "(\\((.{1,3})\\)\\{)")
                        mat = patMainFunction.matcher(javascriptFile)
                        if (!mat.find())
                            return null
                        mainDecipherFunct = "function " + decipherFunctionName + mat.group(2)
                    }

                    var startIndex = mat.end()

                    run {
                        var braces = 1
                        var i = startIndex
                        while (i < javascriptFile.length) {
                            if (braces == 0 && startIndex + 5 < i) {
                                mainDecipherFunct += javascriptFile.substring(startIndex, i) + ";"
                                break
                            }
                            if (javascriptFile[i] == '{')
                                braces++
                            else if (javascriptFile[i] == '}')
                                braces--
                            i++
                        }
                    }
                    decipherFunctions = mainDecipherFunct
                    // Search the main function for extra functions and variables
                    // needed for deciphering
                    // Search for variables
                    mat = YoutubePatterns.patVariableFunction.matcher(mainDecipherFunct)
                    while (mat.find()) {
                        val variableDef = "var " + mat.group(2) + "={"
                        if (decipherFunctions.contains(variableDef)) {
                            continue
                        }
                        startIndex = javascriptFile.indexOf(variableDef) + variableDef.length
                        var braces = 1
                        var i = startIndex
                        while (i < javascriptFile.length) {
                            if (braces == 0) {
                                decipherFunctions += variableDef + javascriptFile.substring(startIndex, i) + ";"
                                break
                            }
                            if (javascriptFile[i] == '{')
                                braces++
                            else if (javascriptFile[i] == '}')
                                braces--
                            i++
                        }
                    }
                    // Search for functions
                    mat = YoutubePatterns.patFunction.matcher(mainDecipherFunct)
                    while (mat.find()) {
                        val functionDef = "function " + mat.group(2) + "("
                        if (decipherFunctions.contains(functionDef)) {
                            continue
                        }
                        startIndex = javascriptFile.indexOf(functionDef) + functionDef.length
                        var braces = 0
                        var i = startIndex
                        while (i < javascriptFile.length) {
                            if (braces == 0 && startIndex + 5 < i) {
                                decipherFunctions += functionDef + javascriptFile.substring(startIndex, i) + ";"
                                break
                            }
                            if (javascriptFile[i] == '{')
                                braces++
                            else if (javascriptFile[i] == '}')
                                braces--
                            i++
                        }
                    }

                    if (LOGGING)
                        Timber.d("Decipher Function: " + decipherFunctions)
                    jsFile = decipherViaWebView(encSignatures, decipherFunctions, decipherFunctionName)
                } else {
                    return null
                }
            } else {
                jsFile = decipherViaWebView(encSignatures, decipherFunctions!!, decipherFunctionName!!)
            }

            return jsFile
        }

        private fun decipherViaWebView(encSignatures: SparseArray<String>,
                                       decipherFunctions: String,
                                       decipherFunctionName: String): String? {

            val stb = StringBuilder(decipherFunctions + " function decipher(")
            stb.append("){return ")
            for (i in 0 until encSignatures.size())
            {
                val key = encSignatures.keyAt(i)
                if (i < encSignatures.size() - 1)
                    stb.append(decipherFunctionName).append("('").append(encSignatures.get(key)).append("')+\"\\n\"+")
                else
                    stb.append(decipherFunctionName).append("('").append(encSignatures.get(key)).append("')")
            }
            stb.append("};decipher();")

            return stb.toString()

//        var decipheredSignature: String? = null
//
//
//        Timber.e("Thread ${Thread.currentThread().name}")

//        return Observable.just(evaluate(jsEvaluator, stb.toString()))
//                .subscribeOn(AndroidSchedulers.mainThread())
//                .observeOn(Schedulers.trampoline())
//                .blockingFirst()



//        jsEvaluator.evaluate(stb.toString()) { result ->
////            lock.lock()
//            println("Thread ${Thread.currentThread().name}")
//            Timber.e("Thread ${Thread.currentThread().name}")
//
//            decipheredSignature = result
//                jsExecuting.signal()
////            try {
////            } finally {
////                lock.unlock()
////            }
//        }

        }
    }










//    fun getYtFilesFromSignature(signature: String?): SparseArray<YtFile>{
//        val ytFiles = SparseArray<YtFile>()
//        if (signature == null) {
//            return SparseArray()
//        } else {
//            val sigs = signature.split("\n".toRegex())
//            var i = 0
//            while (i < encSignatures.size() && i < sigs.size) {
//                val key = encSignatures.keyAt(i)
//                if (key == 0) {
//                    dashMpdUrl = dashMpdUrl?.replace("/s/" + encSignatures.get(key), "/signature/" + sigs[i])
//                } else {
//                    var url = ytFiles.get(key).url
//                    url += "&signature=" + sigs[i]
//                    val newFile = YtFile(FORMAT_MAP.get(key), url)
//                    ytFiles.put(key, newFile)
//                }
//                i++
//            }
//        }
//        return ytFiles
//    }

//    private fun parseDashManifest(dashManifest: String?, ytFiles: SparseArray<YtFile>) {
//        val patBaseUrl = Pattern.compile("<BaseURL yt:contentLength=\"[0-9]+?\">(.+?)</BaseURL>")
//
//        if (dashManifest == null)
//            return
//        val mat = patBaseUrl.matcher(dashManifest)
//        while (mat.find()) {
//            val itag: Int
//            var url = mat.group(1)
//            val mat2 = patItag.matcher(url)
//            if (mat2.find()) {
//                itag = Integer.parseInt(mat2.group(1))
//                if (FORMAT_MAP.get(itag) == null)
//                    continue
//                if (!includeWebM && FORMAT_MAP.get(itag).ext == "webm")
//                    continue
//            } else {
//                continue
//            }
//            url = url.replace("&amp;", "&").replace(",", "%2C").replace("mime=audio/", "mime=audio%2F").replace("mime=video/", "mime=video%2F")
//            val yf = YtFile(FORMAT_MAP.get(itag), url)
//            ytFiles.append(itag, yf)
//        }
//
//
//    }

    fun parseVideoMeta(videoId: String, outputMap: String): VideoMeta {
        var isLiveStream = false
        var title: String? = null
        var author: String? = null
        var channelId: String? = null
        var viewCount: Long = 0
        var length: Long = 0
        var mat = patTitle.matcher(outputMap)
        if (mat.find()) {
            title = URLDecoder.decode(mat.group(1), "UTF-8")
        }

        mat = patHlsvp.matcher(outputMap)
        if (mat.find())
            isLiveStream = true

        mat = patAuthor.matcher(outputMap)
        if (mat.find()) {
            author = URLDecoder.decode(mat.group(1), "UTF-8")
        }
        mat = patChannelId.matcher(outputMap)
        if (mat.find()) {
            channelId = mat.group(1)
        }
        mat = patLength.matcher(outputMap)
        if (mat.find()) {
            length = parseLong(mat.group(1))
        }
        mat = patViewCount.matcher(outputMap)
        if (mat.find()) {
            viewCount = parseLong(mat.group(1))
        }

        return VideoMeta(videoId, title!!, author!!, channelId!!, length, viewCount, isLiveStream)
    }

//    data class DecipherSignatureResult(val flag: Boolean, val )




    interface ExtractorContract{
        fun evaluate(from: String): String
    }

}