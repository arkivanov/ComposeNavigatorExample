package com.arkivanov.composenavigatorexample.android

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import com.arkivanov.composenavigatorexample.navigator.ProvideComponentContext
import com.arkivanov.composenavigatorexample.screens.MainContent
import com.arkivanov.decompose.defaultComponentContext

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val rootComponentContext = defaultComponentContext()

        setContent {
            MaterialTheme {
                Surface {
                    ProvideComponentContext(rootComponentContext) {
                        MainContent()
                    }
                }
            }
        }
    }
}
