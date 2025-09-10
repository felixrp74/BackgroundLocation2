package com.ar.backgroundlocation

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch


/**
 * @Author: Abdul Rehman
 * @Date: 06/05/2024.
 */
class LocationService : Service(), LocationUpdatesCallBack {
    private val TAG = LocationService::class.java.simpleName

    private lateinit var gpsLocationClient: GPSLocationClient
    private var notification: NotificationCompat.Builder? = null
    private var notificationManager: NotificationManager? = null
    private lateinit var currentDriverId: String // Debes inicializar esto
    private lateinit var fireLocationSyncService: LocationSyncService // Inyéctalo o créalo
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob()) // Para coroutines

    override fun onCreate() {
        super.onCreate()
        gpsLocationClient = GPSLocationClient()
        gpsLocationClient.setLocationUpdatesCallBack(this)

        // DETERMINAR currentDriverId AQUÍ:
        // Ejemplo: leerlo desde SharedPreferences, o si hay un login, obtenerlo del usuario logueado.
        currentDriverId = "driver1" // Implementa esta función

        val firestore = FirebaseFirestore.getInstance() // Asegúrate de tener Firebase inicializado
        fireLocationSyncService = FirestoreLocationSyncService(firestore)

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_SERVICE_START -> startService()
            ACTION_SERVICE_STOP -> stopService()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    companion object {
        const val ACTION_SERVICE_START = "ACTION_START"
        const val ACTION_SERVICE_STOP = "ACTION_STOP"
    }


    private fun startService() {
        gpsLocationClient.getLocationUpdates(applicationContext)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "location",
                "Location",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
        notification = NotificationCompat.Builder(this, "location")
            .setContentTitle("Tracking location...")
            .setContentText("Searching...")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setOngoing(true)

        notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        startForeground(1, notification?.build())
    }

    private fun stopService() {
        gpsLocationClient.setLocationUpdatesCallBack(null)
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun locationException(message: String) {
        Log.d(TAG, message)
    }

    override fun onLocationUpdate(location: Location) {

        val locationData = LocationData(
            driverId = currentDriverId, // Usar el ID del chófer actual
            latitude = location.latitude,
            longitude = location.longitude
        )

        serviceScope.launch {
            val result = fireLocationSyncService.sendLocation(locationData)
            if (result.isSuccess) {
                Log.d("MyLocationService", "Location sent successfully for driver $currentDriverId")
            } else {
                Log.e("MyLocationService", "Failed to send location for driver $currentDriverId: ${result.exceptionOrNull()?.message}")
            }
        }

        val updatedNotification = notification?.setContentText(
            "Location: (${location.latitude}, ${location.longitude})"
        )
        notificationManager?.notify(1, updatedNotification?.build())
    }
}