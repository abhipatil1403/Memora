package com.memora

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.memora.core.ui.theme.MemoraTheme
import com.memora.navigation.MemoraNavigation
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MemoraTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = com.memora.core.ui.theme.MemoraTheme.colors.background
                ) {
                    MemoraNavigation(startIntentAction = intent?.action)
                }
            }
        }
    }
}
