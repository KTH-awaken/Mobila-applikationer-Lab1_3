package mobappdev.example.sensorapplication.data

/**
 * File: AndroidPolarController.kt
 * Purpose: Implementation of the PolarController Interface.
 *          Communicates with the polar API
 * Author: Jitse van Esch
 * Created: 2023-07-08
 * Last modified: 2023-07-11
 */

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.polar.sdk.api.PolarBleApi
import com.polar.sdk.api.PolarBleApiCallback
import com.polar.sdk.api.PolarBleApiDefaultImpl
import com.polar.sdk.api.errors.PolarInvalidArgument
import com.polar.sdk.api.model.PolarAccelerometerData
import com.polar.sdk.api.model.PolarDeviceInfo
import com.polar.sdk.api.model.PolarGyroData
import com.polar.sdk.api.model.PolarHrData
import com.polar.sdk.api.model.PolarSensorSetting
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.Disposable
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import mobappdev.example.sensorapplication.data.Repository.MeasurementsRepo
import mobappdev.example.sensorapplication.data.model.MathFilter
import mobappdev.example.sensorapplication.data.model.Measurement
import mobappdev.example.sensorapplication.data.model.addMeasurement
import mobappdev.example.sensorapplication.data.model.addMeasurementToList
import mobappdev.example.sensorapplication.domain.PolarController
import java.util.UUID

class AndroidPolarController (
    private val context: Context,
    val measurementsRepo: MeasurementsRepo
): PolarController {

    private val api: PolarBleApi by lazy {
        // Notice all features are enabled
        PolarBleApiDefaultImpl.defaultImplementation(
            context = context,
            setOf(
                PolarBleApi.PolarBleSdkFeature.FEATURE_HR,
                PolarBleApi.PolarBleSdkFeature.FEATURE_POLAR_SDK_MODE,
                PolarBleApi.PolarBleSdkFeature.FEATURE_BATTERY_INFO,
                PolarBleApi.PolarBleSdkFeature.FEATURE_POLAR_H10_EXERCISE_RECORDING,
                PolarBleApi.PolarBleSdkFeature.FEATURE_POLAR_OFFLINE_RECORDING,
                PolarBleApi.PolarBleSdkFeature.FEATURE_POLAR_ONLINE_STREAMING,
                PolarBleApi.PolarBleSdkFeature.FEATURE_POLAR_DEVICE_TIME_SETUP,
                PolarBleApi.PolarBleSdkFeature.FEATURE_DEVICE_INFO
            )
        )
    }

    private var hrDisposable: Disposable? = null
    private val TAG = "AndroidPolarController"

    private val _currentHR = MutableStateFlow<Int?>(null)
    override val currentHR: StateFlow<Int?>
        get() = _currentHR.asStateFlow()

    private val _hrList = MutableStateFlow<List<Int>>(emptyList())
    override val hrList: StateFlow<List<Int>>
        get() = _hrList.asStateFlow()

    private val _connected = MutableStateFlow(false)
    override val connected: StateFlow<Boolean>
        get() = _connected.asStateFlow()

    private val _measuring = MutableStateFlow(false)
    override val measuring: StateFlow<Boolean>
        get() = _measuring.asStateFlow()


    private val _currentLinAccUI = MutableStateFlow<Triple<Float, Float, Float>?>(null)
    override val currentLinAccUI: StateFlow<Triple<Float, Float, Float>?>
        get() = _currentLinAccUI.asStateFlow()

    private val _currentGyroUI = MutableStateFlow<Triple<Float, Float, Float>?>(null)
    override val currentGyroUI: StateFlow<Triple<Float, Float, Float>?>
        get() = _currentGyroUI.asStateFlow()

    private val _streamingGyro = MutableStateFlow(false)
    override val streamingGyro: StateFlow<Boolean>
        get() = _streamingGyro.asStateFlow()

    private val _streamingLinAcc = MutableStateFlow(false)
    override val streamingLinAcc: StateFlow<Boolean>
        get() = _streamingLinAcc.asStateFlow()

    private var _currentGyro: Triple<Float, Float, Float>? = null
    private var _currentLinAcc: Triple<Float, Float, Float>? = null


    private val _measurementsUI = MutableStateFlow<List<List<Measurement>>>(emptyList())
    override val measurementsUI: StateFlow<List<List<Measurement>>>
        get() = _measurementsUI.asStateFlow()

    override fun updateUIMeasurements() {
        GlobalScope.launch {
            var listOfListOfMeasurement = emptyList<List<Measurement>>()
            measurementsRepo.listOfMeasurementsFlow.collect { newMeasurements  ->
                listOfListOfMeasurement = newMeasurements
                Log.d("COLLECTING_POLAR","Size of listOfMeasurements: ${listOfListOfMeasurement.size}")
                Log.d("COLLECTING_POLAR","listOfMeasurements: $listOfListOfMeasurement")
            }

            _measurementsUI.update { listOfListOfMeasurement }
        }
    }

    private var _currentMeasurements = MutableStateFlow<List<Measurement>>(emptyList())

    private lateinit var broadcastDisposable: Disposable

    private var lastLinAccSample: Triple<Float, Float, Float>? = null
    private var lastGyroSample: Triple<Float, Float, Float>? = null

    private lateinit var mathFilter: MathFilter



    init {
        api.setPolarFilter(false)

        val enableSdkLogs = false
        if(enableSdkLogs) {
            api.setApiLogger { s: String -> Log.d("Polar API Logger", s) }
        }

        api.setApiCallback(object: PolarBleApiCallback() {
            override fun batteryLevelReceived(identifier: String, level: Int) {
                Log.d(TAG, "BATTERY LEVEL: $level")
            }

            override fun deviceConnected(polarDeviceInfo: PolarDeviceInfo) {
                Log.d(TAG, "CONNECTED: ${polarDeviceInfo.deviceId}")
                _connected.update { true }
            }

            override fun deviceConnecting(polarDeviceInfo: PolarDeviceInfo) {
                Log.d(TAG, "CONNECTING: ${polarDeviceInfo.deviceId}")
            }

            override fun deviceDisconnected(polarDeviceInfo: PolarDeviceInfo) {
                Log.d(TAG, "DISCONNECTED: ${polarDeviceInfo.deviceId}")
                _connected.update { false }
            }

            override fun disInformationReceived(identifier: String, uuid: UUID, value: String) {
                Log.d(TAG, "DIS INFO uuid: $uuid value: $value")
            }
        })
    }

    override fun connectToDevice(deviceId: String) {
        try {
            api.connectToDevice(deviceId)
        } catch (polarInvalidArgument: PolarInvalidArgument) {
            Log.e(TAG, "Failed to connect to $deviceId.\n Reason $polarInvalidArgument")
        }
    }

    override fun disconnectFromDevice(deviceId: String) {
        try {
            api.disconnectFromDevice(deviceId)
        } catch (polarInvalidArgument: PolarInvalidArgument) {
            Log.e(TAG, "Failed to disconnect from $deviceId.\n Reason $polarInvalidArgument")
        }
    }

    @SuppressLint("CheckResult")
    override fun startGyroStream(deviceId:String) { //TODO add saving
        var gyroData: Triple<Float,Float,Float>
        if (_streamingGyro.value && _streamingLinAcc.value) {
            Log.e(TAG, "Gyroscope and LinAcc sensor is already streaming")
            return
        }

        mathFilter = MathFilter()
        GlobalScope.launch {
            _streamingGyro.value = true
            _streamingLinAcc.value = true
            var listOfMeasurements = emptyList<List<Measurement>>()
            while (_streamingGyro.value && _streamingLinAcc.value) {

                fetchAccStreamingData(deviceId)
                fetchGyroStreamData(deviceId)
                Log.d(TAG,"Fetched Acc Data=${_currentLinAcc}")
                Log.d(TAG,"Fetched Gyro Data=${_currentGyro}")

                _currentLinAccUI.update { _currentLinAcc?.let { it1 ->
                    Log.d("CALCULATING_POLAR","Calculated Acc=$it1")
                    mathFilter.calculateAngles(
                        it1
                    )
                } }
                Log.d("POLAR_ACC","polar acc=${_currentLinAccUI.value}")
                _currentGyroUI.update { _currentLinAcc?.let { it1 ->
                    _currentGyro?.let { it2 ->
                        Log.d("CALCULATING_POLAR","Calculated Gyro=$it2")
                        mathFilter.calculateAnglesWithGyro(
                            it1, it2
                        )
                    }
                } }
                Log.d("POLAR_GYRO","polar gyro=${_currentGyroUI.value}")//TODO HÄR HAR VI VÄRDET

                _currentMeasurements.addMeasurement(_currentLinAcc,"LinAcc","Polar")
                _currentMeasurements.addMeasurement(_currentGyro,"Gyro","Polar")
                Log.d("POLAR_GYRO","polar gyro=${_currentGyroUI.value}")
                //_measurementsUI.update { listOfMeasurements }
                delay(500)
            }

        }
    }


    @SuppressLint("CheckResult")
    private fun fetchGyroStreamData(deviceId:String){
        requestStreamSettings(deviceId, PolarBleApi.PolarDeviceDataType.GYRO).flatMap {
                settings: PolarSensorSetting ->
            api.startGyroStreaming(deviceId, settings)
        }.observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { polarGyroData: PolarGyroData ->
                    for (data in polarGyroData.samples) {
                        _currentGyro = Triple(data.x,data.y,data.z)
                        //Log.d(TAG, "Gyro    x: ${data.x} y: ${data.y} z: ${data.z} timeStamp: ${data.timeStamp}")
                    }
                },
                { error: Throwable ->
                    Log.e(TAG, "Gyro stream failed. Reason $error")
                },
                {
                    Log.d(TAG, "Gyro stream complete")
                }
            )
    }

    @SuppressLint("CheckResult")
    private fun fetchAccStreamingData(deviceId:String){
        requestStreamSettings(deviceId, PolarBleApi.PolarDeviceDataType.ACC).flatMap {
                settings: PolarSensorSetting ->
            api.startAccStreaming(deviceId, settings)
        }.observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { polarAccelerometerData: PolarAccelerometerData ->
                    for (data in polarAccelerometerData.samples) {
                        _currentLinAcc = Triple(data.x.toFloat()/4096,data.y.toFloat()/4096,data.z.toFloat()/4096)
                        //Log.d(TAG, "ACC    x: ${data.x} y: ${data.y} z: ${data.z} timeStamp: ${data.timeStamp}")
                    }
                },
                { error: Throwable ->
                    Log.e(TAG, "ACC stream failed. Reason $error")
                },
                {
                    Log.d(TAG, "ACC stream complete")
                }
            )
    }

    override fun stopGyroStream() {
        if (_streamingLinAcc.value) {
            GlobalScope.launch {
                measurementsRepo.saveMeasurementsToList(_currentMeasurements.value)
                Log.d("POLAR_SAVING","Saving polar measurements=${_currentMeasurements.value}") //TODO är alltid null
                _currentMeasurements.value = emptyList()
            }
            _streamingLinAcc.value = false
            _streamingGyro.value = false
        }
    }

    private fun requestStreamSettings(identifier: String, feature: PolarBleApi.PolarDeviceDataType): Flowable<PolarSensorSetting> {
        return Single.zip(
            api.requestStreamSettings(identifier, feature),
            api.requestFullStreamSettings(identifier, feature)
                .onErrorReturn {
                    Log.w(TAG, "Full stream settings are not available for feature $feature. REASON: $it")
                    PolarSensorSetting(emptyMap())
                },
            { available: PolarSensorSetting, all: PolarSensorSetting ->
                if (available.settings.isEmpty()) {
                    throw Throwable("Settings are not available")
                } else {
                    Log.d(TAG, "Feature $feature available settings ${available.settings}")
                    Log.d(TAG, "Feature $feature all settings ${all.settings}")
                    return@zip android.util.Pair(available, all)
                }
            }
        )
            .observeOn(AndroidSchedulers.mainThread())
            .toFlowable()
            .map { sensorSettings: android.util.Pair<PolarSensorSetting, PolarSensorSetting> ->
                return@map sensorSettings.first
            }
    }


    override fun startHrStreaming(deviceId: String) {
        val isDisposed = hrDisposable?.isDisposed ?: true
        if(isDisposed) {
            _measuring.update { true }
            hrDisposable = api.startHrStreaming(deviceId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { hrData: PolarHrData ->
                        for (sample in hrData.samples) {
                            _currentHR.update { sample.hr }
                            _hrList.update { hrList ->
                                hrList + sample.hr
                            }
                        }
                    },
                    { error: Throwable ->
                        Log.e(TAG, "Hr stream failed.\nReason $error")
                    },
                    { Log.d(TAG, "Hr stream complete")}
                )
        } else {
            Log.d(TAG, "Already streaming")
        }
    }

    override fun stopHrStreaming() {
        _measuring.update { false }
        hrDisposable?.dispose()
        _currentHR.update { null }
    }
}