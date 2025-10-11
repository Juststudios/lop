package com.example.lop.network

import android.content.Context
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pInfo
import android.net.wifi.p2p.WifiP2pManager
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.*
import java.net.ServerSocket
import java.net.Socket

class ConnectionManager(private val context: Context) {

    private var wifiManager: WifiP2pManager? = null
    private var channel: WifiP2pManager.Channel? = null
    private var isGroupOwner = false
    private var hostAddress: String? = null

    fun init() {
        wifiManager = context.getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager
        channel = wifiManager?.initialize(context, context.mainLooper, null)
    }

    fun discoverPeers() {
        wifiManager?.discoverPeers(channel, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                Toast.makeText(context, "Searching for nearby devices...", Toast.LENGTH_SHORT).show()
            }
            override fun onFailure(reason: Int) {
                Toast.makeText(context, "Failed to discover peers: $reason", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun connectToDevice(device: WifiP2pDevice) {
        val config = WifiP2pManager.Config().apply { deviceAddress = device.deviceAddress }
        wifiManager?.connect(channel, config, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                Toast.makeText(context, "Connecting to ${device.deviceName}", Toast.LENGTH_SHORT).show()
            }
            override fun onFailure(reason: Int) {
                Toast.makeText(context, "Connection failed: $reason", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun startDataTransfer(info: WifiP2pInfo, contactJson: String, onReceived: (String) -> Unit) {
        isGroupOwner = info.isGroupOwner
        hostAddress = info.groupOwnerAddress.hostAddress

        if (isGroupOwner) {
            startServer(onReceived)
        } else {
            hostAddress?.let { startClient(it, contactJson) }
        }
    }

    private fun startServer(onReceived: (String) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val serverSocket = ServerSocket(8888)
                val client = serverSocket.accept()
                val reader = BufferedReader(InputStreamReader(client.getInputStream()))
                val data = reader.readLine()
                client.close()
                serverSocket.close()

                CoroutineScope(Dispatchers.Main).launch {
                    onReceived(data)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun startClient(host: String, contactJson: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val socket = Socket(host, 8888)
                val writer = BufferedWriter(OutputStreamWriter(socket.getOutputStream()))
                writer.write(contactJson)
                writer.newLine()
                writer.flush()
                socket.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}