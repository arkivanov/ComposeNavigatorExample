package com.arkivanov.composenavigatorexample.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.composenavigatorexample.navigator.rememberRouter
import com.arkivanov.decompose.extensions.compose.jetbrains.Children
import com.arkivanov.decompose.extensions.compose.jetbrains.animation.child.crossfadeScale
import com.arkivanov.decompose.pop
import com.arkivanov.decompose.push
import com.arkivanov.decompose.statekeeper.Parcelable
import com.arkivanov.decompose.statekeeper.Parcelize

@Composable
fun List(onItemClick: (String) -> Unit) {
    val items = remember { List(100) { "Item $it" } }

    LazyColumn {
        items(items) { item ->
            Text(
                text = item,
                modifier = Modifier
                    .clickable { onItemClick(item) }
                    .fillMaxWidth()
                    .padding(16.dp)
            )
        }
    }
}

@Composable
fun Details(text: String, onBack: () -> Unit) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = text)

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onBack) {
            Text(text = "Back")
        }
    }
}

@Composable
fun Main() {
    val router =
        rememberRouter<Screen>(
            initialConfiguration = { Screen.List },
            handleBackButton = true
        )

    Children(
        routerState = router.state,
        animation = crossfadeScale()
    ) { screen ->
        when (val configuration = screen.configuration) {
            is Screen.List -> List(onItemClick = { router.push(Screen.Details(text = it)) })
            is Screen.Details -> Details(text = configuration.text, onBack = router::pop)
        }
    }
}

sealed class Screen : Parcelable {

    @Parcelize
    object List : Screen()

    @Parcelize
    data class Details(val text: String) : Screen()
}
