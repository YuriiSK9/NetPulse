package com.yurii.netpulse

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yurii.netpulse.data.NetworkDataSource
import com.yurii.netpulse.ui.NetPulseScreen
import com.yurii.netpulse.ui.theme.NetPulseTheme

class MainActivity : ComponentActivity() {

    private lateinit var networkDataSource: NetworkDataSource

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        networkDataSource = NetworkDataSource(applicationContext)

        enableEdgeToEdge()
        setContent {
            val networkState by networkDataSource.networkStateFlow.collectAsStateWithLifecycle(networkDataSource.getCurrentNetworkState())
            NetPulseTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NetPulseScreen(networkState, Modifier.padding(innerPadding))
                }
            }
        }
    }

}