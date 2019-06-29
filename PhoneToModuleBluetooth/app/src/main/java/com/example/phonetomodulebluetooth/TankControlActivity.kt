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

    // static properties
    companion object {
        var m_myUUID: UUID =
            UUID.fromString("00001101-0000-1000-8000-00805F9B34FB") // UUID to connect to Bluetooth module, default for serial comm

        var m_bluetoothSocket: BluetoothSocket? = null
        lateinit var m_bluetoothAdapter: BluetoothAdapter
        var m_isConnected: Boolean = false
        var m_address: String? = "" // MAC address of Tank bluetooth module
    }

    // UI + data init
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tank_control_layout) // Set app UI

        // get MAC address of Tank bluetooth module from MainActivity
        m_address = intent.getStringExtra(MainActivity.EXTRA_ADDRESS)

        ConnectToDevice(this).execute() // this = Current context

        // left track stop
        left_track_stop.setOnClickListener {
            val command = "a"
            sendCommand(command)
            Log.i("left_track", "stop")
        }

        // left track forward
        left_track_forward.setOnClickListener {
            val command = "b"
            sendCommand(command)
            Log.i("left_track", "fwd")
        }

        // left track backwards
        left_track_backwards.setOnClickListener {
            val command = "c"
            sendCommand(command)
            Log.i("left_track", "bckwd")
        }

        // right track stop
        right_track_stop.setOnClickListener {
            val command = "d"
            sendCommand(command)
            Log.i("right_track", "stop")
        }

        // right track foward
        right_track_forward.setOnClickListener {
            val command = "e"
            sendCommand(command)
            Log.i("right_track", "fwd")
        }

        // right track backwards
        right_track_backwards.setOnClickListener {
            val command = "f"
            sendCommand(command)
            Log.i("right_track", "bckwd")
        }


        // disconnect button, to close connection w/ curr device (Bluetooth module)
        close_connection.setOnClickListener {
            Log.i("connection", "closing connection. . . ")
            disconnect()
        }
    }

    // send commands via Bluetooth
    private fun sendCommand(input: String) {
        if (m_bluetoothSocket != null) {
            try {
                m_bluetoothSocket!!.outputStream.write(input.toByteArray())
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    // terminate Bluetooth connection
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

    // connect to device via Bluetooth
    private class ConnectToDevice(c: Context) : AsyncTask<Void, Void, String>() {
        private var connectSuccess: Boolean = true
        private val context: Context

        init {
            this.context = c
        }

        override fun onPreExecute() {
            super.onPreExecute()
        }

        override fun doInBackground(vararg p0: Void?): String? {
            try {

                // prep for connection, var assignment
                if (m_bluetoothSocket == null || !m_isConnected) {
                    m_bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
                    val device: BluetoothDevice = m_bluetoothAdapter.getRemoteDevice(m_address)
                    m_bluetoothSocket = device.createInsecureRfcommSocketToServiceRecord(m_myUUID)
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery()

                    m_bluetoothSocket!!.connect() // attempt to connect to device

                    // log after successful connection
                    Log.i("connection", "connected to Bluetooth module")

                }
            } catch (e: IOException) { // if connection fails
                connectSuccess = false
                e.printStackTrace()
                Log.i("connection", "connection failed")
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
        }
    }
}

