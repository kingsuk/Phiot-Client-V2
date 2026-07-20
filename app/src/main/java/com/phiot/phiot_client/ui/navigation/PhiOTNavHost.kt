package com.phiot.phiot_client.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.phiot.phiot_client.ui.components.LoadingScreen
import com.phiot.phiot_client.ui.dataset.DatasetScreen
import com.phiot.phiot_client.ui.login.LoginScreen
import com.phiot.phiot_client.ui.main.MainScreen
import com.phiot.phiot_client.ui.rememberRepository
import kotlinx.coroutines.launch

object Routes {
    const val LOGIN = "login"
    const val MAIN = "main"
    const val DATASET = "dataset?deviceId={deviceId}&token={token}"

    fun dataset(deviceId: String, token: String): String =
        "dataset?deviceId=$deviceId&token=$token"
}

@Composable
fun PhiOTNavHost() {
    val repository = rememberRepository()
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()
    val session by repository.session.collectAsStateWithLifecycle(initialValue = null)
    var isReady by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        repository.restoreSession()
        isReady = true
    }

    if (!isReady) {
        LoadingScreen()
        return
    }

    NavHost(
        navController = navController,
        startDestination = if (session != null) Routes.MAIN else Routes.LOGIN,
    ) {
        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Routes.MAIN) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
            )
        }

        composable(Routes.MAIN) {
            if (session == null) {
                LaunchedEffect(Unit) {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.MAIN) { inclusive = true }
                    }
                }
                LoadingScreen()
            } else {
                MainScreen(
                    onLogout = {
                        scope.launch {
                            repository.logout()
                            navController.navigate(Routes.LOGIN) {
                                popUpTo(Routes.MAIN) { inclusive = true }
                            }
                        }
                    },
                    onDeviceClick = { device ->
                        navController.navigate(Routes.dataset(device.id, device.deviceToken))
                    },
                )
            }
        }

        composable(
            route = Routes.DATASET,
            arguments = listOf(
                navArgument("deviceId") { type = NavType.StringType },
                navArgument("token") { type = NavType.StringType },
            ),
        ) { backStackEntry ->
            val deviceId = backStackEntry.arguments?.getString("deviceId").orEmpty()
            val token = backStackEntry.arguments?.getString("token").orEmpty()
            DatasetScreen(
                deviceId = deviceId,
                deviceToken = token,
                onBack = { navController.popBackStack() },
            )
        }
    }
}
