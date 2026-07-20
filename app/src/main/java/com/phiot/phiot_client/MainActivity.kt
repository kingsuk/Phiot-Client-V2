package com.phiot.phiot_client

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.phiot.phiot_client.ui.navigation.PhiOTNavHost
import com.phiot.phiot_client.ui.theme.PhiOTTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PhiOTTheme {
                PhiOTNavHost()
            }
        }
    }
}
