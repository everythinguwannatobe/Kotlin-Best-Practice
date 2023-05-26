package org.lotka.bp.presentation.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import org.lotka.bp.presentation.components.CircularIndeterminateProgressBar
import org.lotka.bp.presentation.components.ConnectivityMonitor
import org.lotka.bp.presentation.components.GenericDialog
import org.lotka.bp.presentation.components.GenericDialogInfo
import java.util.Queue

//private val LightThemeColors = lightColors(
//    primary = Blue600,
//    primaryVariant = Blue400,
//    onPrimary = Black2,
//    secondary = Color.White,
//    secondaryVariant = Teal300,
//    onSecondary = Color.Black,
//    error = RedErrorDark,
//    onError = RedErrorLight,
//    background = Grey1,
//    onBackground = Color.Black,
//    surface = Color.White,
//    onSurface = Black2,
//)

//private val DarkThemeColors = darkColors(
//    primary = Blue700,
//    primaryVariant = Color.White,
//    onPrimary = Color.White,
//    secondary = Black1,
//    onSecondary = Color.White,
//    error = RedErrorLight,
//    background = Color.Black,
//    onBackground = Color.White,
//    surface = Black1,
//    onSurface = Color.White,
//)

@ExperimentalComposeUiApi
@Composable
fun AppTheme(
    darkTheme: Boolean,
    isNetworkAvailable: Boolean,
    displayProgressBar: Boolean,
    dialogQueue: Queue<GenericDialogInfo>? = null,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        typography = YekanBakhTypography,
        shapes = AppShapes
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = if (!darkTheme) Grey1 else Color.Black)
        ) {
            Column {
                ConnectivityMonitor(isNetworkAvailable = isNetworkAvailable)
                content()
            }
            CircularIndeterminateProgressBar(isDisplayed = displayProgressBar, 0.3f)
            ProcessDialogQueue(
                dialogQueue = dialogQueue,
            )
        }
    }
}


@Composable
fun ProcessDialogQueue(
    dialogQueue: Queue<GenericDialogInfo>?,
) {
    dialogQueue?.peek()?.let { dialogInfo ->
        GenericDialog(
            onDismiss = dialogInfo.onDismiss,
            title = dialogInfo.title,
            description = dialogInfo.description,
            positiveAction = dialogInfo.positiveAction,
            negativeAction = dialogInfo.negativeAction
        )
    }
}








































