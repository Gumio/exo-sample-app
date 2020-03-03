package com.gumioji.exosampleapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.PlayerView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var playerView: PlayerView
    private lateinit var mediaPlayer: SimpleExoPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        playerView = video_view
        initPlayer()
    }

    private fun initPlayer() {
        mediaPlayer = SimpleExoPlayer.Builder(this).build()
        playerView.player = mediaPlayer
    }
}
