package com.example.rentalproofer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.rentalproofer.ui.navigation.AppNavGraph
import com.example.rentalproofer.ui.theme.RentalProoferTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RentalProoferTheme {
                AppNavGraph()
            }
        }
    }
}
