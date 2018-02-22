package com.hexfan.youtubedownloader.settings

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.hexfan.youtubedownloader.R
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_settings.*
import timber.log.Timber
import javax.inject.Inject

class SettingsActivity : AppCompatActivity() {

    @Inject
    lateinit var factory: SettingsViewModel.Factory
    lateinit var viewModel: SettingsViewModel

    companion object {
        val KEY_YTLINK = "yt_link"
        fun start(context: Context, ytLink: String){
            val intent = Intent(context, SettingsActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            intent.putExtra(KEY_YTLINK, ytLink)
            context.startActivity(intent)
        }

        fun start(context: Context){
            val intent = Intent(context, SettingsActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        viewModel = ViewModelProviders.of(this, factory).get(SettingsViewModel::class.java)

        title = "Ustawienia"

        if(intent.extras != null) {
            val ytLink = intent.extras.getString(SettingsActivity.KEY_YTLINK)
            saveIdFromLink(ytLink)
        }else
            viewModel.loadId()

        sQuality.setItems<String>(resources.getStringArray(R.array.quality).toCollection(ArrayList()))
    }

    override fun onResume() {
        super.onResume()
        viewModel.idValue.observe(this, Observer{
            displayId(it ?: "")
        })
    }

    private fun saveIdFromLink(ytLink: String){
        val id = ytLink.split("playlist?list=")[1]
        displayId(id)
        viewModel.saveId(id)
    }

    private fun displayId(id: String){
        etPlaylistId.setText(id)
        etPlaylistId.setSelection(id.length)
    }

}
