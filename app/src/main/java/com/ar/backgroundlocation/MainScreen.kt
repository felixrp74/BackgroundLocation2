package com.ar.backgroundlocation

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

/**
 * @Author: Abdul Rehman
 * @Date: 06/05/2024.
 */

@Composable
fun App() {
    val navController = rememberNavController()
    val context = LocalContext.current

    NavHost(navController = navController, startDestination = "main") {
        composable("main") {
            MainScreen(
                onNavigateToMap = { navController.navigate("map") },
                onStartLocationService = {
                    Intent(context, LocationService::class.java).apply {
                        action = LocationService.ACTION_SERVICE_START
                        context.startService(this)
                    }
                },
                onStopLocationService = {
                    Intent(context, LocationService::class.java).apply {
                        action = LocationService.ACTION_SERVICE_STOP
                        context.startService(this)
                    }
                }
            )
        }
        composable("map") {
            MapScreen()
        }
    }
}

@Composable
fun MainScreen(
    onNavigateToMap: () -> Unit,
    onStartLocationService: () -> Unit,
    onStopLocationService: () -> Unit
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = {
            Toast.makeText(context, "Boton de inicialización GPS", Toast.LENGTH_SHORT).show()
            onStartLocationService()
        }) {
            Text(text = "Iniciar GPS")
        }

        Spacer(modifier = Modifier.padding(12.dp))

        Button(onClick = {
            Toast.makeText(context, "Boton de finalización GPS", Toast.LENGTH_SHORT).show()
            onStopLocationService()
        }) {
            Text(text = "Finalizar GPS")
        }

        Spacer(modifier = Modifier.padding(12.dp))

        Button(onClick = {
            Toast.makeText(context, "Ver mapa en tiempo real", Toast.LENGTH_SHORT).show()
            onNavigateToMap()
        }) {
            Text(text = "Ver Mapa")
        }
    }
}

@Preview
@Composable
fun AppPreview() {
    App()
}