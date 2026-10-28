package com.example.vpn.data

import com.example.vpn.model.ServerCategory
import com.example.vpn.model.VpnProtocol
import com.example.vpn.model.VpnServer
import kotlin.random.Random

object ServerRepository {

    val defaultServers = listOf(
        VpnServer(
            id = "de_fra_1",
            country = "Germany",
            countryCode = "DE",
            city = "Frankfurt",
            flagEmoji = "🇩🇪",
            ip = "185.220.101.45",
            pingMs = 22,
            load = 28,
            categories = listOf(ServerCategory.ALL, ServerCategory.RECOMMENDED, ServerCategory.PRIVACY, ServerCategory.P2P),
            detail = "10 Gbps Tier-1 Frankfurt Backbone (Zero Logs)",
            isRecommended = true
        ),
        VpnServer(
            id = "ch_zrh_1",
            country = "Switzerland",
            countryCode = "CH",
            city = "Zurich",
            flagEmoji = "🇨🇭",
            ip = "194.126.177.12",
            pingMs = 19,
            load = 32,
            categories = listOf(ServerCategory.ALL, ServerCategory.RECOMMENDED, ServerCategory.PRIVACY),
            detail = "Swiss Privacy Fortress (Non-14-Eyes Jurisdiction)",
            isRecommended = true
        ),
        VpnServer(
            id = "us_nyc_1",
            country = "United States",
            countryCode = "US",
            city = "New York",
            flagEmoji = "🇺🇸",
            ip = "198.54.135.21",
            pingMs = 34,
            load = 45,
            categories = listOf(ServerCategory.ALL, ServerCategory.RECOMMENDED, ServerCategory.STREAMING, ServerCategory.P2P),
            detail = "East Coast Ultra Low Latency Hub",
            isRecommended = true
        ),
        VpnServer(
            id = "gb_lon_1",
            country = "United Kingdom",
            countryCode = "GB",
            city = "London",
            flagEmoji = "🇬🇧",
            ip = "185.156.174.99",
            pingMs = 28,
            load = 39,
            categories = listOf(ServerCategory.ALL, ServerCategory.STREAMING, ServerCategory.P2P),
            detail = "London Docklands Optical Ring (BBC iPlayer 4K Ready)"
        ),
        VpnServer(
            id = "nl_ams_1",
            country = "Netherlands",
            countryCode = "NL",
            city = "Amsterdam",
            flagEmoji = "🇳🇱",
            ip = "195.123.210.84",
            pingMs = 24,
            load = 35,
            categories = listOf(ServerCategory.ALL, ServerCategory.PRIVACY, ServerCategory.P2P),
            detail = "AMS-IX Direct Peer (Unlimited P2P BitTorrent)"
        ),
        VpnServer(
            id = "sg_sin_1",
            country = "Singapore",
            countryCode = "SG",
            city = "Singapore",
            flagEmoji = "🇸🇬",
            ip = "103.253.144.11",
            pingMs = 38,
            load = 42,
            categories = listOf(ServerCategory.ALL, ServerCategory.RECOMMENDED, ServerCategory.GAMING),
            detail = "Asia-Pacific Subsea Cable Gateway"
        ),
        VpnServer(
            id = "jp_tyo_1",
            country = "Japan",
            countryCode = "JP",
            city = "Tokyo",
            flagEmoji = "🇯🇵",
            ip = "133.130.122.9",
            pingMs = 52,
            load = 48,
            categories = listOf(ServerCategory.ALL, ServerCategory.GAMING, ServerCategory.STREAMING),
            detail = "Tokyo Equinix Datacenter (Optimized for Low Jitter)"
        ),
        VpnServer(
            id = "us_lax_1",
            country = "United States",
            countryCode = "US",
            city = "Los Angeles",
            flagEmoji = "🇺🇸",
            ip = "143.244.52.88",
            pingMs = 48,
            load = 51,
            categories = listOf(ServerCategory.ALL, ServerCategory.STREAMING, ServerCategory.GAMING),
            detail = "West Coast 4K UltraHD Streaming Cluster"
        ),
        VpnServer(
            id = "ca_tor_1",
            country = "Canada",
            countryCode = "CA",
            city = "Toronto",
            flagEmoji = "🇨🇦",
            ip = "192.99.148.60",
            pingMs = 41,
            load = 36,
            categories = listOf(ServerCategory.ALL, ServerCategory.PRIVACY, ServerCategory.STREAMING),
            detail = "Toronto High Bandwidth Node"
        ),
        VpnServer(
            id = "ae_dxb_1",
            country = "United Arab Emirates",
            countryCode = "AE",
            city = "Dubai",
            flagEmoji = "🇦🇪",
            ip = "185.191.171.30",
            pingMs = 65,
            load = 60,
            categories = listOf(ServerCategory.ALL, ServerCategory.PRIVACY),
            detail = "Stealth Obfuscated Tunnel (Anti-VoIP Filter Bypass)"
        ),
        VpnServer(
            id = "in_bom_1",
            country = "India",
            countryCode = "IN",
            city = "Mumbai",
            flagEmoji = "🇮🇳",
            ip = "103.111.39.15",
            pingMs = 46,
            load = 44,
            categories = listOf(ServerCategory.ALL, ServerCategory.STREAMING, ServerCategory.GAMING),
            detail = "Direct NIXI Peering & Hotstar Optimized"
        ),
        VpnServer(
            id = "fr_par_1",
            country = "France",
            countryCode = "FR",
            city = "Paris",
            flagEmoji = "🇫🇷",
            ip = "51.15.82.170",
            pingMs = 26,
            load = 30,
            categories = listOf(ServerCategory.ALL, ServerCategory.PRIVACY, ServerCategory.STREAMING),
            detail = "France-IX Low Hop Connectivity"
        ),
        VpnServer(
            id = "kr_sel_1",
            country = "South Korea",
            countryCode = "KR",
            city = "Seoul",
            flagEmoji = "🇰🇷",
            ip = "211.233.78.102",
            pingMs = 58,
            load = 55,
            categories = listOf(ServerCategory.ALL, ServerCategory.GAMING),
            detail = "Gigabit Fiber eSports Route"
        ),
        VpnServer(
            id = "au_syd_1",
            country = "Australia",
            countryCode = "AU",
            city = "Sydney",
            flagEmoji = "🇦🇺",
            ip = "139.99.130.40",
            pingMs = 89,
            load = 38,
            categories = listOf(ServerCategory.ALL, ServerCategory.STREAMING),
            detail = "Australia Sydney Intercontinental Hub"
        ),
        VpnServer(
            id = "br_sao_1",
            country = "Brazil",
            countryCode = "BR",
            city = "São Paulo",
            flagEmoji = "🇧🇷",
            ip = "177.54.148.18",
            pingMs = 114,
            load = 47,
            categories = listOf(ServerCategory.ALL, ServerCategory.STREAMING),
            detail = "Latin America Regional Core"
        )
    )

    fun simulatePingRefresh(servers: List<VpnServer>): List<VpnServer> {
        return servers.map { server ->
            val jitter = Random.nextInt(-4, 5)
            val newPing = (server.pingMs + jitter).coerceIn(15, 250)
            server.copy(pingMs = newPing)
        }
    }
}
