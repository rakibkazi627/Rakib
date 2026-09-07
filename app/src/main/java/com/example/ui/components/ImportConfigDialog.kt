package com.example.ui.components

import android.content.ClipboardManager
import android.content.Context
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.CardBorder
import com.example.ui.theme.PureWhite
import com.example.ui.theme.RedBorder
import com.example.ui.theme.RedContainer
import com.example.ui.theme.RedDark
import com.example.ui.theme.RedPrimary
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceLight
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSecondary

@Composable
fun ImportConfigDialog(
  onDismiss: () -> Unit,
  onImport: (String) -> Boolean
) {
  var selectedTab by remember { mutableIntStateOf(0) }
  var configText by remember { mutableStateOf("") }
  var errorMessage by remember { mutableStateOf<String?>(null) }
  val context = LocalContext.current

  Dialog(onDismissRequest = onDismiss) {
    Card(
      shape = RoundedCornerShape(24.dp),
      colors = CardDefaults.cardColors(containerColor = PureWhite),
      border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
      elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
      modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 16.dp)
        .testTag("import_dialog")
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp)
      ) {
        // Dialog Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(RedContainer),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Link,
                contentDescription = null,
                tint = RedPrimary,
                modifier = Modifier.size(20.dp)
              )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "Import V2Ray / Xray",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
              )
              Text(
                text = "VMess, VLESS, Trojan, SS",
                fontSize = 11.sp,
                color = TextMuted
              )
            }
          }

          IconButton(onClick = onDismiss) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Close",
              tint = TextMuted
            )
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Tabs: URL / Text vs QR Code
        TabRow(
          selectedTabIndex = selectedTab,
          containerColor = SurfaceLight,
          indicator = { tabPositions ->
            TabRowDefaults.SecondaryIndicator(
              modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
              color = RedPrimary
            )
          },
          modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
        ) {
          Tab(
            selected = selectedTab == 0,
            onClick = {
              selectedTab = 0
              errorMessage = null
            },
            text = {
              Text(
                text = "From URL / Link",
                fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Medium,
                color = if (selectedTab == 0) RedPrimary else TextSecondary,
                fontSize = 13.sp
              )
            },
            icon = {
              Icon(
                imageVector = Icons.Default.Link,
                contentDescription = null,
                tint = if (selectedTab == 0) RedPrimary else TextMuted,
                modifier = Modifier.size(18.dp)
              )
            }
          )

          Tab(
            selected = selectedTab == 1,
            onClick = {
              selectedTab = 1
              errorMessage = null
            },
            text = {
              Text(
                text = "Scan QR Code",
                fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Medium,
                color = if (selectedTab == 1) RedPrimary else TextSecondary,
                fontSize = 13.sp
              )
            },
            icon = {
              Icon(
                imageVector = Icons.Default.QrCodeScanner,
                contentDescription = null,
                tint = if (selectedTab == 1) RedPrimary else TextMuted,
                modifier = Modifier.size(18.dp)
              )
            }
          )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (selectedTab == 0) {
          // Tab 0: Input link or URL
          OutlinedTextField(
            value = configText,
            onValueChange = {
              configText = it
              errorMessage = null
            },
            placeholder = {
              Text(
                text = "Paste vmess://, vless://, trojan://, or subscription URL...",
                fontSize = 12.sp,
                color = TextMuted
              )
            },
            modifier = Modifier
              .fillMaxWidth()
              .height(110.dp)
              .testTag("import_config_input"),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = RedPrimary,
              unfocusedBorderColor = CardBorder,
              focusedContainerColor = SurfaceLight,
              unfocusedContainerColor = SurfaceLight,
              cursorColor = RedPrimary
            ),
            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 12.sp, color = TextDark)
          )

          Spacer(modifier = Modifier.height(8.dp))

          // Paste from Clipboard Button & Demo configs
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .clickable {
                  val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                  val clip = clipboard?.primaryClip?.getItemAt(0)?.text?.toString()
                  if (!clip.isNullOrBlank()) {
                    configText = clip
                  }
                }
                .padding(4.dp)
            ) {
              Icon(
                imageVector = Icons.Default.ContentPaste,
                contentDescription = "Paste",
                tint = RedPrimary,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "Paste Clipboard",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = RedPrimary
              )
            }

            // Quick Preset Demo Button
            Text(
              text = "Load Sample Config",
              fontSize = 12.sp,
              fontWeight = FontWeight.Medium,
              color = TextSecondary,
              modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .clickable {
                  configText = "vless://439f0423-5e92-4217-a068-d0590895c19f@us-ultra.rakibking.net:443?encryption=none&security=reality&type=tcp#US%20Ultra%20Fast%20%F0%9F%87%BA%F0%9F%87%B8"
                }
                .padding(4.dp)
            )
          }
        } else {
          // Tab 1: QR Code Scanner Viewfinder
          QrCodeScannerSimulationView(
            onQrScanned = { scanned ->
              configText = scanned
              val success = onImport(scanned)
              if (success) {
                onDismiss()
              } else {
                errorMessage = "Invalid V2Ray/Xray QR code"
              }
            }
          )
        }

        // Error banner if any
        if (errorMessage != null) {
          Spacer(modifier = Modifier.height(10.dp))
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(10.dp))
              .background(RedContainer)
              .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.Warning,
              contentDescription = null,
              tint = RedDark,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = errorMessage ?: "",
              fontSize = 12.sp,
              color = RedDark,
              fontWeight = FontWeight.Medium
            )
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Action Buttons
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Button(
            onClick = onDismiss,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
              containerColor = SurfaceLight,
              contentColor = TextSecondary
            )
          ) {
            Text("Cancel", fontWeight = FontWeight.SemiBold)
          }

          Button(
            onClick = {
              if (configText.isBlank()) {
                errorMessage = "Please enter a valid V2Ray or Xray link"
                return@Button
              }
              val success = onImport(configText)
              if (success) {
                onDismiss()
              } else {
                errorMessage = "Unsupported or invalid config format"
              }
            },
            modifier = Modifier
              .weight(1f)
              .testTag("submit_import_button"),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
              containerColor = RedPrimary,
              contentColor = PureWhite
            )
          ) {
            Text("Import Server", fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}

@Composable
fun QrCodeScannerSimulationView(
  onQrScanned: (String) -> Unit
) {
  val infiniteTransition = rememberInfiniteTransition(label = "scanner_line")
  val scanLineOffset by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 110f,
    animationSpec = infiniteRepeatable(
      animation = tween(1600, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "scan_line"
  )

  Column(
    modifier = Modifier.fillMaxWidth(),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Box(
      modifier = Modifier
        .size(170.dp)
        .clip(RoundedCornerShape(16.dp))
        .background(Color(0xFF1E1E24))
        .border(2.dp, RedBorder, RoundedCornerShape(16.dp)),
      contentAlignment = Alignment.Center
    ) {
      Icon(
        imageVector = Icons.Default.QrCodeScanner,
        contentDescription = "Scanner Target",
        tint = PureWhite.copy(alpha = 0.35f),
        modifier = Modifier.size(90.dp)
      )

      // Laser red scan line
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(2.dp)
          .offset(y = (scanLineOffset - 55).dp)
          .background(RedPrimary)
      )
    }

    Spacer(modifier = Modifier.height(10.dp))

    Text(
      text = "Point camera at V2Ray QR code or tap below to simulate scan",
      fontSize = 11.sp,
      color = TextSecondary,
      textAlign = TextAlign.Center
    )

    Spacer(modifier = Modifier.height(8.dp))

    Button(
      onClick = {
        // Simulate reading standard Japan Fast VMess QR code
        val sampleQr = "vless://98765432-abcd-ef01-2345-6789abcdef01@jp-tokyo.rakibking.net:443?security=reality&type=tcp#Japan%20Tokyo%20VIP%20%F0%9F%87%AF%F0%9F%87%B5"
        onQrScanned(sampleQr)
      },
      shape = RoundedCornerShape(10.dp),
      colors = ButtonDefaults.buttonColors(containerColor = RedContainer, contentColor = RedDark)
    ) {
      Text("Scan Sample Japan QR", fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
  }
}
