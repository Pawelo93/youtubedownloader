package com.hexfan.youtubedownloader

import android.app.DownloadManager
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.support.v7.app.AppCompatActivity
import android.util.SparseArray
import android.view.View
import android.widget.Button
import com.hexfan.youtubedownloader.youtube.VideoMeta
import com.hexfan.youtubedownloader.youtube.YtFile
import dagger.android.AndroidInjection
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_download.*
import timber.log.Timber
import javax.inject.Inject


class DownloadActivity : AppCompatActivity() {

    @Inject
    lateinit var factory: DownloadViewModel.Factory
    lateinit var viewModel: DownloadViewModel

    lateinit var youtubeLink: String

    companion object {
        val KEY_YTLINK = "yt_link"
        fun start(context: Context, ytLink: String){
            val intent = Intent(context, DownloadActivity::class.java)
            intent.putExtra(KEY_YTLINK, ytLink)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_download)


        viewModel = ViewModelProviders.of(this, factory).get(DownloadViewModel::class.java)


        if(intent.extras != null) {
            val ytLink = intent.extras.getString(KEY_YTLINK)

            getYoutubeDownloadUrl(ytLink)

        }
    }

    private fun getYoutubeDownloadUrl(youtubeLink: String) {
        println("URL : $youtubeLink")
        viewModel.loadVideo(youtubeLink)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe{
                    progressBar.visibility = View.GONE
                    Timber.e("working $it")
                    if(it.ytFiles != null && it.videoMeta != null)
                        makeLayout(it.ytFiles, it.videoMeta)
                }
    }

    private fun makeLayout(ytFiles: SparseArray<YtFile>, videoMeta: VideoMeta){
        var i = 0
        var itag = 0
        while (i < ytFiles.size()) {
            itag = ytFiles.keyAt(i)
            // ytFile represents one file with its url and meta data
            val ytFile = ytFiles.get(itag)

            // Just add videos in a decent format => height -1 = audio
                addButtonToMainLayout(videoMeta.title, ytFile)
            if (ytFile.format.height == -1 || ytFile.format.height >= 360) {
            }
            i++
        }
    }

    private fun addButtonToMainLayout(videoTitle: String, ytfile: YtFile) {
        // Display some buttons and let the user choose the format
        var btnText = if (ytfile.format.height == -1)
            "Audio " + ytfile.format.audioBitrate + " kbit/s"
        else
            (ytfile.format.height).toString() + "p"

        btnText += if ((ytfile.format.isDashContainer)) " dash" else ""
        val btn = Button(this)
        btn.setText(btnText)
        btn.setOnClickListener {
            var filename: String
            if (videoTitle.length > 55) {
                filename = videoTitle.substring(0, 55) + "." + ytfile.format.ext
            } else {
                filename = videoTitle + "." + ytfile.format.ext
            }
            filename = filename.replace(("\\\\|>|<|\"|\\||\\*|\\?|%|:|#|/").toRegex(), "")
            downloadFromUrl(ytfile.url, videoTitle, filename)
            finish()
        }
        mainLayout.addView(btn)
    }

    private fun downloadFromUrl(youtubeDlUrl: String, downloadTitle: String, fileName: String) {
        val uri = Uri.parse(youtubeDlUrl)
        val request = DownloadManager.Request(uri)
        request.setTitle(downloadTitle)

        request.allowScanningByMediaScanner()
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)

        val manager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        manager.enqueue(request)
    }

}
