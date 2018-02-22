package com.hexfan.youtubedownloader.api

/**
 * Created by Pawel on 15.02.2018.
 */
data class PlaylistResponse(val etag: String,
                            val kind: String,
                            val pageInfo: PageInfo,
                            val items: List<Item>)

data class PageInfo(val totalResults: Int,
                    val resultsPerPage: Int)

data class Item(val kind: String,
                val etag: String,
                val id: String,
                val snippet: Snippet,
                val contentDetails: ContentDetails,
                var progress: Int){
    companion object {
        val NOT_DOWNLOADED = -1
        val IN_PROGRESS = 0
        val DOWNLOADED = 100
    }
}

data class ContentDetails(val videoId: String,
                          var videoPublishedAt: String)

data class Snippet(val publishedAt: String,
                   val channelId: String,
                   val title: String,
                   val description: String,
                   val thumbnails: HashMap<String, Thumbnail>,
                   val channelTitle: String,
                   val playlistId: String,
                   val position: String,
                   val resourceId: ResourceId)

data class Thumbnail(val url: String,
                     val width: Int,
                     val height: Int)

data class ResourceId(val kind: String,
                      val videoId: String)