package com.example.eventable.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalConfiguration

/**
 * Width/height breakpoints mirror Google's recommended window size class
 * breakpoints for adapting layouts across phones, foldables and tablets:
 * https://developer.android.com/guide/topics/large-screens/support-different-screen-sizes#window_size_classes
 */
enum class WindowWidthSizeClass { COMPACT, MEDIUM, EXPANDED }
enum class WindowHeightSizeClass { COMPACT, MEDIUM, EXPANDED }

data class WindowSizeClass(
    val widthSizeClass: WindowWidthSizeClass,
    val heightSizeClass: WindowHeightSizeClass
)

private fun widthSizeClass(widthDp: Int): WindowWidthSizeClass = when {
    widthDp < 600 -> WindowWidthSizeClass.COMPACT
    widthDp < 840 -> WindowWidthSizeClass.MEDIUM
    else -> WindowWidthSizeClass.EXPANDED
}

private fun heightSizeClass(heightDp: Int): WindowHeightSizeClass = when {
    heightDp < 480 -> WindowHeightSizeClass.COMPACT
    heightDp < 900 -> WindowHeightSizeClass.MEDIUM
    else -> WindowHeightSizeClass.EXPANDED
}

@Composable
fun rememberWindowSizeClass(): WindowSizeClass {
    val configuration = LocalConfiguration.current
    return WindowSizeClass(
        widthSizeClass = widthSizeClass(configuration.screenWidthDp),
        heightSizeClass = heightSizeClass(configuration.screenHeightDp)
    )
}

private val DefaultWindowSizeClass = WindowSizeClass(
    widthSizeClass = WindowWidthSizeClass.COMPACT,
    heightSizeClass = WindowHeightSizeClass.MEDIUM
)

val LocalWindowSizeClass = staticCompositionLocalOf { DefaultWindowSizeClass }
