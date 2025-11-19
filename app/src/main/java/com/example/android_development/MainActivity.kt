package com.example.android_development

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val bt_calc = findViewById<Button>(R.id.button1)
        val bt_media_player = findViewById<Button>(R.id.button2)
        val bt_location = findViewById<Button>(R.id.button3)


        bt_calc.setOnClickListener ({
            val calcIntent = Intent(this,calc::class.java)
            startActivity(calcIntent)
        })

        bt_media_player.setOnClickListener ({
            val MediaPlayerIntent = Intent(this,MediaPlayer::class.java)
            startActivity(MediaPlayerIntent)
        })

        bt_location.setOnClickListener ({
            val locationIntent = Intent(this, location::class.java)
            startActivity(locationIntent)
        })
    }
}