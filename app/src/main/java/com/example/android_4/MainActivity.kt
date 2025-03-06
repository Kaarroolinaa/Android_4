package com.example.android_4
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var videoView: VideoView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        videoView = findViewById(R.id.videoView)

        val videoUri = Uri.parse("android.resource://${packageName}/${R.raw.sample_video}")
        videoView.setVideoURI(videoUri)

        mediaPlayer = MediaPlayer.create(this, R.raw.sample_audio)

        val playAudio: Button = findViewById(R.id.playAudio)
        val pauseAudio: Button = findViewById(R.id.pauseAudio)
        val stopAudio: Button = findViewById(R.id.stopAudio)

        playAudio.setOnClickListener {
            if (!mediaPlayer.isPlaying) {
                mediaPlayer.start()
            }
        }

        pauseAudio.setOnClickListener {
            mediaPlayer.pause()
        }

        stopAudio.setOnClickListener {
            mediaPlayer.stop()
            mediaPlayer.prepareAsync()
        }

        val playVideo: Button = findViewById(R.id.playVideo)
        val pauseVideo: Button = findViewById(R.id.pauseVideo)
        val stopVideo: Button = findViewById(R.id.stopVideo)

        playVideo.setOnClickListener {
            if (!videoView.isPlaying) {
                videoView.start()
            }
        }

        pauseVideo.setOnClickListener {
            videoView.pause()
        }

        stopVideo.setOnClickListener {
            videoView.stopPlayback()
            videoView.setVideoURI(Uri.parse("android.resource://${packageName}/${R.raw.sample_video}"))
            videoView.seekTo(0)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
        videoView.stopPlayback()
    }
}