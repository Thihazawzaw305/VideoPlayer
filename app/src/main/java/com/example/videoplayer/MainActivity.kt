package com.example.videoplayer


import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.Player
import androidx.media3.common.util.Util
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private var player: ExoPlayer? = null
    private var playWhenReady = true
    private var currentItem = 0
    private var playbackPosition = 0L
    private val playbackStateListener : Player.Listener = playbackStatListener()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


    }

    override fun onStart() {
        super.onStart()
        if (Util.SDK_INT > 23) {
            initializePlayer()
        }
    }


    override fun onResume() {
        super.onResume()
        hideSystemUi()
        if (Util.SDK_INT <= 23 || player == null) {
            initializePlayer()
        }
    }

    override fun onPause() {
        super.onPause()
        if (Util.SDK_INT <= 23) {
            releasePlayer()
        }
    }


    override fun onStop() {
        super.onStop()
        if (Util.SDK_INT > 23) {
            releasePlayer()
        }
    }


    private fun initializePlayer() {
        player = ExoPlayer.Builder(this)
            .build()
            .also { exoPlayer ->
                playerView.player = exoPlayer

              val mediaItem = MediaItem.fromUri(getString(R.string.test_video))
//
//                val mediaItem = MediaItem.Builder()
//                    .setUri(getString(R.string.test_video3))
//                    .setMimeType(MimeTypes.APPLICATION_MPD)
//                    .build()
                exoPlayer.setMediaItem(mediaItem)
                exoPlayer.playWhenReady = playWhenReady
                exoPlayer.seekTo(currentItem,playbackPosition)
                exoPlayer.addListener(playbackStateListener)
                exoPlayer.prepare()
            }
    }

    private fun releasePlayer(){
        player?.let { exoPlayer ->
            playWhenReady = exoPlayer.playWhenReady
            currentItem = exoPlayer.currentMediaItemIndex
            playbackPosition = exoPlayer.currentPosition
            exoPlayer.removeListener(playbackStateListener)
            exoPlayer.release()
        }
        player = null
    }

    @SuppressLint("InlinedApi")
    private fun hideSystemUi() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, playerView).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }

    }



}

private const val TAG = "PlayerActivity"
private fun playbackStatListener() = object :Player.Listener{
    override fun onPlaybackStateChanged(playbackState: Int) {
        val statString : String = when(playbackState){
            ExoPlayer.STATE_IDLE -> "ExoPlayer.STATE_IDLE"
            ExoPlayer.STATE_BUFFERING -> "ExoPlayer.STATE_BUFFERING"
            ExoPlayer.STATE_READY -> "ExoPlayer.STATE_READY"
            ExoPlayer.STATE_ENDED -> "ExoPlayer.STATE_ENDED"

            else -> "UNKNOWN STATE"
        }
        Log.d(TAG,"changed state to $statString")
    }

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        val playingString : String = if (isPlaying) "PLAYING" else "NOT PLAYING"
        Log.d(TAG,"Player is currently $playingString")
    }
}