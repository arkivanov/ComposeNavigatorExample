package com.arkivanov.composenavigatorexample.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.arkivanov.composenavigatorexample.navigator.Navigator
import com.arkivanov.decompose.pop
import com.arkivanov.decompose.push
import com.arkivanov.decompose.statekeeper.Parcelable
import com.arkivanov.decompose.statekeeper.Parcelize

@Composable
fun List(onItemClick: (String) -> Unit) {
    val items = remember { List(100) { "Item $it" } }

    LazyColumn {
        items(items) { item ->
            Text(text = item, modifier = Modifier.clickable { onItemClick(item) })
        }
    }
}

@Composable
fun Details(text: String, onBack: () -> Unit) {
    Column {
        Text(text = text)
        Button(onClick = onBack) {
            Text(text = "Back")
        }
    }
}

@Composable
fun Main() {
    Navigator<Screen>(
        initialConfiguration = { Screen.List },
        handleBackButton = true,
        // Crossfade does not preserve UI state properly since (probably) 0.3.0-build146.
        // Uncomment when https://issuetracker.google.com/u/1/issues/178729296 is fixed.
//        animation = { screen, contentCallback ->
//            Crossfade(targetState = screen, content = contentCallback)
//        }
    ) { screen ->
        when (screen) {
            is Screen.List -> List(onItemClick = { push(Screen.Details(text = it)) })
            is Screen.Details -> Details(text = screen.text, onBack = { pop() })
        }
    }
}

sealed class Screen : Parcelable {

    @Parcelize
    object List : Screen()

    @Parcelize
    data class Details(val text: String) : Screen()
}
