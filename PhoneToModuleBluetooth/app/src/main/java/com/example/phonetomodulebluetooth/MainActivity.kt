package com.example.phonetomodulebluetooth

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    // private props
    private var m_bluetoothAdapter: BluetoothAdapter? = null
    private lateinit var m_pairedDevices: Set<BluetoothDevice> // list of paired devices to the Bluetooth adapter
    private val REQUEST_ENABLE_BLUETOOTH = 1 // bluetooth enable const

    // static properties
    companion object {
        val EXTRA_ADDRESS: String = "" // device addr
        val EXTRA_NAME: String = "" // device addr

    }

    // UI + data init
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // Set app UI

        // get device's Bluetooth adapter
        m_bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        // if device doesnt support BT
        if(m_bluetoothAdapter == null) {
            // spit out some err
            Log.i("bluetooth", "not supported")
            return
        }

        if(!m_bluetoothAdapter!!.isEnabled) {
            val enableBTIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBTIntent, REQUEST_ENABLE_BLUETOOTH)
//            Log.i("bluetooth", "device's bluetooth is now on")
        }


        // get/ refresh list of bluetooth devices
        refresh_button.setOnClickListener {
            pairedDeviceList()
        }

    }

    // get list of paired devices to the Bluetooth adapter
    private fun pairedDeviceList() {
        m_pairedDevices = m_bluetoothAdapter!!.bondedDevices // get data set of paired devices

        val all_devices : ArrayList<BluetoothDevice> = ArrayList()

        if (!m_pairedDevices.isEmpty()) { // if there are paired devices, populate all_devices collection
            for (device: BluetoothDevice in m_pairedDevices) {
                all_devices.add(device)
                Log.i("bluetooth", "found: " +device.name + " - MAC -  " + device) // log name + MAC addr for every paired device found
            }
        } else { // if no paired devices found, log msg
            Log.i("bluetooth", "no paired devices found")
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, all_devices) // handle UI list
        list_all_devices.adapter = adapter

        list_all_devices.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val device: BluetoothDevice = all_devices[position]
            //val device_name : String = device.name
            val address: String = device.address

            // pass info to TankControlActivity
            val intent = Intent(this, TankControlActivity::class.java)

            // put -> where, what
            intent.putExtra(Companion.EXTRA_ADDRESS, address)

            startActivity(intent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // if a request is sent
        if (requestCode == REQUEST_ENABLE_BLUETOOTH) {
            // if BT conn is ok
            if (resultCode == Activity.RESULT_OK) {

                if (m_bluetoothAdapter!!.isEnabled) {
                    // Bluetooth has been enabled
                    Log.i("bluetooth", "device's bluetooth is now on")
                } else {
                    // Bluetooth has been disabled
                    Log.i("bluetooth", "device's bluetooth is now off")
                }

            // if Bluetooth conn was cancelled for some reasong
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.i("bluetooth", "Belutooth connecting was cancelled")
            }
        }
    }
}
