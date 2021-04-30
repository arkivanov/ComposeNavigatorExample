package com.arkivanov.composenavigatorexample.android

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.CompositionLocalProvider
import com.arkivanov.composenavigatorexample.navigator.LocalBackPressedDispatcher
import com.arkivanov.composenavigatorexample.screens.Main
import com.arkivanov.decompose.backpressed.toBackPressedDispatcher

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val backPressedDispatcher = onBackPressedDispatcher.toBackPressedDispatcher()

        setContent {
            MaterialTheme {
                Surface {
                    CompositionLocalProvider(LocalBackPressedDispatcher provides backPressedDispatcher) {
                        Main()
                    }
                }
            }
        }
    }
}
