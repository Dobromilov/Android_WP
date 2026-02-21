package com.example.android_development

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.zeromq.SocketType
import org.zeromq.ZContext
import org.zeromq.ZMQ
import java.util.concurrent.atomic.AtomicReference

@Serializable
data class Json_obj(
    val _Latitude: Double,
    val _Longitude: Double,
    val _Altitude: Double,
    val _Time: Long,
)

class LocationServer : AppCompatActivity() {

    companion object {
        private const val PERMISSION_REQUEST_ACCESS_LOCATION = 100
    }

    private lateinit var tvStatus: TextView
    private lateinit var btnStart: Button
    private lateinit var locationManager: LocationManager
    private val mainHandler = Handler(Looper.getMainLooper())

    private var isRunning = false
    private val lastLocation = AtomicReference<Location?>(null)

    private val locationListener = android.location.LocationListener { location ->
        updateLocationInfo(location)
    }

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
        setContentView(R.layout.activity_location_server)

        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        tvStatus = findViewById(R.id.tv_server)

        btnStart = findViewById(R.id.send_client_data)

        btnStart.setOnClickListener {
            if (!isRunning) {
                if (checkLocationPermission()) {
                    if (isLocationEnable()) {
                        isRunning = true
                        startLocationUpdates()
                        startSendingLoop()
                        btnStart.text = "Остановить"
                    } else {
                        Toast.makeText(applicationContext, "Enable location in settings", Toast.LENGTH_SHORT).show()
                        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                        startActivity(intent)
                    }
                } else {
                    requestPermissions()
                }
            } else {
                isRunning = false
                stopLocationUpdates()
                btnStart.text = "Старт"
            }
        }
    }

    private fun startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            val lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if (lastKnownLocation != null) {
                updateLocationInfo(lastKnownLocation)
            }

            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                1000L,
                0f,
                locationListener
            )
        }
    }

    private fun stopLocationUpdates() {
        locationManager.removeUpdates(locationListener)
    }

    private fun updateLocationInfo(location: Location) {
        lastLocation.set(location)
        mainHandler.post {
            tvStatus.text = "Широта: ${location.latitude}\nДолгота: ${location.longitude}\nВысота: ${location.altitude}\nВремя: ${location.time}"
        }
    }

    private fun startSendingLoop() {
        Thread {
            try {
                val context = ZContext()
                val socket = context.createSocket(SocketType.REQ)
                socket.connect("tcp://127.0.0.1:7777")

                Log.d("ZMQ", "Подключено к серверу")

                while (isRunning) {
                    val location = lastLocation.get()
                    if (location != null) {
                        val jsonObject = Json_obj(location.latitude, location.longitude, location.altitude, location.time)
                        val jsonString = Json.encodeToString(jsonObject)
                        Log.d("ZMQ", "Отправка: $jsonString")
                        socket.send(jsonString.toByteArray(ZMQ.CHARSET), 0)
                        val reply = socket.recv(0)
                        Log.d("ZMQ", "Получен ответ: ${String(reply, ZMQ.CHARSET)}")
                    } else {
                        Log.d("ZMQ", "Местоположение еще не получено")
                    }
                    Thread.sleep(1000)
                }

                socket.close()
                context.close()
                Log.d("ZMQ", "Соединение закрыто")

            } catch (e: Exception) {
                e.printStackTrace()
                mainHandler.post {
                    tvStatus.text = "Ошибка: ${e.message}"
                }
            }
        }.start()
    }

    private fun checkLocationPermission(): Boolean {
        val fineLocationPermission = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarseLocationPermission = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        return fineLocationPermission && coarseLocationPermission
    }

    private fun isLocationEnable(): Boolean {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_ACCESS_LOCATION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (isRunning) {
                        if (isLocationEnable()) {
                            startLocationUpdates()
                            startSendingLoop()
                        } else {
                            Toast.makeText(applicationContext, "Enable location in settings", Toast.LENGTH_SHORT).show()
                            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                            startActivity(intent)
                        }
                    }
                } else {
                    Toast.makeText(applicationContext, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        isRunning = false
        stopLocationUpdates()
    }
}