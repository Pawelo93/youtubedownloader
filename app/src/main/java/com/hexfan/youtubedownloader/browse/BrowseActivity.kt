package com.hexfan.youtubedownloader.browse

import android.app.DownloadManager
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.SparseArray
import android.view.View
import com.hexfan.youtubedownloader.R
import com.hexfan.youtubedownloader.api.Item
import com.hexfan.youtubedownloader.background
import com.hexfan.youtubedownloader.settings.SettingsActivity
import com.hexfan.youtubedownloader.youtube.VideoMeta
import com.hexfan.youtubedownloader.youtube.YtFile
import com.snatik.storage.Storage
import com.squareup.picasso.Picasso
import dagger.android.AndroidInjection
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_browse.*
import kotlinx.android.synthetic.main.list_item.view.*
import timber.log.Timber
import java.io.File
import javax.inject.Inject


class BrowseActivity : AppCompatActivity(), BrowseAdapter.AdapterContract {

    @Inject
    lateinit var factory: BrowseViewModel.Factory
    lateinit var viewModel: BrowseViewModel

    @Inject
    lateinit var picasso: Picasso
    @Inject
    lateinit var storage: Storage

    lateinit var browseAdapter: BrowseAdapter

    val videoFile = File(Environment.getExternalStorageDirectory(), "YoutubeDownloader/video")
    lateinit var manager: DownloadManager

    companion object {
        fun start(context: Context) {
            val starter = Intent(context, BrowseActivity::class.java)
            context.startActivity(starter)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_browse)

        viewModel = ViewModelProviders.of(this, factory).get(BrowseViewModel::class.java)

        manager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

        viewModel.subscribePlaylistId().observe(this, Observer {
            if (it == null || it.equals(""))
                SettingsActivity.start(this)
            else
                viewModel.loadPlaylist(it, videoFile)
        })


        browseAdapter = BrowseAdapter(this)
        recyclerView.layoutManager = LinearLayoutManager(this) as RecyclerView.LayoutManager?
        recyclerView.adapter = browseAdapter

        viewModel.playlist.observe(this, Observer {
            if (it != null) {
                browseAdapter.list = it

//                viewModel.downloadPlaylist(it, manager)
//                downloadPlaylist(it)
            }
        })


//        viewModel.itemStatus.observe(this, Observer {
//            if (it != null) {
//                browseAdapter.findId(it.videoId) {
//                    Timber.e("progress ${it.progress}")
//                    progress = it.progress
//                }
//            }
//        })
    }

    fun downloadPlaylist(playlist: List<Item>) {

        Observable.fromIterable(playlist)
                .filter { it.progress == Item.NOT_DOWNLOADED }
                .firstElement()
                .downloadVideo()
                .subscribe {
                    Timber.e("name ${it.title} prog ${it.progress}")
                    browseAdapter.findId(it.videoId) {
                        Timber.e("progress ${it.progress}")
                        progress = it.progress
                    }
                }
    }

    data class Req(val id: Long, val videoMeta: VideoMeta)
    data class SItem(val videoId: String, val title: String, val progress: Int)

    fun Maybe<Item>.downloadVideo(): Observable<SItem> {

        var downloading = true

        val item = this.blockingGet() ?: return Observable.empty()

        return viewModel.downloadInteractor.downloadFromId(item.contentDetails.videoId)
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
                    Req(manager.enqueue(request), it.videoMeta)
                }
                .flatMap { req ->
                    Observable.create<SItem> {
                        while (downloading) {
                            val q = DownloadManager.Query()
                            q.setFilterById(req.id)
                            val cursor = manager.query(q)

                            if (!cursor.moveToFirst() || cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) ==
                                    DownloadManager.STATUS_SUCCESSFUL) {
                                downloading = false
                                cursor.close()
                                it.onComplete()
                                break
                            }

                            val bytesTotal = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
                            val bytesDownloaded = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))

                            it.onNext(SItem(req.videoMeta.videoId,
                                    req.videoMeta.title,
                                    (bytesDownloaded * 100) / bytesTotal))
                            try {
                                Thread.sleep(200)
                            } catch (e: InterruptedException) {
                                Timber.e("Thread interrupted")
                            }
                        }
                    }.distinct()
                }


    }

    override fun bind(itemView: View, item: Item) {
        itemView.title.text = item.snippet.title
        itemView.date.text = item.contentDetails.videoPublishedAt

        itemView.thumbnail.setOnClickListener {
            val uri = Uri.parse("$videoFile/${item.snippet.title}")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            intent.setDataAndType(uri, "video/mp4")
            startActivity(intent)
        }

        changeProgress(itemView, item)


        if (item.progress == Item.NOT_DOWNLOADED) {
            viewModel.download(item.contentDetails.videoId, manager)
                    .flatMap {
                        Timber.e("im in flat map prog ${item.progress}")
                        viewModel.subscribeProgress(it, manager)
                    }
                    .subscribe ({
                        Timber.e("progress $it")
                        item.progress = it
                        changeProgress(itemView, item)
                    }, {
                        Timber.e("error $it")
                    }, {
                        Timber.e("COMPLETED")
                        item.progress = Item.DOWNLOADED
                        changeProgress(itemView, item)
                    })
        }
        // check if video is already downloaded
        // true set icon
        // false start downloading, show progressbar

        picasso.load(item.snippet.thumbnails["standard"]?.url)
                .fit()
                .centerCrop()
                .into(itemView.thumbnail)
    }

    private fun changeProgress(itemView: View, item: Item){
        when (item.progress) {
            -1, 100 -> {
                itemView.status.visibility = View.VISIBLE
                val drawable = if (item.progress == 100) R.drawable.ok else R.drawable.to_download
                itemView.status.setImageDrawable(resources.getDrawable(drawable))
                itemView.progress.visibility = View.GONE
            }
            else -> {
                itemView.progress.visibility = View.VISIBLE
                itemView.progress.text = "${item.progress}%"
                itemView.status.visibility = View.GONE
            }
        }
    }

    private fun getYoutubeDownloadUrl(youtubeLink: String) {
        println("URL : $youtubeLink")
        viewModel.loadVideo(youtubeLink)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    Timber.e("working $it")
                    if (it.ytFiles != null && it.videoMeta != null)
                        download(it.ytFiles, it.videoMeta)
                }
    }

    fun download(ytFiles: SparseArray<YtFile>, videoMeta: VideoMeta) {
        val ytFile = ytFiles.get(17)

        val videoTitle = videoMeta.title
        var filename: String
        if (videoTitle.length > 55) {
            filename = videoTitle.substring(0, 55) + "." + ytFile.format.ext
        } else {
            filename = videoTitle + "." + ytFile.format.ext
        }
        filename = filename.replace(("\\\\|>|<|\"|\\||\\*|\\?|%|:|#|/").toRegex(), "")
        downloadFromUrl(ytFile.url, videoTitle, filename)
    }

    private fun downloadFromUrl(youtubeDlUrl: String, downloadTitle: String, fileName: String) {

        val uri = Uri.parse(youtubeDlUrl)
        val request = DownloadManager.Request(uri)
        request.setTitle(downloadTitle)

        request.allowScanningByMediaScanner()
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setDestinationInExternalPublicDir("YoutubeDownloader/video", fileName)

        val manager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val id = manager.enqueue(request)

//        viewModel.downloadIndicator(videoId, manager)
//                .subscribe({
//                    Timber.e("progress $it")
//                }, {
//                    Timber.e(it)
//                })
    }


}
