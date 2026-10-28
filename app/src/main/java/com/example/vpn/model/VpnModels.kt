package com.example.vpn.model

enum class VpnState {
    DISCONNECTED,
    CONNECTING,
    CONNECTED,
    DISCONNECTING
}

enum class VpnProtocol(val displayName: String, val shortDesc: String, val tag: String) {
    WIREGUARD(
        displayName = "WireGuard v2",
        shortDesc = "Fastest throughput & instant handshake",
        tag = "Ultra Fast"
    ),
    OPENVPN_UDP(
        displayName = "OpenVPN (UDP)",
        shortDesc = "Industry gold-standard encryption & low latency",
        tag = "Balanced"
    ),
    OPENVPN_TCP(
        displayName = "OpenVPN (TCP)",
        shortDesc = "Reliable packet delivery on unstable networks",
        tag = "Stable"
    ),
    SHADOWSOCKS(
        displayName = "Shadowsocks Stealth",
        shortDesc = "Anti-censorship obfuscation against DPI firewalls",
        tag = "Stealth"
    )
}

enum class ServerCategory(val label: String, val icon: String) {
    ALL("All Servers", "🌐"),
    RECOMMENDED("Fastest", "⚡"),
    STREAMING("Streaming", "🎬"),
    GAMING("Gaming", "🎮"),
    PRIVACY("High Privacy", "🛡️"),
    P2P("P2P / Torrent", "📁")
}

data class VpnServer(
    val id: String,
    val country: String,
    val countryCode: String,
    val city: String,
    val flagEmoji: String,
    val ip: String,
    var pingMs: Int,
    val load: Int,
    val supportedProtocols: List<VpnProtocol> = listOf(
        VpnProtocol.WIREGUARD,
        VpnProtocol.OPENVPN_UDP,
        VpnProtocol.OPENVPN_TCP,
        VpnProtocol.SHADOWSOCKS
    ),
    val categories: List<ServerCategory>,
    val detail: String,
    val isRecommended: Boolean = false
)

enum class DnsProvider(
    val displayName: String,
    val primaryIp: String,
    val secondaryIp: String,
    val providerDesc: String
) {
    CLOUDFLARE(
        displayName = "Cloudflare Warp DNS",
        primaryIp = "1.1.1.1",
        secondaryIp = "1.0.0.1",
        providerDesc = "Fastest global DNS resolver with zero-logging guarantee"
    ),
    QUAD9(
        displayName = "Quad9 Security",
        primaryIp = "9.9.9.9",
        secondaryIp = "149.112.112.112",
        providerDesc = "Swiss-based non-profit privacy DNS with malware blocking"
    ),
    GOOGLE(
        displayName = "Google Public DNS",
        primaryIp = "8.8.8.8",
        secondaryIp = "8.8.4.4",
        providerDesc = "Ultra-high availability and geo-distributed cache"
    ),
    ADGUARD(
        displayName = "AdGuard Clean DNS",
        primaryIp = "94.140.14.14",
        secondaryIp = "94.140.15.15",
        providerDesc = "Filters intrusive ads, trackers, and phishing domains"
    )
}

data class VpnSettings(
    val killSwitchEnabled: Boolean = true,
    val dnsLeakProtection: Boolean = true,
    val dnsProvider: DnsProvider = DnsProvider.CLOUDFLARE,
    val selectedProtocol: VpnProtocol = VpnProtocol.WIREGUARD,
    val splitTunnelingEnabled: Boolean = false,
    val bypassedPackages: Set<String> = emptySet(),
    val mtu: Int = 1420,
    val stealthObfuscation: Boolean = false,
    val autoReconnect: Boolean = true
)

data class VpnStatistics(
    val durationSeconds: Long = 0L,
    val bytesIn: Long = 0L,
    val bytesOut: Long = 0L,
    val downloadSpeedBps: Long = 0L,
    val uploadSpeedBps: Long = 0L,
    val realIp: String = "192.168.1.104",
    val realLocation: String = "Local Network (Exposed)",
    val virtualIp: String = "",
    val cipherSuite: String = "ChaCha20-Poly1305 / 256-bit",
    val handshakeLatencyMs: Int = 0
)

enum class LogLevel { INFO, SUCCESS, WARNING, SHIELD }

data class VpnLogItem(
    val timeFormatted: String,
    val message: String,
    val level: LogLevel
)

data class AppPackageInfo(
    val packageName: String,
    val appName: String,
    val isBypassed: Boolean = false
)
