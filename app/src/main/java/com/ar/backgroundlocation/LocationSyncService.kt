package com.ar.backgroundlocation

interface LocationSyncService {
    suspend fun sendLocation(locationData: LocationData): Result<Unit> // Usamos Result para manejar éxito/error
    fun connect() // Para WebSocket
    fun disconnect() // Para WebSocket
    // Puedes añadir un callback o Flow para observar el estado de la conexión si usas WebSockets
    // fun observeConnectionState(): Flow<Boolean>
}