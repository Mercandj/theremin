package com.mercandalli.android.apps.theremin.wifi

import android.content.Context
import android.net.wifi.WifiManager
import android.util.Log
import java.math.BigInteger
import java.net.InetAddress
import java.net.UnknownHostException
import java.nio.ByteOrder

class WifiUtils {

    companion object {

        @JvmStatic
        fun wifiIpAddress(context: Context): String? {
            val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
            var ipAddress = wifiManager.connectionInfo.ipAddress

            // Convert little-endian to big-endianif needed
            if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
                ipAddress = Integer.reverseBytes(ipAddress)
            }

            val ipByteArray = BigInteger.valueOf(ipAddress.toLong()).toByteArray()

            val ipAddressString: String?
            ipAddressString = try {
                InetAddress.getByAddress(ipByteArray).hostAddress
            } catch (ex: UnknownHostException) {
                Log.e("WIFIIP", "Unable to get host address.")
                null
            }

            return ipAddressString
        }
    }
}
