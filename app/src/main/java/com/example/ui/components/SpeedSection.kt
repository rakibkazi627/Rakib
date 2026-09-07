package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.TrafficStats
import com.example.model.VpnStatus
import com.example.ui.theme.CardBorder
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.PureWhite
import com.example.ui.theme.RedBorder
import com.example.ui.theme.RedContainer
import com.example.ui.theme.RedDark
import com.example.ui.theme.RedPrimary
import com.example.ui.theme.SlateBorder100
import com.example.ui.theme.SlateBorder200
import com.example.ui.theme.SlateSurface50
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSecondary

@Composable
fun ConnectionTimeCard(
  formattedTime: String,
  status: VpnStatus,
  modifier: Modifier = Modifier
) {
  // Geometric Balance: Centered, spacious, clean mono timer with uppercase subtext
  Column(
    modifier = modifier
      .fillMaxWidth()
      .padding(vertical = 4.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Text(
      text = formattedTime,
      fontSize = 44.sp,
      fontWeight = FontWeight.Medium,
      fontFamily = FontFamily.Monospace,
      letterSpacing = 4.sp,
      color = if (status == VpnStatus.CONNECTED) RedPrimary else TextMuted
    )
    Spacer(modifier = Modifier.height(4.dp))
    Text(
      text = "CONNECTION TIME",
      fontSize = 11.sp,
      fontWeight = FontWeight.Bold,
      color = TextMuted,
      letterSpacing = 2.sp
    )
  }
}

@Composable
fun SpeedSection(
  stats: TrafficStats,
  isConnected: Boolean,
  modifier: Modifier = Modifier
) {
  // Geometric Balance: 2-column grid inside rounded-3xl slate container with crisp divider
  Card(
    modifier = modifier.fillMaxWidth(),
    shape = RoundedCornerShape(24.dp),
    colors = CardDefaults.cardColors(containerColor = SlateSurface50),
    border = BorderStroke(1.dp, SlateBorder100),
    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 16.dp, horizontal = 12.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Download half
      GeometricSpeedColumn(
        arrow = "↓",
        arrowColor = RedPrimary,
        speedValue = if (isConnected) stats.formattedDownSpeed().replace(" KB/s", "").replace(" MB/s", "") else "0.0",
        unit = if (isConnected && stats.downloadSpeedKb >= 1024f) "MB/s" else "KB/s",
        label = "DOWNLOAD",
        modifier = Modifier.weight(1f)
      )

      // Divider
      Box(
        modifier = Modifier
          .width(1.dp)
          .height(48.dp)
          .background(SlateBorder200)
      )

      // Upload half
      GeometricSpeedColumn(
        arrow = "↑",
        arrowColor = EmeraldGreen,
        speedValue = if (isConnected) stats.formattedUpSpeed().replace(" KB/s", "").replace(" MB/s", "") else "0.0",
        unit = if (isConnected && stats.uploadSpeedKb >= 1024f) "MB/s" else "KB/s",
        label = "UPLOAD",
        modifier = Modifier.weight(1f)
      )
    }
  }
}

@Composable
private fun GeometricSpeedColumn(
  arrow: String,
  arrowColor: Color,
  speedValue: String,
  unit: String,
  label: String,
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier,
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.Center
    ) {
      Text(
        text = arrow,
        color = arrowColor,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp
      )
      Spacer(modifier = Modifier.width(6.dp))
      Text(
        text = speedValue,
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold,
        color = TextDark
      )
      Spacer(modifier = Modifier.width(4.dp))
      Text(
        text = unit,
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium,
        color = TextSecondary
      )
    }
    Spacer(modifier = Modifier.height(4.dp))
    Text(
      text = label,
      fontSize = 10.sp,
      fontWeight = FontWeight.Bold,
      color = TextMuted,
      letterSpacing = 1.5.sp
    )
  }
}

@Composable
private fun SpeedCard(
  title: String,
  speed: String,
  total: String,
  icon: ImageVector,
  accentColor: Color,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier,
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
    border = BorderStroke(1.dp, CardBorder),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp)
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
      ) {
        Text(
          text = title,
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          color = TextMuted,
          letterSpacing = 0.8.sp
        )
        Box(
          modifier = Modifier
            .size(28.dp)
            .clip(CircleShape)
            .background(RedContainer),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = icon,
            contentDescription = title,
            tint = accentColor,
            modifier = Modifier.size(16.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = speed,
        fontSize = 18.sp,
        fontWeight = FontWeight.ExtraBold,
        color = TextDark
      )

      Spacer(modifier = Modifier.height(2.dp))

      Text(
        text = "Total: $total",
        fontSize = 11.sp,
        color = TextSecondary,
        fontWeight = FontWeight.Medium
      )
    }
  }
}
