package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("RAKIB KING V2VPN", appName)
  }

  @Test
  fun `test v2ray config parsing`() {
    val sampleVless = "vless://439f0423-5e92-4217-a068-d0590895c19f@sg-test.node.com:443?encryption=none&security=reality#Singapore%20Fast%20Node"
    val parsed = com.example.model.V2RayConfigParser.parse(sampleVless)
    org.junit.Assert.assertNotNull(parsed)
    assertEquals("Singapore Fast Node", parsed?.name)
    assertEquals("Singapore", parsed?.country)
    assertEquals("🇸🇬", parsed?.flag)
  }
}
