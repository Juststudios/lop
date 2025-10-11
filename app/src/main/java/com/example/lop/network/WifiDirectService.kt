package com.example.lop.network

import android.content.Context
import android.net.wifi.p2p.WifiP2pManager

class WifiDirectService(private val context: Context) {

    private val manager: WifiP2pManager = context.getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager
    private val channel: WifiP2pManager.Channel = manager.initialize(context, context.mainLooper, null)

    fun startConnection() {
        // TODO: Discover peers and connect
    }

    fun sendData(data: ByteArray) {
        // TODO: Transfer contact/business card info
    }
}