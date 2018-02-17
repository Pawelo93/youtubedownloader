package com.hexfan.youtubedownloader

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.hexfan.youtubedownloader.settings.SettingsActivity
import timber.log.Timber

class EntryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entry)

        // Check how it was started and if we can get the youtube link
        if (savedInstanceState == null && Intent.ACTION_SEND == intent.action &&
                intent.type != null && "text/plain" == intent.type) {

            val link = intent.getStringExtra(Intent.EXTRA_TEXT)

            Timber.e("ytLink $link")
            if (link != null && (link.contains("://youtu.be/") || link.contains("youtube.com/watch?v="))) {
                DownloadActivity.start(this, link)
            } else if (link != null && (link.contains("://youtube.com/") || link.contains("playlist?list"))){
                SettingsActivity.start(this, link)
            } else {
                Toast.makeText(this, "Error", Toast.LENGTH_LONG).show()
                finish()
            }
        } else {
            finish()
        }
    }
}
