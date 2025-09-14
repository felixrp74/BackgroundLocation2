//// LoginService.kt
//package com.ar.backgroundlocation.network
//
//import android.content.Context
//import android.content.SharedPreferences
//import io.ktor.client.*
//import io.ktor.client.call.*
//import io.ktor.client.request.*
//import io.ktor.client.statement.*
//import io.ktor.client.plugins.contentnegotiation.*
//import io.ktor.http.*
//import io.ktor.serialization.kotlinx.json.*
//import io.ktor.client.request.forms.*
//import kotlinx.serialization.Serializable
//
//@Serializable
//data class LoginResponse(val id: String, val name: String)
//
//class LoginService(private val context: Context) {
//
//    private val client = HttpClient {
//        install(ContentNegotiation) {
//            json()
//        }
//    }
//
//    private val prefs: SharedPreferences =
//        context.getSharedPreferences("user_session", Context.MODE_PRIVATE)
//
//    suspend fun login(username: String, password: String): Boolean {
//        return try {
//            val response: HttpResponse = client.post("https://lapt.com/get/uinformation") {
//                setBody(FormDataContent(Parameters.build {
//                    append("username", username)
//                    append("password", password)
//                }))
//                headers {
//                    append(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
//                }
//            }
//
//            if (response.status == HttpStatusCode.OK) {
//                val data: LoginResponse = response.body()
//                prefs.edit().putString("user_id", data.id)
//                    .putString("user_name", data.name)
//                    .putBoolean("is_logged_in", true)
//                    .apply()
//                true
//            } else {
//                false
//            }
//        } catch (e: Exception) {
//            false
//        }
//    }
//
//    fun isLoggedIn(): Boolean = prefs.getBoolean("is_logged_in", false)
//
//    fun logout() {
//        prefs.edit().clear().apply()
//    }
//
//    fun getUserId(): String? = prefs.getString("user_id", null)
//}
