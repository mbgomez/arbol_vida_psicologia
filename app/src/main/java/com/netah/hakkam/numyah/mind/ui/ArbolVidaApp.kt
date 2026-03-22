package com.netah.hakkam.numyah.mind.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import androidx.compose.foundation.isSystemInDarkTheme
import com.netah.hakkam.numyah.mind.R
import com.netah.hakkam.numyah.mind.ui.nav.MainNavGraph
import com.netah.hakkam.numyah.mind.ui.theme.AppTheme
import com.netah.hakkam.numyah.mind.viewmodel.AppStateViewModel

@Composable
fun ArbolVidaApp(
    appStateViewModel: AppStateViewModel = hiltViewModel()
) {
    val uiState by appStateViewModel.uiState.collectAsState()
    val systemInDarkTheme = isSystemInDarkTheme()

    AppTheme(
        darkTheme = uiState.themeMode.resolve(systemInDarkTheme)
    ) {
        if (uiState.isLoading) {
            AppLoadingScreen()
        } else {
            val navController = rememberNavController()
            MainNavGraph(
                navController = navController,
                startDestination = uiState.startDestination
            )
        }
    }
}

@Composable
private fun AppLoadingScreen() {
    val horizontalPadding = dimensionResource(R.dimen.spacing_2xl)
    val topSpacing = dimensionResource(R.dimen.spacing_2xl)
    val sectionSpacing = dimensionResource(R.dimen.spacing_md)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = horizontalPadding),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        Text(
            text = stringResource(R.string.app_loading_title),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(top = topSpacing),
            textAlign = TextAlign.Center
        )
        Text(
            text = stringResource(R.string.app_loading_body),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = sectionSpacing),
            textAlign = TextAlign.Center
        )
    }
}
