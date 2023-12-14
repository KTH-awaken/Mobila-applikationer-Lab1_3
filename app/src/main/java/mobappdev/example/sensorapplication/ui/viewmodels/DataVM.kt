package mobappdev.example.sensorapplication.ui.viewmodels

import android.annotation.SuppressLint
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
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
    private val hrDataFlow = polarController.currentHR
    private val linAccDataFlow = internalSensorController.currentLinAccUI
    private val _savedData = internalSensorController.measurementsUI
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
    private val _maximumMeasurementTime = MutableStateFlow(30)
    val maximumMeasurementTime: StateFlow<Int> get() = _maximumMeasurementTime

    var permissionRequester: PermissionRequester? = null
//    private val _savedData = MutableStateFlow<List<Measurement>>(emptyList())
//    val savedData: StateFlow<List<Measurement>> get() = _savedData

    // Combine the two data flows
    val combinedDataFlow= combine(
        gyroDataFlow,
        hrDataFlow,
        linAccDataFlow
    ) { gyro, hr,linAcc ->
        if (hr != null ) {
            CombinedSensorData.HrData(hr)
        } else if (gyro != null) {
            CombinedSensorData.GyroData(gyro)
        }else if (linAcc != null){
            CombinedSensorData.LinAccData(linAcc)
        } else {
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
        Log.d("BLUETOOTH", "2")
        } else {
        Log.d("BLUETOOTH", "-3")
            permissionRequester?.triggerPermissionRequest(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_FINE_LOCATION_PERMISSIONS)
        }
    }


    private fun startDiscovery() {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val action: String = intent.action!!
                when(action) {
                    BluetoothDevice.ACTION_FOUND -> {
                        val device: BluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)!!
                        if (isPolarDevice(device)) {
                            _devices.value = _devices.value + device
                        }
                    }
                    // Handle other actions like BluetoothAdapter.ACTION_DISCOVERY_FINISHED if needed
                }
            }
        }

        // Register for broadcasts when a device is discovered
        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        //... register receiver with the filter
    }

    @SuppressLint("MissingPermission")//todo ta bort om inte går att to connecta
    private fun isPolarDevice(device: BluetoothDevice): Boolean {
        return device.name?.startsWith("Polar") == true
    }

    fun connectToDevice(device: BluetoothDevice) {
        // Implement connection logic
    }

    fun disconnectFromDevice(device: BluetoothDevice) {
        // Implement disconnection logic
    }

    fun chooseSensor(deviceId: String) {
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
        internalSensorController.startGyroStream()
        streamType = StreamType.LOCAL_GYRO

        _state.update { it.copy(measuring = true) }
    }

    fun startLinAcc() {
        internalSensorController.startImuStream()
        streamType = StreamType.LOCAL_ACC

        _state.update { it.copy(measuring = true) }
    }

    fun stopDataStream(){
        when (streamType) {
            StreamType.LOCAL_GYRO -> internalSensorController.stopGyroStream()
            StreamType.LOCAL_ACC -> internalSensorController.stopImuStream()
            StreamType.FOREIGN_HR -> polarController.stopHrStreaming()
            else -> {} // Do nothing
        }
        _state.update { it.copy(measuring = false) }
    }
}

data class DataUiState(
    val hrList: List<Int> = emptyList(),
    val connected: Boolean = false,
    val measuring: Boolean = false
)

enum class StreamType {
    LOCAL_GYRO, LOCAL_ACC, FOREIGN_HR
}

sealed class CombinedSensorData {
    data class GyroData(val gyro: Triple<Float, Float, Float>?) : CombinedSensorData()
    data class LinAccData(val linAcc: Triple<Float, Float, Float>?): CombinedSensorData()
    data class HrData(val hr: Int?) : CombinedSensorData()

}

