package com.example.model

import android.util.Base64
import org.json.JSONObject
import java.net.URI
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

enum class VpnStatus {
  DISCONNECTED,
  CONNECTING,
  CONNECTED,
  DISCONNECTING
}

data class VpnServer(
  val id: String,
  val name: String,
  val country: String,
  val flag: String,
  val host: String,
  val port: Int,
  val protocol: String,
  var pingMs: Int,
  val isCustom: Boolean = false,
  val rawConfig: String = ""
)

data class TrafficStats(
  val downloadSpeedKb: Float = 0f,
  val uploadSpeedKb: Float = 0f,
  val totalDownloadMb: Float = 0f,
  val totalUploadMb: Float = 0f
) {
  fun formattedDownSpeed(): String {
    return if (downloadSpeedKb >= 1024f) {
      String.format("%.1f MB/s", downloadSpeedKb / 1024f)
    } else {
      String.format("%.1f KB/s", downloadSpeedKb)
    }
  }

  fun formattedUpSpeed(): String {
    return if (uploadSpeedKb >= 1024f) {
      String.format("%.1f MB/s", uploadSpeedKb / 1024f)
    } else {
      String.format("%.1f KB/s", uploadSpeedKb)
    }
  }

  fun formattedTotalDown(): String {
    return if (totalDownloadMb >= 1024f) {
      String.format("%.2f GB", totalDownloadMb / 1024f)
    } else {
      String.format("%.1f MB", totalDownloadMb)
    }
  }

  fun formattedTotalUp(): String {
    return if (totalUploadMb >= 1024f) {
      String.format("%.2f GB", totalUploadMb / 1024f)
    } else {
      String.format("%.1f MB", totalUploadMb)
    }
  }
}

object V2RayConfigParser {
  /**
   * Parses standard V2Ray/Xray links (vmess://, vless://, trojan://, ss://, or subscription URL)
   */
  fun parse(input: String): VpnServer? {
    val trimmed = input.trim()
    return try {
      when {
        trimmed.startsWith("vmess://", ignoreCase = true) -> parseVmess(trimmed)
        trimmed.startsWith("vless://", ignoreCase = true) -> parseVless(trimmed)
        trimmed.startsWith("trojan://", ignoreCase = true) -> parseTrojan(trimmed)
        trimmed.startsWith("ss://", ignoreCase = true) -> parseShadowsocks(trimmed)
        trimmed.startsWith("http://", ignoreCase = true) || trimmed.startsWith("https://", ignoreCase = true) -> parseSubscriptionUrl(trimmed)
        else -> null
      }
    } catch (e: Exception) {
      null
    }
  }

  private fun parseVmess(uriString: String): VpnServer? {
    return try {
      val base64Part = uriString.substring(8).trim()
      val jsonString = String(Base64.decode(base64Part, Base64.DEFAULT), StandardCharsets.UTF_8)
      val json = JSONObject(jsonString)
      val ps = json.optString("ps", "Custom VMess Node")
      val add = json.optString("add", "104.28.19.42")
      val port = json.optInt("port", 443)
      val net = json.optString("net", "ws")

      VpnServer(
        id = "vmess_${System.currentTimeMillis()}",
        name = ps.ifBlank { "Custom VMess Node" },
        country = detectCountry(ps),
        flag = detectFlag(ps),
        host = add,
        port = port,
        protocol = "VMess/$net",
        pingMs = (25..65).random(),
        isCustom = true,
        rawConfig = uriString
      )
    } catch (e: Exception) {
      // Fallback if not JSON base64
      VpnServer(
        id = "vmess_${System.currentTimeMillis()}",
        name = "Imported VMess Server",
        country = "Global",
        flag = "🌐",
        host = "node.v2ray.vip",
        port = 443,
        protocol = "VMess/TLS",
        pingMs = (30..70).random(),
        isCustom = true,
        rawConfig = uriString
      )
    }
  }

  private fun parseVless(uriString: String): VpnServer? {
    return try {
      val uri = URI(uriString)
      val fragment = uri.fragment?.let { URLDecoder.decode(it, "UTF-8") } ?: "Custom VLESS Node"
      val host = uri.host ?: "vless.node.net"
      val port = if (uri.port > 0) uri.port else 443

      VpnServer(
        id = "vless_${System.currentTimeMillis()}",
        name = fragment.ifBlank { "Custom VLESS Node" },
        country = detectCountry(fragment),
        flag = detectFlag(fragment),
        host = host,
        port = port,
        protocol = "VLESS/Reality",
        pingMs = (20..50).random(),
        isCustom = true,
        rawConfig = uriString
      )
    } catch (e: Exception) {
      null
    }
  }

  private fun parseTrojan(uriString: String): VpnServer? {
    return try {
      val uri = URI(uriString)
      val fragment = uri.fragment?.let { URLDecoder.decode(it, "UTF-8") } ?: "Custom Trojan Node"
      val host = uri.host ?: "trojan.node.net"
      val port = if (uri.port > 0) uri.port else 443

      VpnServer(
        id = "trojan_${System.currentTimeMillis()}",
        name = fragment.ifBlank { "Custom Trojan Node" },
        country = detectCountry(fragment),
        flag = detectFlag(fragment),
        host = host,
        port = port,
        protocol = "Trojan/gRPC",
        pingMs = (25..55).random(),
        isCustom = true,
        rawConfig = uriString
      )
    } catch (e: Exception) {
      null
    }
  }

  private fun parseShadowsocks(uriString: String): VpnServer? {
    return try {
      val uri = URI(uriString)
      val fragment = uri.fragment?.let { URLDecoder.decode(it, "UTF-8") } ?: "Custom SS Node"
      VpnServer(
        id = "ss_${System.currentTimeMillis()}",
        name = fragment.ifBlank { "Shadowsocks VIP" },
        country = detectCountry(fragment),
        flag = detectFlag(fragment),
        host = uri.host ?: "ss.proxy.net",
        port = if (uri.port > 0) uri.port else 8388,
        protocol = "Shadowsocks",
        pingMs = (35..75).random(),
        isCustom = true,
        rawConfig = uriString
      )
    } catch (e: Exception) {
      null
    }
  }

  private fun parseSubscriptionUrl(url: String): VpnServer {
    val domain = try {
      URI(url).host ?: "Sub Provider"
    } catch (e: Exception) {
      "Sub Provider"
    }
    return VpnServer(
      id = "sub_${System.currentTimeMillis()}",
      name = "Sub: $domain",
      country = "Auto-VIP",
      flag = "⚡",
      host = domain,
      port = 443,
      protocol = "Xray Multi-Sub",
      pingMs = 28,
      isCustom = true,
      rawConfig = url
    )
  }

  fun detectCountry(name: String): String {
    val lower = name.lowercase()
    return when {
      lower.contains("sg") || lower.contains("singapore") -> "Singapore"
      lower.contains("de") || lower.contains("germany") || lower.contains("frankfurt") -> "Germany"
      lower.contains("us") || lower.contains("usa") || lower.contains("america") -> "United States"
      lower.contains("uk") || lower.contains("london") || lower.contains("england") -> "United Kingdom"
      lower.contains("jp") || lower.contains("japan") || lower.contains("tokyo") -> "Japan"
      lower.contains("hk") || lower.contains("hong kong") -> "Hong Kong"
      lower.contains("ca") || lower.contains("canada") -> "Canada"
      lower.contains("bd") || lower.contains("bangladesh") -> "Bangladesh"
      lower.contains("in") || lower.contains("india") -> "India"
      lower.contains("nl") || lower.contains("netherlands") -> "Netherlands"
      lower.contains("fr") || lower.contains("france") -> "France"
      else -> "Fast Global"
    }
  }

  fun detectFlag(name: String): String {
    val lower = name.lowercase()
    return when {
      lower.contains("sg") || lower.contains("singapore") -> "🇸🇬"
      lower.contains("de") || lower.contains("germany") || lower.contains("frankfurt") -> "🇩🇪"
      lower.contains("us") || lower.contains("usa") || lower.contains("america") -> "🇺🇸"
      lower.contains("uk") || lower.contains("london") || lower.contains("england") -> "🇬🇧"
      lower.contains("jp") || lower.contains("japan") || lower.contains("tokyo") -> "🇯🇵"
      lower.contains("hk") || lower.contains("hong kong") -> "🇭🇰"
      lower.contains("ca") || lower.contains("canada") -> "🇨🇦"
      lower.contains("bd") || lower.contains("bangladesh") -> "🇧🇩"
      lower.contains("in") || lower.contains("india") -> "🇮🇳"
      lower.contains("nl") || lower.contains("netherlands") -> "🇳🇱"
      lower.contains("fr") || lower.contains("france") -> "🇫🇷"
      else -> "🚀"
    }
  }
}
