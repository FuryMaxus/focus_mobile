package com.example.focus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.focus.network.RetrofitClient
import com.example.focus.ui.MainScreen
import com.example.focus.ui.theme.GremioFocusTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        RetrofitClient.initialize(this)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GremioFocusTheme {
                MainScreen()
            }
        }
    }
}