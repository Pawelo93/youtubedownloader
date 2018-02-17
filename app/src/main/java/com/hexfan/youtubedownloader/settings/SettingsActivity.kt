package com.hexfan.youtubedownloader.settings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.hexfan.youtubedownloader.DownloadActivity
import com.hexfan.youtubedownloader.R
import timber.log.Timber

class SettingsActivity : AppCompatActivity() {

    companion object {
        val KEY_YTLINK = "yt_link"
        fun start(context: Context, ytLink: String){
            val intent = Intent(context, DownloadActivity::class.java)
            val bundle = Bundle()
            bundle.putString(KEY_YTLINK, ytLink)
            context.startActivity(intent, bundle)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val ytLink = intent.extras.getString(SettingsActivity.KEY_YTLINK)

        if(ytLink != null){
            Timber.e("heres it is $ytLink")
        }
        else {
            Timber.e("Wrong something!!!")
        }
    }

}
