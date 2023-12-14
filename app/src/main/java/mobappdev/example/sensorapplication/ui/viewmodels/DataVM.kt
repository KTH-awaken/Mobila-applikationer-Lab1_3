package mobappdev.example.sensorapplication.ui.viewmodels

import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import mobappdev.example.sensorapplication.data.model.CSVHelper
import mobappdev.example.sensorapplication.data.model.Measurement
import mobappdev.example.sensorapplication.domain.InternalSensorController
import mobappdev.example.sensorapplication.domain.PolarController
import mobappdev.example.sensorapplication.ui.PermissionRequester
import javax.inject.Inject

@HiltViewModel
class DataVM @Inject constructor(
    private val application: Application, // Inject application context
    private val polarController: PolarController,
    private val internalSensorController: InternalSensorController
): ViewModel() {

    private val gyroDataFlow = internalSensorController.currentGyroUI
    private val linAccDataFlow = internalSensorController.currentLinAccUI
    private val _savedData = internalSensorController.measurementsUI

    val polarGyroDataFlow = polarController.currentGyroUI
     val polarAccDataFlow = polarController.currentLinAccUI

    private val hrDataFlow = polarController.currentHR
    val savedData: StateFlow<List<List<Measurement>>> get() = _savedData

    private var _sensorMode = MutableStateFlow<String>("ACC")
    val sensorMode: StateFlow<String> get() = _sensorMode
    fun setSensorMode(sensorMode:String){
        _sensorMode.value=sensorMode
    }

    private var _sensorType = MutableStateFlow<String>("EXTERNAL")
    val sensorType: StateFlow<String> get() = _sensorType
    fun setSensorType(sensorType:String){
        _sensorType.value=sensorType
    }
    private val _maximumMeasurementTime = MutableStateFlow(10000)
    val maximumMeasurementTime: StateFlow<Int> get() = _maximumMeasurementTime
    fun setMaximumMesurmentTime(time:Int){
        Log.d("BLUETOOTH", "SET MAX TIME "+time)
        _maximumMeasurementTime.value=time
    }
    var permissionRequester: PermissionRequester? = null
//    private val _savedData = MutableStateFlow<List<Measurement>>(emptyList())
//    val savedData: StateFlow<List<Measurement>> get() = _savedData

    private var _premisionsGranted = MutableStateFlow(true)

    private var _isPolarStreaming = MutableStateFlow(false)
    val premisionsGranted: StateFlow<Boolean> get() = _premisionsGranted
    fun setPremisionGranted(premisionsGranted:Boolean){
        _premisionsGranted.value=premisionsGranted
    }

    // Combine the two data flows
    val combinedDataFlow= combine(
        if(_isPolarStreaming.value){
            Log.d("POLAR_STREAM","isPolarStreaming=${_isPolarStreaming.value}")
            polarGyroDataFlow
        } else {gyroDataFlow},
        hrDataFlow,

        if(_isPolarStreaming.value){
            Log.d("POLAR_STREAM","isPolarStreaming=${_isPolarStreaming.value}")
            polarAccDataFlow
        } else {linAccDataFlow}
    ) { gyro, hr,linAcc ->
        if (hr != null ) {
            CombinedSensorData.HrData(hr)
        } else if (gyro != null && linAcc != null) {
            CombinedSensorData.LinAccAndGyroData(linAcc,gyro)
        }else {
            null
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _state = MutableStateFlow(DataUiState())
    val state = combine(
        polarController.hrList,
        polarController.connected,
        _state
    ) { hrList, connected, state ->
        state.copy(
            hrList = hrList,
            connected = connected,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), _state.value)

    private var streamType: StreamType? = null


    private val _deviceId = MutableStateFlow("")
    val deviceId: StateFlow<String>
        get() = _deviceId.asStateFlow()

    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private val _devices = MutableStateFlow<List<BluetoothDevice>>(emptyList())
    val devices: StateFlow<List<BluetoothDevice>> = _devices.asStateFlow()

    companion object {
        const val REQUEST_FINE_LOCATION_PERMISSIONS = 1001 // Unique request code
    }


    fun startDeviceScan() {
        Log.d("BLUETOOTH", "1")
        if (ContextCompat.checkSelfPermission(application, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            bluetoothAdapter?.startDiscovery()
            startDiscovery()
        Log.d("BLUETOOTH", "2")
        } else {
        Log.d("BLUETOOTH", "-3")
            permissionRequester?.triggerPermissionRequest(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_FINE_LOCATION_PERMISSIONS)
        }
    }


    init {
        startDiscovery()
    }

    override fun onCleared() {
        super.onCleared()
        // Unregister the BroadcastReceiver
        application.unregisterReceiver(receiver)
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Log.d("BLUETOOTH", "3")
            val action: String = intent.action!!
            when(action) {
                BluetoothDevice.ACTION_FOUND -> {
            Log.d("BLUETOOTH", "4")
                    val device: BluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)!!
                    if (isPolarDevice(device,context)) {
            Log.d("BLUETOOTH", "5")
                        _devices.value = _devices.value + device
                    }
                }
                // Handle other actions if needed
            }
        }
    }

    private fun startDiscovery() {
        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        application.registerReceiver(receiver, filter)
    }


    private fun isPolarDevice(device: BluetoothDevice, context: Context): Boolean {
        // Check if location permissions are granted
        if (ContextCompat.checkSelfPermission(application, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.d("BLUETOOTH", "PREMISIONS GRANTED")
            return device.name?.startsWith("Polar") == true

        } else {
            Log.d("BLUETOOTH", "PREMISIONS DENIDE")
            permissionRequester?.triggerPermissionRequest(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_FINE_LOCATION_PERMISSIONS)
        }
        // Permission is granted, proceed with the Bluetooth device check
        return device.name?.startsWith("Polar") == true
    }


    fun chooseSensor(deviceId: String) {
        Log.d("BLUETOOTH", "CHOOSE SENSOR "+deviceId)
        _deviceId.update { deviceId }
    }

    fun connectToSensor() {
        polarController.connectToDevice(_deviceId.value)
    }

    fun disconnectFromSensor() {
        stopDataStream()
        polarController.disconnectFromDevice(_deviceId.value)
    }

    fun startHr() {
        polarController.startHrStreaming(_deviceId.value)
        streamType = StreamType.FOREIGN_HR
        _state.update { it.copy(measuring = true) }
    }

    fun startGyro() {
        _isPolarStreaming.value = false
        internalSensorController.startGyroStream()
        streamType = StreamType.LOCAL_GYRO

        _state.update { it.copy(measuring = true) }

        viewModelScope.launch {
            val maxTime = maximumMeasurementTime.value
            delay(maxTime.toLong()) // 10 seconds in milliseconds
//            stopDataStream()
            internalSensorController.stopGyroStream()
            Log.d("BLUETOOTH", "STOPPED STREAMING TIMER DONE")

        }

    }

    fun startLinAcc() {
        _isPolarStreaming.value = false
        internalSensorController.startImuStream()
        streamType = StreamType.LOCAL_ACC

        _state.update { it.copy(measuring = true) }
    }

    fun startPolarGyro(){
        _isPolarStreaming.value = true
        streamType = StreamType.EXTERNAL_GYRO
        polarController.startGyroStream(deviceId.value)

        Log.d("POLARGYRO", polarController.currentGyroUI.value.toString())
        _state.update { it.copy(measuring = true) }
    }

    fun stopDataStream(){
        when (streamType) {
            StreamType.LOCAL_GYRO -> internalSensorController.stopGyroStream()
            StreamType.LOCAL_ACC -> internalSensorController.stopImuStream()
            StreamType.FOREIGN_HR -> polarController.stopHrStreaming()
            StreamType.EXTERNAL_GYRO ->{
                _isPolarStreaming.value = false
                polarController.stopGyroStream()
            }
            else -> {} // Do nothing
        }
        _state.update { it.copy(measuring = false) }
    }
    fun exportMeasurements(measurements:List<Measurement>){
        internalSensorController.exportMeasurements(measurements = measurements)
    }
}

data class DataUiState(
    val hrList: List<Int> = emptyList(),
    val connected: Boolean = false,
    val measuring: Boolean = false
)

enum class StreamType {
    LOCAL_GYRO, LOCAL_ACC, FOREIGN_HR,EXTERNAL_GYRO
}

sealed class CombinedSensorData {
    data class GyroData(val gyro: Triple<Float, Float, Float>?) : CombinedSensorData()
    data class LinAccData(val linAcc: Triple<Float, Float, Float>?): CombinedSensorData()
    data class HrData(val hr: Int?) : CombinedSensorData()
    data class LinAccAndGyroData(val linAcc: Triple<Float, Float, Float>?,val gyro: Triple<Float, Float, Float>?)

}

