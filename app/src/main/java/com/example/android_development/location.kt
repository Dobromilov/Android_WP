package com.example.android_development

import android.Manifest
import android.provider.Settings
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// https://tutorials.eu/integrating-location-services-in-your-android-app-day-11-android-14-masterclass/ - полезная инфа

class location : AppCompatActivity(), LocationListener {

    companion object {
        private const val PERMISSION_REQUEST_ACCESS_LOCATION = 100
    }

    private lateinit var currentLatitude: TextView
    private lateinit var currentLongitude: TextView
    private lateinit var currentAltitude: TextView
    private lateinit var currentTime: TextView
    private lateinit var locationManager: LocationManager

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            PERMISSION_REQUEST_ACCESS_LOCATION
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_location)

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        currentAltitude = findViewById(R.id.currentAltitude)
        currentLatitude = findViewById(R.id.currentLatitude)
        currentLongitude = findViewById(R.id.currentLongitude)
        currentTime = findViewById(R.id.currentTime)

        if (!checkLocationPermission()) {
            requestPermissions()
        } else {
            updateServiceGroup()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_ACCESS_LOCATION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                updateServiceGroup()
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun updateServiceGroup() {
        if (checkLocationPermission()) {
            if (isLocationEnable()) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return
                }

                val lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                lastLocation?.let {
                    updateLocationInfo(it)
                }

                locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    1000L, 0f,
                    this
                )
            } else {
                Toast.makeText(applicationContext, "Enable location in settings", Toast.LENGTH_SHORT).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        }
    }

    private fun updateLocationInfo(location: Location) {
        currentLatitude.text = "Latitude: ${location.latitude}"
        currentLongitude.text = "Longitude: ${location.longitude}"
        currentAltitude.text = "Altitude: ${location.altitude}"
        currentTime.text = "Time: ${formatTime(location.time)}"
    }

    override fun onLocationChanged(location: Location) {
        updateLocationInfo(location)
    }


    fun checkLocationPermission(): Boolean {
        val FineLocationPermission = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val CoarseLocationPermission = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        return FineLocationPermission && CoarseLocationPermission
    }

    private fun formatTime(timeInMillis: Long): String {
        val date = Date(timeInMillis)
        val format = SimpleDateFormat("HH:mm:ss         dd.MM.yyyy", Locale.getDefault())
        return format.format(date)
    }

    private fun isLocationEnable(): Boolean {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }
}