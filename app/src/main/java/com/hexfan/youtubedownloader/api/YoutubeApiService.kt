package com.hexfan.youtubedownloader.api

import io.reactivex.Flowable
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

/**
 * Created by Pawel on 14.02.2018.
 */
interface YoutubeApiService {

    @GET
    fun getVideoInfo(@Url url: String): Flowable<String>

    @GET
    fun getUrl(@Url hlsvp: String): Flowable<String>

//    playlistItems?part=snippet%2CcontentDetails&maxResults=25&playlistId=PLOrLTbSvMDlHNfKo35w5ikmga9PUEoi3F&key=AIzaSyCBOuSMs3XbnNKTErnURD1saioZSMgMZhQ
    @GET("playlistItems?part=snippet%2CcontentDetails&maxResults=25")
    fun getPlaylist(@Query("playlistId") playlistId: String): Single<PlaylistResponse>

}