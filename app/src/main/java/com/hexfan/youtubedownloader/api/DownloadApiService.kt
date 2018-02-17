package com.hexfan.youtubedownloader.api

import io.reactivex.Flowable
import io.reactivex.Single

/**
 * Created by Pawel on 14.02.2018.
 */
class DownloadApiService(private val youtubeApiService: YoutubeApiService): YoutubeService {


    override fun getVideoInfo(url: String): Flowable<String> {
        return youtubeApiService.getVideoInfo(url)
    }

    override fun getUrl(hlsvp: String): Flowable<String> {
        return youtubeApiService.getUrl(hlsvp)
    }

    override fun getPlaylist(playlistId: String): Single<PlaylistResponse> {
        return youtubeApiService.getPlaylist(playlistId)
    }
}