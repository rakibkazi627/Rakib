package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ripple
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.VpnServer
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.PureWhite
import com.example.ui.theme.RedPrimary
import com.example.ui.theme.SlateBorder100
import com.example.ui.theme.SlateBorder200
import com.example.ui.theme.SlateSurface50
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSecondary

@Composable
fun ServerSelectionSection(
  servers: List<VpnServer>,
  selectedServer: VpnServer,
  isTestingPing: Boolean,
  onSelectServer: (VpnServer) -> Unit,
  onRefreshPings: () -> Unit,
  modifier: Modifier = Modifier
) {
  Column(modifier = modifier.fillMaxWidth()) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "AVAILABLE SERVERS",
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = TextMuted,
        letterSpacing = 1.5.sp
      )

      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
          .clip(RoundedCornerShape(12.dp))
          .clickable(onClick = onRefreshPings)
          .padding(horizontal = 8.dp, vertical = 4.dp)
      ) {
        if (isTestingPing) {
          CircularProgressIndicator(
            modifier = Modifier.size(14.dp),
            strokeWidth = 2.dp,
            color = RedPrimary
          )
        } else {
          Icon(
            imageVector = Icons.Default.Refresh,
            contentDescription = "Refresh Ping",
            tint = RedPrimary,
            modifier = Modifier.size(15.dp)
          )
        }
        Spacer(modifier = Modifier.width(4.dp))
        Text(
          text = if (isTestingPing) "Testing..." else "Test Ping",
          fontSize = 12.sp,
          fontWeight = FontWeight.SemiBold,
          color = RedPrimary
        )
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Display server cards styled with Geometric Balance aesthetic
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
      servers.take(2).forEach { server ->
        val isSelected = server.id == selectedServer.id
        ServerCardItem(
          server = server,
          isSelected = isSelected,
          onSelect = { onSelectServer(server) }
        )
      }
    }
  }
}

@Composable
fun ServerCardItem(
  server: VpnServer,
  isSelected: Boolean,
  onSelect: () -> Unit,
  modifier: Modifier = Modifier
) {
  val borderColor = if (isSelected) SlateBorder200 else SlateBorder100

  // Geometric Balance: p-3 bg-white border border-slate-200 rounded-2xl
  Card(
    modifier = modifier
      .fillMaxWidth()
      .clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = ripple(color = RedPrimary),
        onClick = onSelect
      )
      .testTag("server_card_${server.id}"),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = PureWhite),
    border = BorderStroke(1.dp, borderColor),
    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 14.dp, vertical = 12.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.weight(1f)
      ) {
        // Flag icon container: w-10 h-10 bg-slate-100 rounded-xl
        Box(
          modifier = Modifier
            .size(40.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(SlateSurface50),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = server.flag,
            fontSize = 20.sp
          )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = server.name,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = TextDark,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )

          Spacer(modifier = Modifier.height(2.dp))

          Text(
            text = "${server.protocol} • ${server.country}",
            fontSize = 10.sp,
            color = TextMuted,
            fontWeight = FontWeight.Medium
          )
        }
      }

      Spacer(modifier = Modifier.width(8.dp))

      // Ping & Radio selector
      Row(verticalAlignment = Alignment.CenterVertically) {
        // Ping: font-mono font-bold
        Text(
          text = if (server.pingMs > 0) "${server.pingMs}ms" else "-- ms",
          fontSize = 12.sp,
          fontFamily = FontFamily.Monospace,
          fontWeight = FontWeight.Bold,
          color = if (server.pingMs in 1..99) EmeraldGreen else TextMuted
        )

        Spacer(modifier = Modifier.width(10.dp))

        // Geometric circle checkbox / radio
        Box(
          modifier = Modifier
            .size(18.dp)
            .clip(CircleShape)
            .background(if (isSelected) RedPrimary else Color.Transparent)
            .border(
              width = if (isSelected) 2.dp else 2.dp,
              color = if (isSelected) RedPrimary else SlateBorder200,
              shape = CircleShape
            ),
          contentAlignment = Alignment.Center
        ) {
          if (isSelected) {
            Box(
              modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(PureWhite)
            )
          }
        }
      }
    }
  }
}
