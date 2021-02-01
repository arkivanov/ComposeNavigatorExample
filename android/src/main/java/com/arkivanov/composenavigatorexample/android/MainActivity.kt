package com.arkivanov.composenavigatorexample.android

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Providers
import androidx.compose.ui.platform.setContent
import com.arkivanov.composenavigatorexample.navigator.AmbientBackPressedDispatcher
import com.arkivanov.composenavigatorexample.screens.Main
import com.arkivanov.decompose.backpressed.toBackPressedDispatcher

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val backPressedDispatcher = onBackPressedDispatcher.toBackPressedDispatcher()

        setContent {
            MaterialTheme {
                Surface {
                    Providers(AmbientBackPressedDispatcher provides backPressedDispatcher) {
                        Main()
                    }
                }
            }
        }
    }
}
