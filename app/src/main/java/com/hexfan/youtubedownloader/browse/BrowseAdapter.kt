package com.hexfan.youtubedownloader.browse

import android.app.Activity
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hexfan.youtubedownloader.R
import com.hexfan.youtubedownloader.api.Item

/**
 * Created by Pawel on 18.02.2018.
 */
class BrowseAdapter(val activity: Activity, list: List<Item> = arrayListOf()):
        RecyclerView.Adapter<BrowseAdapter.BrowseViewHolder>() {

    var list: List<Item> = list
    set(value) {
        field = value
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): BrowseViewHolder {
        return BrowseViewHolder(LayoutInflater.from(activity).inflate(R.layout.list_item, parent, false))
    }

    override fun onBindViewHolder(holder: BrowseViewHolder, position: Int) {
        if(activity is AdapterContract)
            activity.bind(holder.itemView, list[position])
    }

    override fun getItemCount() = list.size

    fun findId(videoId: String, operation: Item.() -> Unit): Item?{
        for (item in list) {
            if (item.contentDetails.videoId == videoId) {
                item.operation()
                notifyDataSetChanged()
            }
        }
        return null
    }

    class BrowseViewHolder(view: View) : RecyclerView.ViewHolder(view)

    interface AdapterContract{
        fun bind(itemView: View, item: Item)
    }
}