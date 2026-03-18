package com.netah.hakkam.numyah.mind.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MillionButtonView(
    modifier: Modifier = Modifier,
    action: () -> Unit,
    text:String,
    enabled: MutableState<Boolean> = mutableStateOf(true)
) {
    Box(
        modifier = modifier
            .padding(16.dp)
    ) {
        Button(
            modifier = Modifier.align(Alignment.Center),
            onClick = action,
            enabled = enabled.value
        ) {
            Text(
                modifier = modifier.padding(32.dp),
                fontSize = 32.sp,
                text = text
            )
        }
    }
}

@Composable
fun HelpForLifeCustomButtonView(
    modifier: Modifier = Modifier,
    action: () -> Unit,
    text:String,
    enabled: MutableState<Boolean> = mutableStateOf(true)
) {
    Box(
        modifier = modifier
    ) {
        Button(
            modifier = Modifier.align(Alignment.Center),
            onClick = action,
            enabled = enabled.value
        ) {
            Text(
                modifier = modifier.padding(16.dp),
                fontSize = 18.sp,
                text = text
            )
        }
    }
}
