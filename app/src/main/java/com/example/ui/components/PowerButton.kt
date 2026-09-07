package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material3.ripple
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.VpnStatus
import com.example.ui.theme.PureWhite
import com.example.ui.theme.RedPrimary
import com.example.ui.theme.TextMuted

@Composable
fun LargePowerButton(
  status: VpnStatus,
  onToggle: () -> Unit,
  modifier: Modifier = Modifier
) {
  val isConnected = status == VpnStatus.CONNECTED
  val isConnecting = status == VpnStatus.CONNECTING || status == VpnStatus.DISCONNECTING

  val infiniteTransition = rememberInfiniteTransition(label = "power_pulse")

  val pulseScale by infiniteTransition.animateFloat(
    initialValue = 1f,
    targetValue = if (isConnected || isConnecting) 1.15f else 1.05f,
    animationSpec = infiniteRepeatable(
      animation = tween(1400, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "pulse_scale"
  )

  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = modifier
  ) {
    Box(
      contentAlignment = Alignment.Center,
      modifier = Modifier
        .size(200.dp)
        .padding(vertical = 4.dp)
    ) {
      // Geometric Outer ring: w-48 h-48 bg-red-50 rounded-full scale-110
      Box(
        modifier = Modifier
          .size(192.dp)
          .scale(if (isConnected || isConnecting) pulseScale else 1.1f)
          .clip(CircleShape)
          .background(Color(0xFFFEF2F2))
      )

      // Geometric Middle ring: w-40 h-40 bg-red-100 rounded-full scale-105 opacity-50
      Box(
        modifier = Modifier
          .size(160.dp)
          .scale(1.05f)
          .clip(CircleShape)
          .background(Color(0xFFFEE2E2).copy(alpha = if (isConnected) 0.8f else 0.5f))
      )

      // Geometric Core button: w-32 h-32 bg-[#D32F2F] rounded-full shadow-xl shadow-red-200 border-4 border-white
      Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
          .size(128.dp)
          .shadow(
            elevation = 16.dp,
            shape = CircleShape,
            spotColor = Color(0xFFFECACA),
            ambientColor = Color(0xFFFEE2E2)
          )
          .clip(CircleShape)
          .background(RedPrimary)
          .border(4.dp, PureWhite, CircleShape)
          .clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = ripple(color = PureWhite),
            onClick = onToggle
          )
          .testTag("vpn_power_button")
      ) {
        if (isConnecting) {
          CircularProgressIndicator(
            modifier = Modifier.size(48.dp),
            color = PureWhite,
            strokeWidth = 4.dp
          )
        } else {
          Icon(
            imageVector = Icons.Default.PowerSettingsNew,
            contentDescription = if (isConnected) "Disconnect VPN" else "Connect VPN",
            tint = PureWhite,
            modifier = Modifier.size(54.dp)
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(8.dp))

    // Interactive helper label
    Text(
      text = when (status) {
        VpnStatus.CONNECTED -> "CONNECTED • TAP TO STOP"
        VpnStatus.CONNECTING -> "ESTABLISHING TUNNEL..."
        VpnStatus.DISCONNECTING -> "DISCONNECTING..."
        VpnStatus.DISCONNECTED -> "TAP TO CONNECT"
      },
      fontSize = 12.sp,
      fontWeight = FontWeight.Bold,
      letterSpacing = 1.2.sp,
      color = if (isConnected) RedPrimary else TextMuted
    )
  }
}
