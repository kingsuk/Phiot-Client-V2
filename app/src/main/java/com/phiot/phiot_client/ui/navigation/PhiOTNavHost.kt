package com.phiot.phiot_client.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.phiot.phiot_client.ui.components.LoadingScreen
import com.phiot.phiot_client.ui.dataset.DatasetScreen
import com.phiot.phiot_client.ui.login.LoginScreen
import com.phiot.phiot_client.ui.main.MainScreen
import com.phiot.phiot_client.ui.session.SessionViewModel

@Composable
fun PhiOTNavHost(
    sessionViewModel: SessionViewModel = hiltViewModel(),
) {
    val navController = rememberNavController()
    val session by sessionViewModel.session.collectAsStateWithLifecycle()
    val isReady by sessionViewModel.isReady.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        sessionViewModel.bootstrap()
    }

    if (!isReady) {
        LoadingScreen()
        return
    }

    val startDestination = if (session != null) MainRoute else LoginRoute

    NavHost(
        navController = navController,
        startDestination = startDestination,
    ) {
        composable<LoginRoute> {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(MainRoute) {
                        popUpTo<LoginRoute> { inclusive = true }
                    }
                },
            )
        }

        composable<MainRoute> {
            if (session == null) {
                LaunchedEffect(Unit) {
                    navController.navigate(LoginRoute) {
                        popUpTo<MainRoute> { inclusive = true }
                    }
                }
                LoadingScreen()
            } else {
                MainScreen(
                    onLogout = {
                        sessionViewModel.logout {
                            navController.navigate(LoginRoute) {
                                popUpTo<MainRoute> { inclusive = true }
                            }
                        }
                    },
                    onDeviceClick = { device ->
                        navController.navigate(
                            DatasetRoute(
                                deviceId = device.id,
                                token = device.deviceToken,
                            ),
                        )
                    },
                )
            }
        }

        composable<DatasetRoute> { backStackEntry ->
            val route = backStackEntry.toRoute<DatasetRoute>()
            DatasetScreen(
                deviceId = route.deviceId,
                deviceToken = route.token,
                onBack = { navController.popBackStack() },
            )
        }
    }
}
