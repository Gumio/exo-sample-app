package com.gumioji.exosampleapp

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import kotlinx.android.synthetic.main.activity_main.*
import android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
import android.view.View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE
import android.view.View.SYSTEM_UI_FLAG_FULLSCREEN
import android.view.View.SYSTEM_UI_FLAG_LOW_PROFILE
import android.annotation.SuppressLint
import com.google.android.exoplayer2.source.ConcatenatingMediaSource


class MainActivity : AppCompatActivity() {

    private var playWhenReady = true
    private var currentWindow = 0
    private var playbackPosition = 0L

    private lateinit var playerView: PlayerView
    private var mediaPlayer: SimpleExoPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        playerView = video_view
    }

    private fun initPlayer() {
        val mediaSource = buildMediaSource()

        mediaPlayer = SimpleExoPlayer.Builder(this).build().apply {
            playWhenReady = playWhenReady
            seekTo(currentWindow, playbackPosition)
            prepare(mediaSource, false, false)
        }

        playerView.player = mediaPlayer
    }

    private fun buildMediaSource(): MediaSource {
        val dataSourceFactory = DefaultDataSourceFactory(this, "exoplayer-sample-app")
        val uri = listOf<Uri>(
            Uri.parse(getString(R.string.media_url_mp4)),
            Uri.parse(getString(R.string.google_sample_url)),
            Uri.parse(getString(R.string.media_url_mp4)),
            Uri.parse(getString(R.string.google_sample_url)),
            Uri.parse(getString(R.string.media_url_mp4)),
            Uri.parse(getString(R.string.google_sample_url))
        )

        val mediaSourceList = mutableListOf<MediaSource>().apply {
            uri.forEach {
                val mediaSource =
                    ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(it)
                add(mediaSource)
            }
        }

        return ConcatenatingMediaSource(*mediaSourceList.toTypedArray())
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
