package com.ar.backgroundlocation

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ar.backgroundlocation.network.LoginService
import kotlinx.coroutines.launch

//import com.ar.backgroundlocation.network.LoginService

/**
 * @Author: Abdul Rehman
 * @Date: 06/05/2024.
 */

@Composable
fun App() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val loginService = remember { LoginService(context) }
    val startDestination = if (loginService.isLoggedIn()) "main" else "login"

    NavHost(navController = navController, startDestination = startDestination) {
        composable("login") {
            LoginScreen(
                onLoginSuccess = { navController.navigate("main") },
                loginService = loginService
            )
        }
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
                },
                loginService = loginService,
                navController = navController
            )
        }
        composable("map") {
            MapScreen()
        }
    }
}

@Composable
fun MainScreen(
    loginService: LoginService,
    navController: androidx.navigation.NavHostController,
    onNavigateToMap: () -> Unit,
    onStartLocationService: () -> Unit,
    onStopLocationService: () -> Unit
) {
    val context = LocalContext.current
    val loginService = remember { LoginService(context) }

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

        Spacer(modifier = Modifier.padding(12.dp))

        Button(onClick = {
            loginService.logout()
            Toast.makeText(context, "Sesión cerrada", Toast.LENGTH_SHORT).show()
            navController.navigate("login") {
                popUpTo("main") { inclusive = true }
            }
        }) {
            Text(text = "Cerrar Sesión")
        }
    }
}

@Preview
@Composable
fun AppPreview() {
    App()
}



@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    loginService: LoginService
) {
    val context = LocalContext.current
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    username = "beatit"
    password = "frpbeatit"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Iniciar Sesión", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))
//        Image(
//            painter = painterResource(id = R.drawable.),
//            contentDescription = "Decoración oceánica",
//            modifier = Modifier
//                .height(180.dp)
//                .fillMaxWidth()
//                .padding(vertical = 16.dp),
//            contentScale = ContentScale.Crop
//        )
//        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Usuario") }
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                isLoading = true
                scope.launch {
                    val success = loginService.login(username, password)
                    isLoading = false
                    if (success) {
                        Toast.makeText(context, "Sesión iniciada",
                            Toast.LENGTH_SHORT).show()
                        onLoginSuccess()
                    } else {
                        Toast.makeText(context, "Credenciales incorrectas",
                            Toast.LENGTH_SHORT).show()
                    }
                }
            },
            enabled = !isLoading
        ) {
            Text(if (isLoading) "Cargando..." else "Ingresar")
        }
    }
}
