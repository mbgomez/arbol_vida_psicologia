package com.netah.hakkam.numyah.mind.ui.components

import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun DefaultSnackBar(
    snackBarHostState: SnackbarHostState,
    modifier: Modifier,
    onDismiss: () -> Unit
) {
    SnackbarHost(
        hostState = snackBarHostState,
        snackbar = { data ->
            Snackbar(
                content = {
                    Text(text = data.visuals.message)
                },
                action = {
                    data.visuals.actionLabel?.let { actionLabel ->
                        TextButton(onClick = onDismiss) {
                            Text(text = actionLabel)
                        }
                    }
                }
            )
        },
        modifier = modifier
    )
}
