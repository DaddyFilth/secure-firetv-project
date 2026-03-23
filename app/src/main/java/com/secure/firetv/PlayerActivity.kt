package com.secure.firetv

import android.os.Bundle
import android.view.WindowManager
import androidx.fragment.app.FragmentActivity
import androidx.media3.common.MediaItem
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.ResolvingDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.ui.PlayerView

class PlayerActivity : FragmentActivity() {

    private var player: ExoPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Link the XML layout to this activity
        setContentView(R.layout.activity_player)
        
        // SECURITY: Prevent stream ripping and screenshots
        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)

        val streamUrl = intent.getStringExtra("STREAM_URL") ?: return
        
        @Suppress("UNCHECKED_CAST")
        val spoofHeaders = intent.getSerializableExtra("HEADERS") as? HashMap<String, String> ?: hashMapOf()

        initializePlayer(streamUrl, spoofHeaders)
    }

    private fun initializePlayer(url: String, headers: Map<String, String>) {
        // 1. Create a data source that dynamically injects the scraped Cloudflare headers
        val baseDataSource = DefaultHttpDataSource.Factory()
        val resolvingDataSource = ResolvingDataSource.Factory(baseDataSource) { dataSpec ->
            dataSpec.buildUpon().setHttpRequestHeaders(headers).build()
        }

        // 2. Build ExoPlayer using only the necessary modules
        val mediaSourceFactory = DefaultMediaSourceFactory(resolvingDataSource)
        player = ExoPlayer.Builder(this)
            .setMediaSourceFactory(mediaSourceFactory)
            .build()

        val mediaItem = MediaItem.fromUri(url)
        player?.setMediaItem(mediaItem)
        player?.prepare()
        player?.playWhenReady = true
        
        // 3. Attach the ExoPlayer instance to the PlayerView in the XML layout
        findViewById<PlayerView>(R.id.player_view).player = player
    }

    override fun onDestroy() {
        super.onDestroy()
        player?.release()
    }
}
