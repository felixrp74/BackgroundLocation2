package com.ar.backgroundlocation

// FirestoreLocationSyncService.kt
import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FirestoreLocationSyncService(
    private val firestore: FirebaseFirestore) : LocationSyncService {

    // Asumimos que tienes una colección llamada "user_locations"
    // y cada documento representa una ubicación con un ID de usuario.
    // Podrías estructurarlo de manera diferente según tus necesidades.
    // Por ejemplo, un documento por usuario con un array de últimas ubicaciones,
    // o un documento por cada actualización de ubicación.

    // Ejemplo: Guardar cada actualización de ubicación como un nuevo documento
    // en una subcolección del usuario.
    // Necesitarás el ID del usuario actual.
    private fun getUserLocationsCollection(userId: String) =
        firestore.collection("drivers")
            .document(userId)
            .collection("location_history")

    // Ejemplo: Actualizar la última ubicación conocida de un usuario
    private fun getUserDocument(userId: String) =
        firestore.collection("drivers")
            .document(userId)


    override suspend fun sendLocation(locationData: LocationData): Result<Unit> {

        // Debes obtener el ID del usuario actual de alguna forma (Firebase Auth, etc.)
        val currentUserId = getCurrentUserId() // Implementa esta función
        if (currentUserId == null) {
            return Result.failure(Exception("User not authenticated"))
        }

        return withContext(Dispatchers.IO) { // Realizar operaciones de red en el hilo de IO
            try {
                // Opción 1: Guardar cada punto como un nuevo documento en una subcolección
                // val locationDoc = mapOf(
                //     "coordinates" to GeoPoint(locationData.latitude, locationData.longitude),
                //     "timestamp" to locationData.timestamp
                // )
                // getUserLocationsCollection(currentUserId).add(locationDoc).await()

                // Opción 2: Actualizar la última ubicación conocida del usuario en su documento
                val lastLocationUpdate = mapOf(
                    "last_known_location" to GeoPoint(locationData.latitude, locationData.longitude),
                    "last_location_timestamp" to locationData.timestamp
                )
                //getUserDocument(currentUserId).update(lastLocationUpdate).await()
                getUserDocument(currentUserId).set(lastLocationUpdate,
                    SetOptions.merge()).await()

                // O .set(lastocationUpdate, SetOptions.merge()) si el documento podría no existirL
                Result.success(Unit)
            } catch (e: Exception) {
                // Loggear el error, por ejemplo, con Timber o Log.e
                Log.e("FirestoreLocationSync", "Error sending location", e)
                Result.failure(e)
            }
        }
    }

    // No son necesarios para Firestore en este contexto simple de envío
    override fun connect() { /* No aplicable para envíos simples a Firestore */ }
    override fun disconnect() { /* No aplicable para envíos simples a Firestore */ }

    // Implementa esto según tu sistema de autenticación
    private fun getCurrentUserId(): String? {
        // Ejemplo: return FirebaseAuth.getInstance().currentUser?.uid
        return "driver1" // Placeholder
    }
}
