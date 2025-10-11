package com.example.lop.network

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.p2p.*
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.*
import java.net.ServerSocket
import java.net.Socket

class ConnectionManager(
    private val context: Context,
    private val onPeerListChanged: (List<WifiP2pDevice>) -> Unit,
    private val onConnectionInfoAvailable: (WifiP2pInfo) -> Unit,
    private val onReceivedData: (String) -> Unit
) {

    private var manager: WifiP2pManager? = null
    private var channel: WifiP2pManager.Channel? = null
    private var receiver: BroadcastReceiver? = null

    init {
        manager = context.getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager
        channel = manager?.initialize(context, context.mainLooper, null)
        registerReceiver()
    }

    fun cleanup() {
        try {
            receiver?.let { context.unregisterReceiver(it) }
        } catch (_: Exception) {}
    }

    private fun registerReceiver() {
        val filter = IntentFilter().apply {
            addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)
            addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)
            addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)
            addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)
        }
        receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                when (intent?.action) {
                    WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION -> {
                        // request current peers
                        manager?.requestPeers(channel) { peers ->
                            onPeerListChanged(peers.deviceList.toList())
                        }
                    }
                    WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION -> {
                        val networkInfo = intent.getParcelableExtra<android.net.NetworkInfo>(WifiP2pManager.EXTRA_NETWORK_INFO)
                        if (networkInfo != null && networkInfo.isConnected) {
                            // request connection info
                            manager?.requestConnectionInfo(channel) { info ->
                                onConnectionInfoAvailable(info)
                            }
                        }
                    }
                }
            }
        }
        context.registerReceiver(receiver, filter)
    }

    fun discoverPeers() {
        manager?.discoverPeers(channel, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                Toast.makeText(context, "Discover started", Toast.LENGTH_SHORT).show()
            }
            override fun onFailure(reason: Int) {
                Toast.makeText(context, "Discover failed: $reason", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun connectToDevice(device: WifiP2pDevice) {
        val config = WifiP2pConfig().apply { deviceAddress = device.deviceAddress }
        manager?.connect(channel, config, object : WifiP2pManager.ActionListener {
            override fun onSuccess() { Toast.makeText(context, "Connecting...", Toast.LENGTH_SHORT).show() }
            override fun onFailure(reason: Int) { Toast.makeText(context, "Connect failed: $reason", Toast.LENGTH_SHORT).show() }
        })
    }

    // call once WifiP2pInfo is available
    fun startDataTransport(info: WifiP2pInfo, sendJson: String) {
        if (info.isGroupOwner) {
            startServer()
        } else {
            val host = info.groupOwnerAddress.hostAddress
            startClient(host, sendJson)
        }
    }

    private fun startServer() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val server = ServerSocket(8888)
                val client = server.accept()
                val reader = BufferedReader(InputStreamReader(client.getInputStream()))
                val data = reader.readLine()
                client.close()
                server.close()
                CoroutineScope(Dispatchers.Main).launch { onReceivedData(data) }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun startClient(host: String, sendJson: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val socket = Socket(host, 8888)
                val writer = BufferedWriter(OutputStreamWriter(socket.getOutputStream()))
                writer.write(sendJson)
                writer.newLine()
                writer.flush()
                socket.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
