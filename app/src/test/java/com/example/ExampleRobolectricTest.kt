package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.vpn.data.ServerRepository
import com.example.vpn.model.VpnState
import com.example.vpn.service.VpnController
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
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
    assertEquals("TIGERALI97 VPN", appName)
  }

  @Test
  fun `verify server repository and vpn controller`() {
    assertTrue(ServerRepository.defaultServers.isNotEmpty())
    val fastest = VpnController.findFastestServer()
    assertNotNull(fastest)
    assertEquals(VpnState.DISCONNECTED, VpnController.vpnState.value)
  }
}
