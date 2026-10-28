package com.example

import android.Manifest
import android.app.Activity
import android.content.Context
import android.net.VpnService
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.screens.DiagnosticsScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.PrivacyScreen
import com.example.ui.screens.ServersScreen
import com.example.ui.theme.*
import com.example.vpn.model.VpnServer
import com.example.vpn.model.VpnState
import com.example.vpn.service.VpnController

enum class MainTab(val label: String) {
    HOME("Shield"),
    SERVERS("Locations"),
    PRIVACY("Privacy"),
    DIAGNOSTICS("Diagnostics")
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                TigerVpnApp()
            }
        }
    }
}

@Composable
fun TigerVpnApp() {
    val context = LocalContext.current
    var currentTab by remember { mutableStateOf(MainTab.HOME) }

    val vpnState by VpnController.vpnState.collectAsStateWithLifecycle()
    val selectedServer by VpnController.selectedServer.collectAsStateWithLifecycle()
    val servers by VpnController.servers.collectAsStateWithLifecycle()
    val statistics by VpnController.statistics.collectAsStateWithLifecycle()
    val settings by VpnController.settings.collectAsStateWithLifecycle()
    val logs by VpnController.logs.collectAsStateWithLifecycle()

    // Request notification permission on Android 13+
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { /* Permission result handled gracefully */ }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    // Android VPN preparation launcher
    val vpnPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            VpnController.startVpn(context)
        } else {
            VpnController.onVpnDisconnected()
            VpnController.addLog("VPN permission was declined by user.", com.example.vpn.model.LogLevel.WARNING)
        }
    }

    fun handleToggleVpn() {
        if (vpnState == VpnState.CONNECTED || vpnState == VpnState.CONNECTING) {
            VpnController.stopVpn(context)
        } else {
            val prepareIntent = VpnService.prepare(context)
            if (prepareIntent != null) {
                vpnPermissionLauncher.launch(prepareIntent)
            } else {
                VpnController.startVpn(context)
            }
        }
    }

    fun handleSelectServer(server: VpnServer) {
        val wasConnected = vpnState == VpnState.CONNECTED
        VpnController.selectServer(server)
        currentTab = MainTab.HOME

        if (wasConnected) {
            // Re-establish tunnel with new server
            val prepareIntent = VpnService.prepare(context)
            if (prepareIntent != null) {
                vpnPermissionLauncher.launch(prepareIntent)
            } else {
                VpnController.startVpn(context)
            }
        }
    }

    // Handle back button on sub-screens to return to Home screen
    if (currentTab != MainTab.HOME) {
        BackHandler {
            currentTab = MainTab.HOME
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = DarkBackground,
        contentWindowInsets = WindowInsets.safeDrawing,
        bottomBar = {
            NavigationBar(
                containerColor = DarkSurface,
                tonalElevation = 8.dp,
                windowInsets = WindowInsets.navigationBars,
                modifier = Modifier.testTag("main_navigation_bar")
            ) {
                NavigationBarItem(
                    selected = currentTab == MainTab.HOME,
                    onClick = { currentTab = MainTab.HOME },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = "Shield HUD"
                        )
                    },
                    label = {
                        Text(
                            text = "Shield",
                            fontWeight = if (currentTab == MainTab.HOME) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 11.sp
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = DarkBackground,
                        selectedTextColor = TigerAmberPrimary,
                        indicatorColor = TigerAmberPrimary,
                        unselectedIconColor = TextSecondary,
                        unselectedTextColor = TextSecondary
                    ),
                    modifier = Modifier.testTag("nav_tab_shield")
                )

                NavigationBarItem(
                    selected = currentTab == MainTab.SERVERS,
                    onClick = { currentTab = MainTab.SERVERS },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Public,
                            contentDescription = "Servers"
                        )
                    },
                    label = {
                        Text(
                            text = "Servers",
                            fontWeight = if (currentTab == MainTab.SERVERS) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 11.sp
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = DarkBackground,
                        selectedTextColor = CyberCyan,
                        indicatorColor = CyberCyan,
                        unselectedIconColor = TextSecondary,
                        unselectedTextColor = TextSecondary
                    ),
                    modifier = Modifier.testTag("nav_tab_servers")
                )

                NavigationBarItem(
                    selected = currentTab == MainTab.PRIVACY,
                    onClick = { currentTab = MainTab.PRIVACY },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Privacy Suite"
                        )
                    },
                    label = {
                        Text(
                            text = "Privacy",
                            fontWeight = if (currentTab == MainTab.PRIVACY) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 11.sp
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = DarkBackground,
                        selectedTextColor = ConnectedGreen,
                        indicatorColor = ConnectedGreen,
                        unselectedIconColor = TextSecondary,
                        unselectedTextColor = TextSecondary
                    ),
                    modifier = Modifier.testTag("nav_tab_privacy")
                )

                NavigationBarItem(
                    selected = currentTab == MainTab.DIAGNOSTICS,
                    onClick = { currentTab = MainTab.DIAGNOSTICS },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Speed,
                            contentDescription = "Speed & Logs"
                        )
                    },
                    label = {
                        Text(
                            text = "Diagnostics",
                            fontWeight = if (currentTab == MainTab.DIAGNOSTICS) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 11.sp
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = DarkBackground,
                        selectedTextColor = TigerAmberPrimary,
                        indicatorColor = TigerAmberPrimary,
                        unselectedIconColor = TextSecondary,
                        unselectedTextColor = TextSecondary
                    ),
                    modifier = Modifier.testTag("nav_tab_diagnostics")
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (currentTab) {
                MainTab.HOME -> {
                    HomeScreen(
                        vpnState = vpnState,
                        selectedServer = selectedServer,
                        statistics = statistics,
                        settings = settings,
                        onToggleVpn = { handleToggleVpn() },
                        onNavigateServers = { currentTab = MainTab.SERVERS },
                        onNavigatePrivacy = { currentTab = MainTab.PRIVACY }
                    )
                }
                MainTab.SERVERS -> {
                    ServersScreen(
                        servers = servers,
                        selectedServer = selectedServer,
                        onSelectServer = { handleSelectServer(it) },
                        onRefreshPings = { VpnController.refreshPings() }
                    )
                }
                MainTab.PRIVACY -> {
                    PrivacyScreen(
                        settings = settings,
                        onUpdateSettings = { VpnController.updateSettings(it) }
                    )
                }
                MainTab.DIAGNOSTICS -> {
                    DiagnosticsScreen(
                        selectedServer = selectedServer,
                        statistics = statistics,
                        logs = logs,
                        onClearLogs = { VpnController.clearLogs() }
                    )
                }
            }
        }
    }
}
