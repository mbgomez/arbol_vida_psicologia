package com.netah.hakkam.numyah.mind.ui.nav.route

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.netah.hakkam.numyah.mind.ui.screen.DetailScreen
import com.netah.hakkam.numyah.mind.ui.screen.LoginScreen
import com.netah.hakkam.numyah.mind.ui.screen.MainScreen
import com.netah.hakkam.numyah.mind.viewmodel.AuthViewModel
import com.netah.hakkam.numyah.mind.viewmodel.MainViewModel

@Composable
fun LoginRoute(navigateToMain: () -> Unit) {

    val viewModel = hiltViewModel<AuthViewModel>()
    LoginScreen(authViewModel = viewModel, navigateToMain = navigateToMain)
}

@Composable
fun MainRoute(navigateToDetails: (index: Int) -> Unit) {
    val viewModel = hiltViewModel<MainViewModel>()
    MainScreen(viewModel = viewModel, navigateToDetails = navigateToDetails)
}

@Composable
fun DetailRoute(
    index: Int
) {
    val viewModel = hiltViewModel<MainViewModel>()
    DetailScreen(viewModel = viewModel, index = index)
}