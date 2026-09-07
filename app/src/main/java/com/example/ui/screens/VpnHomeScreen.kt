package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SignalWifi4Bar
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.VpnStatus
import com.example.ui.components.ConnectionTimeCard
import com.example.ui.components.ImportConfigDialog
import com.example.ui.components.LargePowerButton
import com.example.ui.components.ServerSelectionSection
import com.example.ui.components.SpeedSection
import com.example.ui.theme.CardBorder
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.PureWhite
import com.example.ui.theme.RedBorder
import com.example.ui.theme.RedContainer
import com.example.ui.theme.RedDark
import com.example.ui.theme.RedLight
import com.example.ui.theme.RedPrimary
import com.example.ui.theme.SlateBorder100
import com.example.ui.theme.SlateBorder200
import com.example.ui.theme.SlateSurface50
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.VpnViewModel

@Composable
fun VpnHomeScreen(
  viewModel: VpnViewModel,
  modifier: Modifier = Modifier
) {
  val vpnStatus by viewModel.vpnStatus.collectAsState()
  val servers by viewModel.servers.collectAsState()
  val selectedServer by viewModel.selectedServer.collectAsState()
  val trafficStats by viewModel.trafficStats.collectAsState()
  val accountExpiry by viewModel.accountExpiry.collectAsState()
  val isTestingPing by viewModel.isTestingPing.collectAsState()
  val importMessage by viewModel.importMessage.collectAsState()

  var showImportDialog by remember { mutableStateOf(false) }
  val context = LocalContext.current
  val insets = WindowInsets.safeDrawing.asPaddingValues()

  LaunchedEffect(importMessage) {
    importMessage?.let { msg ->
      Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
      viewModel.clearImportMessage()
    }
  }

  Scaffold(
    contentWindowInsets = WindowInsets(0.dp),
    containerColor = PureWhite,
    modifier = modifier.fillMaxSize()
  ) { padding ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .background(PureWhite)
        .padding(padding),
      contentPadding = PaddingValues(
        top = insets.calculateTopPadding() + 12.dp,
        bottom = insets.calculateBottomPadding() + 24.dp,
        start = 20.dp,
        end = 20.dp
      ),
      verticalArrangement = Arrangement.spacedBy(18.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      // 1. Top Header with Title & Subtitle
      item {
        TopHeaderSection(
          onOpenImport = { showImportDialog = true }
        )
      }

      // 2. Connection Time Display (00:00:00)
      item {
        ConnectionTimeCard(
          formattedTime = viewModel.formattedConnectionTime(),
          status = vpnStatus,
          modifier = Modifier.fillMaxWidth()
        )
      }

      // 3. Large Red Power Button in the Center
      item {
        LargePowerButton(
          status = vpnStatus,
          onToggle = { viewModel.toggleConnection() },
          modifier = Modifier.padding(vertical = 6.dp)
        )
      }

      // 4. Download / Upload Speed Section
      item {
        SpeedSection(
          stats = trafficStats,
          isConnected = vpnStatus == VpnStatus.CONNECTED,
          modifier = Modifier.fillMaxWidth()
        )
      }

      // 5. Two Server Cards with Ping and Country Flag
      item {
        ServerSelectionSection(
          servers = servers,
          selectedServer = selectedServer,
          isTestingPing = isTestingPing,
          onSelectServer = { server ->
            viewModel.selectServer(server)
          },
          onRefreshPings = { viewModel.testAllPings() },
          modifier = Modifier.fillMaxWidth()
        )
      }

      // 6. Bottom Status: Disconnected / Connected Card
      item {
        BottomStatusCard(
          status = vpnStatus,
          serverHost = if (vpnStatus == VpnStatus.CONNECTED) selectedServer.host else "Offline",
          protocol = selectedServer.protocol,
          modifier = Modifier.fillMaxWidth()
        )
      }

      // 7. Account Expiry Text
      item {
        AccountExpiryCard(
          expiryText = accountExpiry,
          modifier = Modifier.fillMaxWidth()
        )
      }
    }
  }

  if (showImportDialog) {
    ImportConfigDialog(
      onDismiss = { showImportDialog = false },
      onImport = { config ->
        viewModel.importConfig(config)
      }
    )
  }
}

@Composable
private fun TopHeaderSection(
  onOpenImport: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 4.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.Top
  ) {
    Column {
      // Geometric Balance header: text-2xl font-bold tracking-tight text-[#D32F2F]
      Text(
        text = "RAKIB KING-V2VPN",
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold,
        color = RedPrimary,
        letterSpacing = (-0.5).sp,
        lineHeight = 26.sp
      )

      Spacer(modifier = Modifier.height(2.dp))

      Text(
        text = "Lightning Fast Internet",
        fontSize = 13.sp,
        fontWeight = FontWeight.Medium,
        color = TextSecondary
      )
    }

    // Two geometric buttons: QR and + (import) styled with p-3 bg-slate-50 rounded-2xl border border-slate-100
    Row(
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      // QR Button
      Box(
        modifier = Modifier
          .size(44.dp)
          .clip(RoundedCornerShape(16.dp))
          .background(SlateSurface50)
          .border(1.dp, SlateBorder100, RoundedCornerShape(16.dp))
          .clickable(onClick = onOpenImport)
          .testTag("qr_import_button"),
        contentAlignment = Alignment.Center
      ) {
        Box(
          modifier = Modifier
            .size(20.dp)
            .border(2.dp, TextSecondary, RoundedCornerShape(3.dp)),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "QR",
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = TextSecondary
          )
        }
      }

      // Add Button
      Box(
        modifier = Modifier
          .size(44.dp)
          .clip(RoundedCornerShape(16.dp))
          .background(SlateSurface50)
          .border(1.dp, SlateBorder100, RoundedCornerShape(16.dp))
          .clickable(onClick = onOpenImport)
          .testTag("open_import_dialog_button"),
        contentAlignment = Alignment.Center
      ) {
        Box(
          modifier = Modifier
            .size(20.dp)
            .border(2.dp, TextSecondary, CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "+",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = TextSecondary
          )
        }
      }
    }
  }
}

@Composable
fun BottomStatusCard(
  status: VpnStatus,
  serverHost: String,
  protocol: String,
  modifier: Modifier = Modifier
) {
  val isConnected = status == VpnStatus.CONNECTED

  // Geometric Balance: footer container with status dot, text, and Change Server trigger
  Card(
    modifier = modifier.testTag("bottom_status_card"),
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(containerColor = SlateSurface50),
    border = BorderStroke(1.dp, SlateBorder100),
    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 14.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        // Dot indicator
        Box(
          modifier = Modifier
            .size(8.dp)
            .clip(CircleShape)
            .background(
              when {
                isConnected -> EmeraldGreen
                status == VpnStatus.CONNECTING -> RedPrimary
                else -> Color(0xFFCBD5E1)
              }
            )
        )

        Spacer(modifier = Modifier.width(10.dp))

        Text(
          text = when (status) {
            VpnStatus.CONNECTED -> "Status: Connected ($protocol)"
            VpnStatus.CONNECTING -> "Status: Connecting..."
            VpnStatus.DISCONNECTING -> "Status: Disconnecting..."
            VpnStatus.DISCONNECTED -> "Status: Disconnected"
          },
          fontSize = 13.sp,
          fontWeight = FontWeight.Bold,
          color = if (isConnected) EmeraldGreen else TextSecondary
        )
      }

      Text(
        text = if (isConnected) "SECURE" else "CHANGE SERVER",
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = RedPrimary,
        letterSpacing = 1.sp
      )
    }
  }
}

@Composable
fun AccountExpiryCard(
  expiryText: String,
  modifier: Modifier = Modifier
) {
  // Geometric Balance: flex justify-between items-center text-[11px] text-slate-400 font-medium bg-white p-3 rounded-xl border border-slate-200
  Card(
    modifier = modifier,
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = PureWhite),
    border = BorderStroke(1.dp, SlateBorder200),
    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 14.dp, vertical = 12.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Text(
        text = "ACCOUNT EXPIRY",
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = TextMuted,
        letterSpacing = 1.sp
      )
      Text(
        text = expiryText.replace("Account Expiry: ", ""),
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = TextDark
      )
    }
  }
}
