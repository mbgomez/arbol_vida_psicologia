package com.netah.hakkam.numyah.mind.ui.nav

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.netah.hakkam.numyah.mind.ui.nav.NumyahMindDestinations.DETAIL_ROUTE
import com.netah.hakkam.numyah.mind.ui.nav.NumyahMindDestinations.MAIN_ROUTE
import com.netah.hakkam.numyah.mind.ui.nav.route.DetailRoute
import com.netah.hakkam.numyah.mind.ui.nav.route.LoginRoute
import com.netah.hakkam.numyah.mind.ui.nav.route.MainRoute


object NumyahMindDestinations {
    const val LOGIN_ROUTE = "numyah_mind_login"
    const val MAIN_ROUTE = "numyah_mind_main"
    const val DETAIL_ROUTE = "numyah_mind_detail_main"

}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MainNavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: String = NumyahMindDestinations.LOGIN_ROUTE
) {
    val actions = remember(navController) { MainNavActions(navController) }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(route = NumyahMindDestinations.LOGIN_ROUTE) {
            LoginRoute(actions.navigateToMain)
        }
        composable(route = NumyahMindDestinations.MAIN_ROUTE) {
            MainRoute(actions.navigateToDetails)
        }
        composable(
            route = "$DETAIL_ROUTE/{index}",
            arguments = listOf(navArgument("index") { type = NavType.IntType })
        )
        { backStackEntry ->
            val index = backStackEntry.arguments?.getInt("index") ?: 0
            DetailRoute(index = index)
        }
    }
}

class MainNavActions(navController: NavHostController) {

    val navigateToDetails: (index: Int) -> Unit = {
        navController.navigate("$DETAIL_ROUTE/$it")
    }

    val navigateToMain: () -> Unit = {
        navController.navigate(MAIN_ROUTE)
    }

}
