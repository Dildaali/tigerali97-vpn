package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.vpn.model.VpnState

@Composable
fun PowerButton(
    vpnState: VpnState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isConnected = vpnState == VpnState.CONNECTED
    val isConnecting = vpnState == VpnState.CONNECTING || vpnState == VpnState.DISCONNECTING

    // Infinite pulsing animation for connecting / connected states
    val infiniteTransition = rememberInfiniteTransition(label = "power_pulse")
    val pulseScale1 by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isConnecting || isConnected) 1.25f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = if (isConnecting) 900 else 2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale_1"
    )

    val pulseScale2 by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isConnecting || isConnected) 1.45f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = if (isConnecting) 900 else 2200, delayMillis = 300, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale_2"
    )

    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 0.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = if (isConnecting) 900 else 2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    val mainColor by animateColorAsState(
        targetValue = when (vpnState) {
            VpnState.CONNECTED -> ConnectedGreen
            VpnState.CONNECTING -> TigerAmberPrimary
            VpnState.DISCONNECTING -> TigerAmberSecondary
            VpnState.DISCONNECTED -> CyberCyan
        },
        animationSpec = tween(500),
        label = "main_color"
    )

    val outerGlowColor by animateColorAsState(
        targetValue = when (vpnState) {
            VpnState.CONNECTED -> RadarPulseGreen
            VpnState.CONNECTING, VpnState.DISCONNECTING -> RadarPulseAmber
            VpnState.DISCONNECTED -> RadarPulseCyan
        },
        animationSpec = tween(500),
        label = "outer_glow"
    )

    val interactionSource = remember { MutableInteractionSource() }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(240.dp)
            .testTag("vpn_power_button_container")
    ) {
        // Outer Radar Wave 2
        if (isConnected || isConnecting) {
            Box(
                modifier = Modifier
                    .size(190.dp)
                    .scale(pulseScale2)
                    .clip(CircleShape)
                    .background(outerGlowColor.copy(alpha = pulseAlpha * 0.6f))
            )

            // Outer Radar Wave 1
            Box(
                modifier = Modifier
                    .size(170.dp)
                    .scale(pulseScale1)
                    .clip(CircleShape)
                    .background(outerGlowColor.copy(alpha = pulseAlpha))
            )
        }

        // Static Ambient Ring
        Box(
            modifier = Modifier
                .size(170.dp)
                .clip(CircleShape)
                .background(DarkSurfaceVariant)
                .border(2.dp, mainColor.copy(alpha = 0.4f), CircleShape)
        )

        // Core Touch Button
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(136.dp)
                .shadow(
                    elevation = if (isConnected) 18.dp else 10.dp,
                    shape = CircleShape,
                    spotColor = mainColor,
                    ambientColor = mainColor
                )
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            DarkCardSurface,
                            DarkBackground
                        )
                    )
                )
                .border(
                    width = 3.dp,
                    brush = Brush.sweepGradient(
                        colors = listOf(
                            mainColor,
                            mainColor.copy(alpha = 0.3f),
                            mainColor,
                            mainColor.copy(alpha = 0.6f),
                            mainColor
                        )
                    ),
                    shape = CircleShape
                )
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onClick
                )
                .testTag("vpn_power_button")
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = if (isConnected) Icons.Default.Shield else Icons.Default.PowerSettingsNew,
                    contentDescription = "VPN Power",
                    tint = mainColor,
                    modifier = Modifier.size(52.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = when (vpnState) {
                        VpnState.CONNECTED -> "CONNECTED"
                        VpnState.CONNECTING -> "CONNECTING"
                        VpnState.DISCONNECTING -> "DISCONNECTING"
                        VpnState.DISCONNECTED -> "CONNECT"
                    },
                    color = mainColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}
