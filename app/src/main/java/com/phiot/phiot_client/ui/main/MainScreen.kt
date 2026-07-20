package com.phiot.phiot_client.ui.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.phiot.phiot_client.data.model.Device
import com.phiot.phiot_client.ui.devices.DevicesScreen
import com.phiot.phiot_client.ui.navigation.MainTab
import com.phiot.phiot_client.ui.setup.SetupScreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onLogout: () -> Unit,
    onDeviceClick: (Device) -> Unit,
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var selectedTab by rememberSaveable { mutableStateOf(MainTab.Devices) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text(
                    text = "PhiOT Client",
                    modifier = Modifier.padding(horizontal = 28.dp, vertical = 24.dp),
                )
                Text(
                    text = "phiot.phibasis.com",
                    modifier = Modifier.padding(horizontal = 28.dp, vertical = 0.dp),
                )
                NavigationDrawerItem(
                    label = { Text("Devices") },
                    selected = selectedTab == MainTab.Devices,
                    icon = { Icon(Icons.Default.Devices, contentDescription = null) },
                    onClick = {
                        selectedTab = MainTab.Devices
                        scope.launch { drawerState.close() }
                    },
                    modifier = Modifier.padding(horizontal = 12.dp),
                )
                NavigationDrawerItem(
                    label = { Text("Setup New Device") },
                    selected = selectedTab == MainTab.Setup,
                    icon = { Icon(Icons.Default.Wifi, contentDescription = null) },
                    onClick = {
                        selectedTab = MainTab.Setup
                        scope.launch { drawerState.close() }
                    },
                    modifier = Modifier.padding(horizontal = 12.dp),
                )
                NavigationDrawerItem(
                    label = { Text("Logout") },
                    selected = false,
                    icon = { Icon(Icons.Default.Logout, contentDescription = null) },
                    onClick = {
                        scope.launch { drawerState.close() }
                        onLogout()
                    },
                    modifier = Modifier.padding(horizontal = 12.dp),
                )
            }
        },
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Open menu")
                        }
                    },
                    title = {
                        Text(
                            when (selectedTab) {
                                MainTab.Devices -> "Devices"
                                MainTab.Setup -> "Setup Device"
                            },
                        )
                    },
                )
            },
        ) { padding ->
            when (selectedTab) {
                MainTab.Devices -> DevicesScreen(
                    modifier = Modifier.padding(padding),
                    onDeviceClick = onDeviceClick,
                )
                MainTab.Setup -> SetupScreen(modifier = Modifier.padding(padding))
            }
        }
    }
}
