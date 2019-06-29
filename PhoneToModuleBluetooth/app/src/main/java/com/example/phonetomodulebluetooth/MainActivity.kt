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

    private var m_bluetoothAdapter: BluetoothAdapter? = null
    private  lateinit var m_pairedDevices: Set<BluetoothDevice>
    private val REQUEST_ENABLE_BLUETOOTH = 1

    companion object { // static prop
        val EXTRA_ADDRESS: String = "Device_address" // device addr
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // set ui

        // get BT device obj instance
        m_bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        // if device doesnt support BT
        if(m_bluetoothAdapter == null) {
            // spit out some err
            println("this device doesn't support bluetooth");
            return
        }

        if(!m_bluetoothAdapter!!.isEnabled) {
            val enableBTIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBTIntent, REQUEST_ENABLE_BLUETOOTH)
            println("device bluetooth is now on")
        }


        // refresh list of bluetooth devices
        refresh_button.setOnClickListener {
            pairedDeviceList()
        }

    }

    private fun pairedDeviceList() {
        m_pairedDevices = m_bluetoothAdapter!!.bondedDevices

        val all_devices : ArrayList<BluetoothDevice> = ArrayList()

        if (!m_pairedDevices.isEmpty()) {
            for (device: BluetoothDevice in m_pairedDevices) {
                all_devices.add(device)
                Log.i("found device", ""+device)
            }
        } else {
            // msg no devices found
            println("no bluetooth devices discovered")

        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, all_devices)
        list_all_devices.adapter = adapter
        list_all_devices.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val device: BluetoothDevice = all_devices[position]
            val address: String = device.address
            //val device_name: String = device.name


            val intent = Intent(this, TankControlActivity::class.java)
            intent.putExtra(Companion.EXTRA_ADDRESS, address)
            // where, what

            //intent.putExtra(Companion.EXTRA_ADDRESS, device_name)
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

                    // bluetooth has been enabled

                } else {

                    // bluetooth has been disabled

                }

            // if BT conn was cancelled
            } else if (resultCode == Activity.RESULT_CANCELED) {

                // bluetooth enabling was cancelled for some reason

            }
        }
    }
}
