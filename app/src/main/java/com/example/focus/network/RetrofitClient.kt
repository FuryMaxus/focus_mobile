package com.example.focus.network

import android.content.Context
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "http://10.0.2.2:8000/"

    // Variable para almacenar temporalmente el TokenManager
    private var tokenManager: TokenManager? = null

    // Función para inicializar el cliente desde MainActivity o MainScreen
    fun initialize(context: Context) {
        if (tokenManager == null) {
            tokenManager = TokenManager(context)
        }
    }

    // El interceptor que inyecta el token en cada petición HTTP
    private val authInterceptor = Interceptor { chain ->
        val requestBuilder = chain.request().newBuilder()

        // Leemos el token guardado. runBlocking es seguro aquí porque
        // OkHttp ejecuta esto en un hilo de fondo, no en el principal (UI)
        val token = runBlocking {
            tokenManager?.getToken?.firstOrNull()
        }

        if (!token.isNullOrEmpty()) {
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }

        chain.proceed(requestBuilder.build())
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient) // Le conectamos nuestro cliente con interceptor
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService: ApiService = retrofit.create(ApiService::class.java)
}