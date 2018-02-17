package com.hexfan.youtubedownloader.api

import io.reactivex.Flowable
import io.reactivex.Single

/**
 * Created by Pawel on 14.02.2018.
 */
interface YoutubeService {

    fun getVideoInfo(url: String): Flowable<String>

    fun getUrl(hlsvp: String): Flowable<String>

    fun getPlaylist(playlistId: String): Single<PlaylistResponse>
}