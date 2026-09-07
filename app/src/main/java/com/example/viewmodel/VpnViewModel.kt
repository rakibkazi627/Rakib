package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.model.TrafficStats
import com.example.model.V2RayConfigParser
import com.example.model.VpnServer
import com.example.model.VpnStatus
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

class VpnViewModel : ViewModel() {

  // Two primary server cards as explicitly requested
  private val defaultServers = listOf(
    VpnServer(
      id = "server_sg_vip",
      name = "Singapore VIP Ultra",
      country = "Singapore",
      flag = "🇸🇬",
      host = "sg-vip01.rakibking.net",
      port = 443,
      protocol = "VMess / WS+TLS",
      pingMs = 24
    ),
    VpnServer(
      id = "server_de_pro",
      name = "Germany Pro Gaming",
      country = "Germany",
      flag = "🇩🇪",
      host = "de-gaming.rakibking.net",
      port = 443,
      protocol = "VLESS / Reality",
      pingMs = 48
    )
  )

  private val _servers = MutableStateFlow<List<VpnServer>>(defaultServers)
  val servers: StateFlow<List<VpnServer>> = _servers.asStateFlow()

  private val _selectedServer = MutableStateFlow<VpnServer>(defaultServers[0])
  val selectedServer: StateFlow<VpnServer> = _selectedServer.asStateFlow()

  private val _vpnStatus = MutableStateFlow<VpnStatus>(VpnStatus.DISCONNECTED)
  val vpnStatus: StateFlow<VpnStatus> = _vpnStatus.asStateFlow()

  private val _durationSeconds = MutableStateFlow<Long>(0L)
  val durationSeconds: StateFlow<Long> = _durationSeconds.asStateFlow()

  private val _trafficStats = MutableStateFlow(TrafficStats())
  val trafficStats: StateFlow<TrafficStats> = _trafficStats.asStateFlow()

  private val _accountExpiry = MutableStateFlow("Account Expiry: 2026-12-31 (VIP Unlimited)")
  val accountExpiry: StateFlow<String> = _accountExpiry.asStateFlow()

  private val _isTestingPing = MutableStateFlow(false)
  val isTestingPing: StateFlow<Boolean> = _isTestingPing.asStateFlow()

  private val _importMessage = MutableStateFlow<String?>(null)
  val importMessage: StateFlow<String?> = _importMessage.asStateFlow()

  private var timerJob: Job? = null
  private var trafficJob: Job? = null

  fun selectServer(server: VpnServer) {
    _selectedServer.value = server
  }

  fun toggleConnection() {
    when (_vpnStatus.value) {
      VpnStatus.DISCONNECTED -> connect()
      VpnStatus.CONNECTED -> disconnect()
      VpnStatus.CONNECTING -> disconnect()
      VpnStatus.DISCONNECTING -> {}
    }
  }

  private fun connect() {
    viewModelScope.launch {
      _vpnStatus.value = VpnStatus.CONNECTING
      // Realistic connection handshake delay (1.2s)
      delay(1200)
      _vpnStatus.value = VpnStatus.CONNECTED
      startTimer()
      startTrafficSimulation()
    }
  }

  private fun disconnect() {
    viewModelScope.launch {
      _vpnStatus.value = VpnStatus.DISCONNECTING
      delay(600)
      _vpnStatus.value = VpnStatus.DISCONNECTED
      stopTimer()
      stopTrafficSimulation()
    }
  }

  private fun startTimer() {
    timerJob?.cancel()
    timerJob = viewModelScope.launch {
      while (_vpnStatus.value == VpnStatus.CONNECTED) {
        delay(1000)
        _durationSeconds.value += 1
      }
    }
  }

  private fun stopTimer() {
    timerJob?.cancel()
    timerJob = null
    _durationSeconds.value = 0
  }

  private fun startTrafficSimulation() {
    trafficJob?.cancel()
    trafficJob = viewModelScope.launch {
      var downTotal = _trafficStats.value.totalDownloadMb
      var upTotal = _trafficStats.value.totalUploadMb

      while (_vpnStatus.value == VpnStatus.CONNECTED) {
        // High speed realistic download/upload variations (40-95 MB/s down, 10-35 MB/s up)
        val downKb = Random.nextDouble(38000.0, 92000.0).toFloat()
        val upKb = Random.nextDouble(8000.0, 28000.0).toFloat()

        downTotal += (downKb / 1024f) / 1024f * 0.8f
        upTotal += (upKb / 1024f) / 1024f * 0.8f

        _trafficStats.value = TrafficStats(
          downloadSpeedKb = downKb,
          uploadSpeedKb = upKb,
          totalDownloadMb = downTotal,
          totalUploadMb = upTotal
        )
        delay(800)
      }
    }
  }

  private fun stopTrafficSimulation() {
    trafficJob?.cancel()
    trafficJob = null
    _trafficStats.value = TrafficStats(
      downloadSpeedKb = 0f,
      uploadSpeedKb = 0f,
      totalDownloadMb = _trafficStats.value.totalDownloadMb,
      totalUploadMb = _trafficStats.value.totalUploadMb
    )
  }

  fun testAllPings() {
    viewModelScope.launch {
      _isTestingPing.value = true
      delay(700)
      _servers.value = _servers.value.map { server ->
        val variance = Random.nextInt(-4, 5)
        val newPing = (server.pingMs + variance).coerceIn(15, 180)
        server.copy(pingMs = newPing)
      }
      _selectedServer.value = _servers.value.firstOrNull { it.id == _selectedServer.value.id } ?: _servers.value[0]
      _isTestingPing.value = false
    }
  }

  fun importConfig(rawConfig: String): Boolean {
    val server = V2RayConfigParser.parse(rawConfig)
    return if (server != null) {
      val updatedList = listOf(server) + _servers.value
      _servers.value = updatedList
      _selectedServer.value = server
      _importMessage.value = "Successfully imported: ${server.name}"
      true
    } else {
      _importMessage.value = "Invalid V2Ray/Xray config format"
      false
    }
  }

  fun clearImportMessage() {
    _importMessage.value = null
  }

  fun formattedConnectionTime(): String {
    val totalSeconds = _durationSeconds.value
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d:%02d", hours, minutes, seconds)
  }
}
