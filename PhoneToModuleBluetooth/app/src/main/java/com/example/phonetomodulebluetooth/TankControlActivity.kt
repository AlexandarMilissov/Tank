package com.example.phonetomodulebluetooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.tank_control_layout.*
import java.io.IOException
import java.util.*

class TankControlActivity : AppCompatActivity() {

    companion object {
        var m_myUUID: UUID =
            UUID.fromString("00001101-0000-1000-8000-00805F9B34FB") // UUID of Bluetooth module (check ln80)
        // default uuid for serial comm
        var m_bluetoothSocket: BluetoothSocket? = null

        // lateinit var m_progress: ProgressDialog // what do ??
        lateinit var m_bluetoothAdapter: BluetoothAdapter
        var m_isConnected: Boolean = false
        lateinit var m_address: String // MAC address of Tank bluetooth module
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tank_control_layout) // set ui

        // MAC address of Tank bluetooth module
        m_address = intent.getStringExtra(MainActivity.EXTRA_ADDRESS)

        ConnectToDevice(this).execute() // koe e this LMAO

        left_track_forward.setOnClickListener {
            val command = "a"
            sendCommand(command)
            Log.i("left_track", "fwd")
        }

        // disconnect button, to close connection w/ curr device
        close_connection.setOnClickListener {
            disconnect()
        }
    }


    private fun sendCommand(input: String) {
        if (m_bluetoothSocket != null) {
            try {
                m_bluetoothSocket!!.outputStream.write(input.toByteArray())
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun disconnect() {
        if (m_bluetoothSocket != null) {
            try {
                m_bluetoothSocket!!.close()
                m_bluetoothSocket = null
                m_isConnected = false
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        finish()
    }

    private class ConnectToDevice(c: Context) : AsyncTask<Void, Void, String>() {
        private var connectSuccess: Boolean = true
        private val context: Context

        init {
            this.context = c
        }

        override fun onPreExecute() {
            super.onPreExecute()
            // m_progress = ProgressDialog.show(context, "Connecting...", "please wait")
        }

        override fun doInBackground(vararg p0: Void?): String? {
            try {

                // prep for connection
                if (m_bluetoothSocket == null || !m_isConnected) {
                    m_bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
                    val device: BluetoothDevice = m_bluetoothAdapter.getRemoteDevice(m_address)
                    m_bluetoothSocket = device.createInsecureRfcommSocketToServiceRecord(m_myUUID)
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery()

                    m_bluetoothSocket!!.connect() // attempt to connect to device
                    Log.i("connection", "connected to Bluetooth module")

                }
            } catch (e: IOException) {
                connectSuccess = false
                e.printStackTrace()
            }
            return null
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            if (!connectSuccess) {
                Log.i("connection", "couldn't connect")
            } else {
                m_isConnected = true
            }
            // m_progress.dismiss()

        }
    }
}

