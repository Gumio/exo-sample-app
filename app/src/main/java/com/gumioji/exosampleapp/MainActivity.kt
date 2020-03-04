package com.gumioji.exosampleapp

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import kotlinx.android.synthetic.main.activity_main.*
import android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
import android.view.View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE
import android.view.View.SYSTEM_UI_FLAG_FULLSCREEN
import android.view.View.SYSTEM_UI_FLAG_LOW_PROFILE
import android.annotation.SuppressLint
import android.util.Log
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.dash.DashMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector

class MainActivity : AppCompatActivity() {

    private val TAG = MainActivity::class.java.name

    private var playWhenReady = true
    private var currentWindow = 0
    private var playbackPosition = 0L

    private lateinit var playerView: PlayerView
    private var mediaPlayer: SimpleExoPlayer? = null

    private val playerEventListener = object : Player.EventListener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            val state = if (isPlaying) "STATE_PLAYING" else "STATE_NOT_PLAYING"
            Log.d(TAG, "isPlayingChanged: $state")
        }

        override fun onPlayerError(error: ExoPlaybackException) {
            Log.d(TAG, "Error: ${error.message ?: ""}")
        }

        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            val stateStr = when(playbackState) {
                SimpleExoPlayer.STATE_IDLE -> "STATE_IDLE"
                SimpleExoPlayer.STATE_BUFFERING -> "STATE_BUFFERING"
                SimpleExoPlayer.STATE_READY -> "STATE_READY"
                SimpleExoPlayer.STATE_ENDED ->  "STATE_ENDED"
                else -> "UNKNOWN"
            }

            Log.d(TAG, "state is $stateStr, playWhenReady is $playWhenReady")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        playerView = video_view
    }

    private fun initPlayer() {
        val mediaSource = buildMediaSource()
        val trackSelector = DefaultTrackSelector(this).apply {
            setParameters(buildUponParameters().setMaxVideoSizeSd())
        }

        mediaPlayer = SimpleExoPlayer.Builder(this)
            .setTrackSelector(trackSelector)
            .build()
            .apply {
                playWhenReady = playWhenReady
                seekTo(currentWindow, playbackPosition)
                addListener(playerEventListener)
                prepare(mediaSource, false, false)
            }

        playerView.player = mediaPlayer
    }

    private fun buildMediaSource(): MediaSource {
        val dataSourceFactory = DefaultDataSourceFactory(this, "exoplayer-sample-app")
        val uri = Uri.parse(getString(R.string.media_url_dash))

        return DashMediaSource.Factory(dataSourceFactory).createMediaSource(uri)
    }

    override fun onResume() {
        super.onResume()
        hideSystemUi()
        if (mediaPlayer == null) {
            initPlayer()
        }
    }

    override fun onStop() {
        super.onStop()
        releasePlayer()
    }

    override fun onPause() {
        super.onPause()
        releasePlayer()
    }

    private fun releasePlayer() {
        mediaPlayer?.let {
            playWhenReady = it.playWhenReady
            playbackPosition = it.currentPosition
            currentWindow = it.currentWindowIndex
            it.removeListener(playerEventListener)
            it.release()
            mediaPlayer = null
        }
    }

    @SuppressLint("InlinedApi")
    private fun hideSystemUi() {
        playerView.systemUiVisibility = (
                SYSTEM_UI_FLAG_LOW_PROFILE
                        or SYSTEM_UI_FLAG_FULLSCREEN
                        or SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        or SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or SYSTEM_UI_FLAG_HIDE_NAVIGATION
                )
    }
}
