package com.example.android_development

import java.io.File
import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MediaPlayer : AppCompatActivity() {
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var seekBar: SeekBar
    private lateinit var seekBarVolume: SeekBar
    private lateinit var textViewCurrent: TextView
    private lateinit var textViewTotal: TextView
    private lateinit var currentTrackText: TextView
    private lateinit var playButton: Button
    private lateinit var pauseButton: Button
    private lateinit var stopButton: Button
    private lateinit var prevButton: Button
    private lateinit var nextButton: Button
    private lateinit var requestPermissionLauncher: androidx.activity.result.ActivityResultLauncher<String>

    private val handler = Handler(Looper.getMainLooper())
    private val updateSeekBar = object : Runnable {
        override fun run() {
            if (::mediaPlayer.isInitialized && mediaPlayer.isPlaying) {
                seekBar.progress = mediaPlayer.currentPosition
                textViewCurrent.text = formatTime(mediaPlayer.currentPosition)
                handler.postDelayed(this, 1000)
            }
        }
    }

    private val trackUris = mutableListOf<String>()
    private val trackNames = mutableListOf<String>()
    private var currentTrackIndex = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        mediaPlayer = android.media.MediaPlayer()

        setContentView(R.layout.activity_media_player)
        initViews()

        requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_LONG).show()
                loadSongsFromExternalStorage()
                if (trackUris.isEmpty()) {
                    Toast.makeText(this, "Песни не найдены в папке Music", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(this, "Please grant permission", Toast.LENGTH_LONG).show()
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupSeekBar()
        setupVolumeSeekBar()
        setupControlButtons()

        requestStoragePermission()
    }

    private fun initViews() {
        seekBar = findViewById(R.id.seekBar)
        seekBarVolume = findViewById(R.id.volumeSeekBar)
        textViewCurrent = findViewById(R.id.currentTime)
        textViewTotal = findViewById(R.id.totalTime)
        currentTrackText = findViewById(R.id.currentTrackText)
        playButton = findViewById(R.id.playButton)
        pauseButton = findViewById(R.id.pauseButton)
        stopButton = findViewById(R.id.stopButton)
        prevButton = findViewById(R.id.prevButton)
        nextButton = findViewById(R.id.nextButton)
    }

    private fun setupControlButtons() {
        playButton.setOnClickListener {
            if (trackUris.isNotEmpty()) {
                if (currentTrackIndex == -1) {
                    playSelectedTrack(0)
                } else {
                    if (!mediaPlayer.isPlaying) {
                        mediaPlayer.start()
                        handler.post(updateSeekBar)
                    }
                }
            } else {
                Toast.makeText(this, "Нет загруженных песен", Toast.LENGTH_LONG).show()
            }
        }

        pauseButton.setOnClickListener {
            if (::mediaPlayer.isInitialized && mediaPlayer.isPlaying) {
                mediaPlayer.pause()
            }
        }

        stopButton.setOnClickListener {
            if (::mediaPlayer.isInitialized) {
                if (mediaPlayer.isPlaying) {
                    mediaPlayer.stop()
                }
                mediaPlayer.reset()
                setupSeekBar()
                seekBar.progress = 0
                textViewCurrent.text = "0:00"
                currentTrackIndex = -1
                currentTrackText.text = "Текущий трек: не выбран"
            }
        }

        prevButton.setOnClickListener {
            if (trackUris.isNotEmpty()) {
                val newIndex = if (currentTrackIndex <= 0) trackUris.size - 1 else currentTrackIndex - 1
                playSelectedTrack(newIndex)
            }
        }

        nextButton.setOnClickListener {
            if (trackUris.isNotEmpty()) {
                val newIndex = if (currentTrackIndex >= trackUris.size - 1) 0 else currentTrackIndex + 1
                playSelectedTrack(newIndex)
            }
        }
    }

    private fun requestStoragePermission() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_AUDIO
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        when {
            ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED -> {
                loadSongsFromExternalStorage()
            }
            else -> {
                requestPermissionLauncher.launch(permission)
            }
        }
    }

    private fun loadSongsFromExternalStorage() {
        trackUris.clear()
        trackNames.clear()
        try {
            val musicPath = Environment.getExternalStorageDirectory().path + "/Music"
            val directory = File(musicPath)

            if (directory.exists() && directory.isDirectory) {
                directory.listFiles { file ->
                    file.isFile && file.name.endsWith(".mp3")
                }?.forEach { file ->
                    trackUris.add(file.absolutePath)
                    trackNames.add(file.nameWithoutExtension)
                }

                Toast.makeText(this, "Найдено ${trackUris.size} песен", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Папка Music не найдена", Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Ошибка: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun playSelectedTrack(position: Int) {
        if (position < trackUris.size) {
            if (::mediaPlayer.isInitialized) {
                mediaPlayer.reset()
            }

            try {
                mediaPlayer.setDataSource(trackUris[position])
                mediaPlayer.prepare()
                mediaPlayer.start()
                setupSeekBar()
                handler.post(updateSeekBar)

                currentTrackIndex = position
                currentTrackText.text = "Текущий трек: ${trackNames[position]}"

                Toast.makeText(this, "Играет: ${trackNames[position]}", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(this, "Ошибка воспроизведения: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun formatTime(time: Int): String {
        val minutes = time / 1000 / 60
        val seconds = (time / 1000) % 60
        return String.format("%d:%02d", minutes, seconds)
    }

    private fun setupSeekBar() {
        if (::mediaPlayer.isInitialized) {
            seekBar.max = mediaPlayer.duration
            textViewTotal.text = formatTime(mediaPlayer.duration)
            seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    if (fromUser && ::mediaPlayer.isInitialized) {
                        mediaPlayer.seekTo(progress)
                        textViewCurrent.text = formatTime(progress)
                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            })
        }
    }

    private fun setupVolumeSeekBar() {
        seekBarVolume.progress = 50
        seekBarVolume.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser && ::mediaPlayer.isInitialized) {
                    val volume = progress / 100.0f
                    mediaPlayer.setVolume(volume, volume)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::mediaPlayer.isInitialized) {
            mediaPlayer.release()
        }
        handler.removeCallbacks(updateSeekBar)
    }
}