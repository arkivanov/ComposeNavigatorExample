package com.arkivanov.composenavigatorexample.navigator

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.ProvidableAmbient
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.AmbientSaveableStateRegistry
import androidx.compose.runtime.saveable.SaveableStateRegistry
import androidx.compose.runtime.staticAmbientOf
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.decompose.Navigator
import com.arkivanov.decompose.backpressed.BackPressedDispatcher
import com.arkivanov.decompose.extensions.compose.jetbrains.Children
import com.arkivanov.decompose.lifecycle.Lifecycle
import com.arkivanov.decompose.lifecycle.LifecycleRegistry
import com.arkivanov.decompose.lifecycle.destroy
import com.arkivanov.decompose.lifecycle.resume
import com.arkivanov.decompose.statekeeper.Parcelable
import com.arkivanov.decompose.statekeeper.ParcelableContainer
import com.arkivanov.decompose.statekeeper.StateKeeper
import com.arkivanov.decompose.statekeeper.StateKeeperDispatcher
import kotlin.reflect.KClass

@Composable
inline fun <reified C : Parcelable> Navigator(
    noinline initialConfiguration: () -> C,
    noinline initialBackStack: () -> List<C> = ::emptyList,
    handleBackButton: Boolean = false,
    noinline animation: @Composable (C, @Composable (C) -> Unit) -> Unit = { configuration, contentCallback ->
        contentCallback(
            configuration
        )
    },
    noinline content: @Composable Navigator<C>.(C) -> Unit
) {
    Navigator(
        initialConfiguration = initialConfiguration,
        initialBackStack = initialBackStack,
        configurationClass = C::class,
        handleBackButton = handleBackButton,
        animation = animation,
        content = content
    )
}

@Composable
fun <C : Parcelable> Navigator(
    initialConfiguration: () -> C,
    initialBackStack: () -> List<C> = ::emptyList, // <-- Add this line
    configurationClass: KClass<out C>,
    handleBackButton: Boolean = false,
    animation: @Composable (C, @Composable (C) -> Unit) -> Unit = { configuration, contentCallback ->
        contentCallback(
            configuration
        )
    },
    content: @Composable Navigator<C>.(C) -> Unit
) {
    val context = componentContext()

    val router =
        remember {
            context.router(
                initialConfiguration = initialConfiguration,
                initialBackStack = initialBackStack, // <-- Add this line
                configurationClass = configurationClass,
                handleBackButton = handleBackButton
            ) { configuration, _ -> configuration }
        }

    Children(
        routerState = router.state,
        animation = { _, configuration, contentCallback ->
            animation(configuration) { contentCallback(it, it) }
        }
    ) { _, configuration -> router.content(configuration) }
}

@Composable
fun componentContext(): ComponentContext {
    val lifecycle = lifecycle()
    val stateKeeper = stateKeeper()
    val backPressedDispatcher = AmbientBackPressedDispatcher.current ?: BackPressedDispatcher()

    return remember {
        DefaultComponentContext(
            lifecycle = lifecycle,
            stateKeeper = stateKeeper,
            backPressedDispatcher = backPressedDispatcher
        )
    }
}

@Composable
private fun lifecycle(): Lifecycle {
    val lifecycle = remember { LifecycleRegistry() }

    DisposableEffect(Unit) {
        lifecycle.resume()
        onDispose { lifecycle.destroy() }
    }

    return lifecycle
}

@Composable
private fun stateKeeper(): StateKeeper {
    val saveableStateRegistry: SaveableStateRegistry? = AmbientSaveableStateRegistry.current

    val dispatcher =
        remember {
            StateKeeperDispatcher(saveableStateRegistry?.consumeRestored(KEY_STATE) as ParcelableContainer?)
        }

    if (saveableStateRegistry != null) {
        DisposableEffect(Unit) {
            val entry = saveableStateRegistry.registerProvider(KEY_STATE, dispatcher::save)
            onDispose { entry.unregister() }
        }
    }

    return dispatcher
}

val AmbientBackPressedDispatcher: ProvidableAmbient<BackPressedDispatcher?> =
    staticAmbientOf { null }

private const val KEY_STATE = "STATE"
