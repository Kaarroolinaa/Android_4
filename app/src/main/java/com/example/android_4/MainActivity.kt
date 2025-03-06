package com.example.android_4

import android.app.Activity
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import android.widget.VideoView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var videoView: VideoView
    private var selectedMediaUri: Uri? = null
    private var isAudioSelected = false

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
        val selectMedia: Button = findViewById(R.id.selectMedia)

        val playVideo: Button = findViewById(R.id.playVideo)
        val pauseVideo: Button = findViewById(R.id.pauseVideo)
        val stopVideo: Button = findViewById(R.id.stopVideo)

        val downloadMedia: Button = findViewById(R.id.downloadMedia)
        val urlInput: EditText = findViewById(R.id.urlInput)

        playAudio.setOnClickListener {
            if (isAudioSelected && selectedMediaUri != null) {
                mediaPlayer = MediaPlayer.create(this, selectedMediaUri)
            }
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

        selectMedia.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "*/*"
                putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("audio/*", "video/*"))
            }
            selectMediaLauncher.launch(intent)
        }

        downloadMedia.setOnClickListener {
            val url = urlInput.text.toString()

            if (url.isNotEmpty()) {
                val uri = Uri.parse(url)
                val request = DownloadManager.Request(uri)
                    .setTitle("Downloading Media")
                    .setDescription("Downloading media file...")
                    .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, uri.lastPathSegment)
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)

                val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                downloadManager.enqueue(request)

                Toast.makeText(this, "Download started...", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Please enter a valid URL", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private val selectMediaLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                selectedMediaUri = uri

                val contentResolver = contentResolver
                val type = contentResolver.getType(uri)

                if (type?.startsWith("audio") == true) {
                    isAudioSelected = true
                    mediaPlayer.reset()
                    mediaPlayer.setDataSource(this, uri)
                    mediaPlayer.prepare()
                } else if (type?.startsWith("video") == true) {
                    isAudioSelected = false
                    videoView.setVideoURI(uri)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
        videoView.stopPlayback()
    }
}
