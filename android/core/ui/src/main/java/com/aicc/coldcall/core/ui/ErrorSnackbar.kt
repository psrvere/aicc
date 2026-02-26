package com.aicc.coldcall.core.ui

import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.TextButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ErrorSnackbar(
    snackbarData: SnackbarData,
    modifier: Modifier = Modifier,
    onRetry: (() -> Unit)? = null,
) {
    Snackbar(
        modifier = modifier,
        action = {
            onRetry?.let {
                TextButton(onClick = it) {
                    Text("Retry")
                }
            }
        },
    ) {
        Text(snackbarData.visuals.message)
    }
}
