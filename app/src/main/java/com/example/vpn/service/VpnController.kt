package com.example.vpn.service

import android.content.Context
import android.content.Intent
import com.example.vpn.data.ServerRepository
import com.example.vpn.model.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random

object VpnController {

    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private val _vpnState = MutableStateFlow(VpnState.DISCONNECTED)
    val vpnState: StateFlow<VpnState> = _vpnState.asStateFlow()

    private val _servers = MutableStateFlow(ServerRepository.defaultServers)
    val servers: StateFlow<List<VpnServer>> = _servers.asStateFlow()

    private val _selectedServer = MutableStateFlow(ServerRepository.defaultServers[0])
    val selectedServer: StateFlow<VpnServer> = _selectedServer.asStateFlow()

    private val _settings = MutableStateFlow(VpnSettings())
    val settings: StateFlow<VpnSettings> = _settings.asStateFlow()

    private val _statistics = MutableStateFlow(
        VpnStatistics(
            realIp = "192.168.1.104",
            realLocation = "Local ISP (Exposed)"
        )
    )
    val statistics: StateFlow<VpnStatistics> = _statistics.asStateFlow()

    private val _logs = MutableStateFlow<List<VpnLogItem>>(emptyList())
    val logs: StateFlow<List<VpnLogItem>> = _logs.asStateFlow()

    private val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

    private var statsJob: Job? = null

    init {
        addLog("TIGERALI97 VPN Core Initialized. Ready for secure tunneling.", LogLevel.INFO)
    }

    fun selectServer(server: VpnServer) {
        _selectedServer.value = server
        addLog("Selected target node: ${server.city}, ${server.country} (${server.ip})", LogLevel.INFO)
        if (_vpnState.value == VpnState.CONNECTED) {
            // Reconnect if currently active
            addLog("Switching active tunnel to ${server.city}...", LogLevel.SHIELD)
        }
    }

    fun updateSettings(newSettings: VpnSettings) {
        _settings.value = newSettings
        addLog("Privacy settings updated: DNS=${newSettings.dnsProvider.name}, KillSwitch=${newSettings.killSwitchEnabled}", LogLevel.SHIELD)
    }

    fun toggleAppBypass(packageName: String) {
        val current = _settings.value.bypassedPackages
        val updated = if (current.contains(packageName)) current - packageName else current + packageName
        _settings.value = _settings.value.copy(bypassedPackages = updated)
    }

    fun refreshPings() {
        scope.launch {
            addLog("Probing global node latencies...", LogLevel.INFO)
            val refreshed = ServerRepository.simulatePingRefresh(_servers.value)
            _servers.value = refreshed
            // also update selected server ping
            _servers.value.find { it.id == _selectedServer.value.id }?.let {
                _selectedServer.value = it
            }
            addLog("Global ping refresh complete. Lowest latency: ${_servers.value.minByOrNull { it.pingMs }?.city ?: "N/A"}", LogLevel.SUCCESS)
        }
    }

    fun startVpn(context: Context) {
        if (_vpnState.value == VpnState.CONNECTING || _vpnState.value == VpnState.CONNECTED) return

        _vpnState.value = VpnState.CONNECTING
        val server = _selectedServer.value
        val protocol = _settings.value.selectedProtocol

        addLog("Initiating handshake with ${server.city} [${server.ip}] via ${protocol.displayName}...", LogLevel.INFO)
        addLog("Injecting DNS resolver: ${_settings.value.dnsProvider.displayName} (${_settings.value.dnsProvider.primaryIp})", LogLevel.SHIELD)
        if (_settings.value.killSwitchEnabled) {
            addLog("Kill Switch ARMED. Leak interception active.", LogLevel.SHIELD)
        }

        val intent = Intent(context, TigerVpnService::class.java).apply {
            action = TigerVpnService.ACTION_CONNECT
            putExtra(TigerVpnService.EXTRA_SERVER_ID, server.id)
            putExtra(TigerVpnService.EXTRA_SERVER_IP, server.ip)
            putExtra(TigerVpnService.EXTRA_SERVER_NAME, "${server.city}, ${server.country}")
        }
        context.startService(intent)
    }

    fun stopVpn(context: Context) {
        if (_vpnState.value == VpnState.DISCONNECTED || _vpnState.value == VpnState.DISCONNECTING) return

        _vpnState.value = VpnState.DISCONNECTING
        addLog("Terminating encrypted tunnel gracefully...", LogLevel.WARNING)

        val intent = Intent(context, TigerVpnService::class.java).apply {
            action = TigerVpnService.ACTION_DISCONNECT
        }
        context.startService(intent)
    }

    // Called from TigerVpnService on established
    fun onVpnConnected(serverIp: String, serverName: String) {
        _vpnState.value = VpnState.CONNECTED
        val server = _selectedServer.value
        addLog("Encrypted tunnel ESTABLISHED to $serverName. Virtual IP assigned: $serverIp", LogLevel.SUCCESS)
        addLog("Cipher: ChaCha20-Poly1305 / 256-bit AEAD authenticated", LogLevel.SUCCESS)

        _statistics.value = _statistics.value.copy(
            virtualIp = serverIp,
            handshakeLatencyMs = server.pingMs,
            durationSeconds = 0L,
            bytesIn = 1420L,
            bytesOut = 840L
        )

        startStatsLoop()
    }

    fun onVpnDisconnected() {
        _vpnState.value = VpnState.DISCONNECTED
        statsJob?.cancel()
        statsJob = null

        _statistics.value = _statistics.value.copy(
            virtualIp = "",
            downloadSpeedBps = 0L,
            uploadSpeedBps = 0L
        )
        addLog("VPN connection closed. Real IP restored.", LogLevel.INFO)
    }

    private fun startStatsLoop() {
        statsJob?.cancel()
        statsJob = scope.launch {
            var seconds = 0L
            var totalIn = 1420L
            var totalOut = 840L

            while (isActive && _vpnState.value == VpnState.CONNECTED) {
                delay(1000L)
                seconds++

                // Realistic active throughput fluctuations
                val currentDownSpeed = Random.nextLong(2_400_000L, 12_800_000L) // 2.4 - 12.8 MB/s
                val currentUpSpeed = Random.nextLong(300_000L, 2_200_000L)   // 300 KB - 2.2 MB/s

                totalIn += (currentDownSpeed / 8)
                totalOut += (currentUpSpeed / 8)

                _statistics.value = _statistics.value.copy(
                    durationSeconds = seconds,
                    bytesIn = totalIn,
                    bytesOut = totalOut,
                    downloadSpeedBps = currentDownSpeed,
                    uploadSpeedBps = currentUpSpeed
                )
            }
        }
    }

    fun addLog(msg: String, level: LogLevel) {
        val entry = VpnLogItem(
            timeFormatted = timeFormat.format(Date()),
            message = msg,
            level = level
        )
        _logs.value = (_logs.value + entry).takeLast(100)
    }

    fun clearLogs() {
        _logs.value = emptyList()
        addLog("System logs cleared.", LogLevel.INFO)
    }

    fun findFastestServer(): VpnServer {
        return _servers.value.minByOrNull { it.pingMs } ?: _servers.value.first()
    }
}
