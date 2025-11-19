package com.example.android_development

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

// https://tutorials.eu/integrating-location-services-in-your-android-app-day-11-android-14-masterclass/ - полезная инфа

class location : AppCompatActivity() {
    companion object {
        private const val PERMISSION_REQUEST_ACCESS_LOCATION = 100
        private const val LOG_TAG = "LOCATION_ACTIVITY"
    }
    private lateinit var currentLatitude: TextView
    private lateinit var currentLongitude: TextView
    private lateinit var currentAltitude: TextView
    private lateinit var currentTime: TextView
    private lateinit var locationManager: LocationManager

    private fun requestPermissions() {
        Log.w(LOG_TAG, "requestPermissions()");
        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION),
            PERMISSION_REQUEST_ACCESS_LOCATION
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_location)

        locationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        currentAltitude = findViewById(R.id.currentAltitude) as TextView
        currentLatitude = findViewById(R.id.currentLatitude) as TextView
        currentLongitude = findViewById(R.id.currentLongitude) as TextView
        currentTime = findViewById(R.id.currentTime) as TextView

        if (!checkLocationPermission(this)){
            requestPermissions()
        }
//        else {
//            updateServiceGroup()
//        }
    }



    fun checkLocationPermission(context: Context): Boolean {
        val FineLocationPermission = ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val CoarseLocationPermission = ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        return FineLocationPermission && CoarseLocationPermission
    }
}