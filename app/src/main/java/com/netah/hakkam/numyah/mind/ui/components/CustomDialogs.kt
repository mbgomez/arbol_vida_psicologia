package com.netah.hakkam.numyah.mind.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.netah.hakkam.numyah.mind.R

@Composable
fun DefaultDialog(
    title: String = "",
    message: String,
    confirmLabel: String,
    dismissLabel: String,
    confirmAction: () -> Unit,
    dismissAction: () -> Unit
) {
    val openDialog = remember { mutableStateOf(true) }
    if (openDialog.value) {
        AlertDialog(
            onDismissRequest = {
                openDialog.value = false
            },
            title = {
                Text(text = title)
            },
            text = {
                Text(message)
            },
            confirmButton = {
                Button(

                    onClick = {
                        confirmAction()
                        openDialog.value = false
                    }) {
                    Text(confirmLabel)
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        dismissAction()
                        openDialog.value = false
                    }) {
                    Text(dismissLabel)
                }
            }
        )
    }
}

@Composable
fun ReplaceInProgressAssessmentDialog(
    currentSephiraName: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = stringResource(R.string.replace_assessment_dialog_title))
        },
        text = {
            Text(
                text = stringResource(
                    R.string.replace_assessment_dialog_body,
                    currentSephiraName
                )
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = stringResource(R.string.replace_assessment_dialog_confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.replace_assessment_dialog_cancel))
            }
        }
    )
}

@Composable
fun AssessmentExitConfirmationDialog(
    destinationLabel: String,
    skipNextTimeChecked: Boolean,
    onSkipNextTimeChanged: (Boolean) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = stringResource(R.string.assessment_exit_dialog_title))
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = stringResource(
                        R.string.assessment_exit_dialog_body_to,
                        destinationLabel
                    )
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Checkbox(
                        checked = skipNextTimeChecked,
                        onCheckedChange = onSkipNextTimeChanged
                    )
                    Text(text = stringResource(R.string.assessment_exit_dialog_skip_future))
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    text = stringResource(
                        R.string.assessment_exit_dialog_confirm_to,
                        destinationLabel
                    )
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.assessment_exit_dialog_cancel))
            }
        }
    )
}
